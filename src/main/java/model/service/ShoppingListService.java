package model.service;

import model.entity.Ingredient;
import model.entity.ShoppingList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListService extends BaseService implements IShoppingListService {

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
                    list.setShoppingListId(rs.getInt(1)); // Cập nhật ID được tạo ra
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


}