package model.service;

import model.entity.Ingredient;
import model.entity.ShoppingList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dịch vụ xử lý các thao tác liên quan đến ShoppingList.
 * Bao gồm tạo, xoá, kiểm tra trùng ngày và thêm nguyên liệu.
 */
public class ShoppingListService extends BaseService implements IShoppingListService {

    /**
     * Tạo một shopping list mới trong cơ sở dữ liệu.
     *
     * @param list Đối tượng ShoppingList chứa ngày mua và groupId.
     * @return true nếu tạo thành công, ngược lại false.
     */
    @Override
    public boolean createShoppingList(ShoppingList list) {
        String sql = "INSERT INTO shoppingList (buyDate, groupId) VALUES (?, ?)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(list.getBuyDate()));
            ps.setInt(2, list.getGroupId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    list.setShoppingListId(rs.getInt(1)); // Gán ID được tạo ra
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo shopping list: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Xoá một shopping list theo ID.
     *
     * @param shoppingListId ID của shopping list cần xoá.
     * @return true nếu xoá thành công, ngược lại false.
     */
    @Override
    public boolean deleteShoppingList(int shoppingListId) {
        String sql = "DELETE FROM shoppingList WHERE shoppingListId = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shoppingListId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa shopping list: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Truy vấn danh sách shopping list theo groupId.
     *
     * @param groupId ID của nhóm.
     * @return Danh sách các ShoppingList tương ứng với groupId.
     */
    @Override
    public List<ShoppingList> getShoppingListsByGroupId(int groupId) {
        List<ShoppingList> list = new ArrayList<>();
        String sql = "SELECT * FROM shoppingList WHERE groupId = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ShoppingList s = new ShoppingList();
                s.setShoppingListId(rs.getInt("shoppingListId"));
                s.setBuyDate(rs.getDate("buyDate").toLocalDate());
                s.setGroupId(rs.getInt("groupId"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách mua sắm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return list;
    }

    /**
     * Thêm danh sách nguyên liệu vào shopping list cụ thể.
     *
     * @param ingredients    Danh sách nguyên liệu.
     * @param shoppingListId ID của shopping list.
     * @return true nếu thêm thành công, ngược lại false.
     */
    @Override
    public boolean addIngredientsToShoppingList(List<Ingredient> ingredients, int shoppingListId) {
        String sql = "INSERT INTO buy_ingredient (shoppingListId, ingredientName, quantity, unitType) VALUES (?, ?, ?, ?)";
        boolean success = true;

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            for (Ingredient ing : ingredients) {
                ps.setInt(1, shoppingListId);
                ps.setString(2, ing.getName());
                ps.setDouble(3, ing.getQuantity());
                ps.setString(4, ing.getUnit().toString().toLowerCase());
                ps.addBatch();
            }

            int[] result = ps.executeBatch();
            for (int r : result) {
                if (r == PreparedStatement.EXECUTE_FAILED) {
                    success = false;
                    break;
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm danh sách nguyên liệu vào shopping list: " + e.getMessage());
            success = false;
        } finally {
            closeConnection();
        }

        return success;
    }

    /**
     * Kiểm tra xem có bị trùng ngày mua trong cùng một nhóm hay không.
     *
     * @param date    Ngày cần kiểm tra.
     * @param groupId ID nhóm.
     * @return true nếu đã tồn tại ngày mua trong nhóm, ngược lại false.
     */
    public boolean isDateDuplicated(LocalDate date, int groupId) {
        String sql = "SELECT COUNT(*) FROM shoppinglist WHERE buyDate = ? AND groupId = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setInt(2, groupId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Tạo mới một shopping list theo groupId và thêm danh sách nguyên liệu vào đó.
     * Dùng ngày hiện tại làm ngày mua.
     *
     * @param groupId     ID của nhóm.
     * @param ingredients Danh sách nguyên liệu cần thêm.
     * @return true nếu thành công, ngược lại false.
     */
    public boolean addIngredientsToShoppingList(int groupId, List<Ingredient> ingredients) {
        ShoppingList list = new ShoppingList();
        list.setGroupId(groupId);
        list.setBuyDate(LocalDate.now());
        if (!createShoppingList(list)) {
            System.out.println("Không thể tạo shopping list mới.");
            return false;
        }
        String idSql = "SELECT MAX(shoppingListId) AS maxId FROM shoppingList";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(idSql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int shoppingListId = rs.getInt("maxId");
                return addIngredientsToShoppingList(ingredients, shoppingListId);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ID shopping list mới: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Lấy ID lớn nhất hiện có của shopping list.
     * Phục vụ mục đích tạo ID mới hoặc kiểm thử.
     *
     * @return ID lớn nhất, hoặc -1 nếu không tìm thấy.
     */
    public int getMaxShoppingListId() {
        String sql = "SELECT MAX(shoppingListId) AS maxId FROM shoppingList";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("maxId");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ID shopping list lớn nhất: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }
}
