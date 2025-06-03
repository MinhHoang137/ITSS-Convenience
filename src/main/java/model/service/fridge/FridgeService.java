package model.service.fridge;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.entity.Dish;
import model.entity.Fridge;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.BaseService;

/**
 * Dịch vụ quản lý tủ lạnh, bao gồm thêm, truy vấn, sử dụng nguyên liệu,
 * cũng như nấu món ăn từ nguyên liệu có sẵn.
 */
public class FridgeService extends BaseService {

    /**
     * Thêm một nguyên liệu vào tủ lạnh (bao gồm cả ID nguyên liệu).
     *
     * @param ingredient nguyên liệu cần thêm
     * @param fridgeId   ID của tủ lạnh
     */
    public void addIngredient(Ingredient ingredient, int fridgeId) {
        getConnection();
        try {
            String sql = "INSERT INTO ingredient (ingredientName, quantity, unitType, ingredientId, expirationDate, fridgeId) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getQuantity());
            stmt.setString(3, ingredient.getUnit().toString());
            stmt.setInt(4, ingredient.getId());
            stmt.setDate(5, Date.valueOf(ingredient.getExpirationDate()));
            stmt.setInt(6, fridgeId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Lấy danh sách tất cả nguyên liệu đang có trong tủ lạnh (sau khi gộp theo tên và đơn vị tính).
     *
     * @param fridgeId ID của tủ lạnh
     * @return danh sách nguyên liệu
     */
    public List<Ingredient> getAllIngredients(int fridgeId) {
        getConnection();
        List<Ingredient> list = new ArrayList<>();
        try {
            String sql = """
                    SELECT ingredientName, unitType, SUM(quantity) as totalQuantity
                    FROM ingredient
                    WHERE fridgeId = ?
                    GROUP BY ingredientName, unitType
                    HAVING SUM(quantity) > 0
                """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(
                        -1,
                        rs.getString("ingredientName"),
                        rs.getDouble("totalQuantity"),
                        Unit.valueOf(rs.getString("unitType")),
                        null
                );
                list.add(ing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    /**
     * Nấu một món ăn bằng cách trừ nguyên liệu tương ứng trong tủ lạnh.
     *
     * @param dish     món ăn cần nấu
     * @param fridgeId ID của tủ lạnh
     * @return true nếu đủ nguyên liệu và trừ thành công; false nếu thiếu nguyên liệu
     */
    public boolean cookDish(Dish dish, int fridgeId) {
        for (Ingredient ing : dish.getIngredients()) {
            boolean ok = useIngredient(ing.getName(), ing.getQuantity(), ing.getUnit(), fridgeId);
            if (!ok) return false;
        }
        return true;
    }

    /**
     * Lấy danh sách nguyên liệu sắp hết hạn (trong khoảng n ngày tới).
     *
     * @param fridgeId          ID của tủ lạnh
     * @param daysBeforeExpire  số ngày trước khi hết hạn
     * @return danh sách nguyên liệu sắp hết hạn
     */
    public List<Ingredient> getExpiringIngredients(int fridgeId, int daysBeforeExpire) {
        getConnection();
        List<Ingredient> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate upper = now.plusDays(daysBeforeExpire);

        try {
            String sql = "SELECT * FROM ingredient WHERE fridgeId = ? AND expirationDate <= ? AND expirationDate >= ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fridgeId);
            stmt.setDate(2, Date.valueOf(upper));
            stmt.setDate(3, Date.valueOf(now));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(
                        rs.getInt("ingredientId"),
                        rs.getString("ingredientName"),
                        rs.getDouble("quantity"),
                        Unit.valueOf(rs.getString("unitType")),
                        rs.getDate("expirationDate").toLocalDate());
                list.add(ing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    /**
     * Trừ một lượng nguyên liệu cụ thể trong tủ lạnh nếu đủ số lượng.
     *
     * @param name         tên nguyên liệu
     * @param usedQuantity số lượng cần dùng
     * @param unit         đơn vị
     * @param fridgeId     ID của tủ lạnh
     * @return true nếu đủ và trừ được, false nếu không đủ
     */
    public boolean useIngredient(String name, double usedQuantity, Unit unit, int fridgeId) {
        getConnection();
        try {
            String sql = "SELECT * FROM ingredient WHERE ingredientName = ? AND unitType = ? AND fridgeId = ?";
            PreparedStatement stmt = connection.prepareStatement(
                    sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, name);
            stmt.setString(2, unit.toString());
            stmt.setInt(3, fridgeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double available = rs.getDouble("quantity");
                if (available >= usedQuantity) {
                    rs.updateDouble("quantity", available - usedQuantity);
                    rs.updateRow();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Thêm một nguyên liệu mới vào tủ lạnh (không cần ID).
     *
     * @param ingredient nguyên liệu
     * @param fridgeId   ID của tủ lạnh
     */
    public void addIngredientToFridge(Ingredient ingredient, int fridgeId) {
        getConnection();
        try {
            String sql = """
                        INSERT INTO ingredient (ingredientName, quantity, unitType, expirationDate, fridgeId)
                        VALUES (?, ?, ?, ?, ?)
                    """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getQuantity());
            stmt.setString(3, ingredient.getUnit().toString());
            stmt.setDate(4, Date.valueOf(ingredient.getExpirationDate()));
            stmt.setInt(5, fridgeId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Truy xuất ID tủ lạnh theo groupId của người dùng.
     *
     * @param groupId ID nhóm người dùng
     * @return ID tủ lạnh tương ứng; -1 nếu không tìm thấy
     */
    public int getFridgeIdByGroupId(int groupId) {
        getConnection();
        int fridgeId = -1;
        try {
            String sql = "SELECT fridgeId FROM fridge WHERE groupId = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fridgeId = rs.getInt("fridgeId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return fridgeId;
    }

    /**
     * Lấy toàn bộ danh sách các tủ lạnh trong hệ thống.
     *
     * @return danh sách các tủ lạnh
     */
    public List<Fridge> getAllFridges() {
        getConnection();
        List<Fridge> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fridge";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Fridge fridge = new Fridge();
                fridge.setId(rs.getInt("fridgeId"));
                fridge.setUserGroupId(rs.getInt("groupId"));
                list.add(fridge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    /**
     * Tiêu thụ nguyên liệu cần thiết để nấu một món ăn (giống cookDish).
     *
     * @param dish     món ăn
     * @param fridgeId ID tủ lạnh
     * @return true nếu đủ nguyên liệu và trừ được; false nếu thiếu
     */
    public boolean consumeIngredientsForDish(Dish dish, int fridgeId) {
        getConnection();
        try {
            for (Ingredient i : dish.getIngredients()) {
                if (!useIngredient(i.getName(), i.getQuantity(), i.getUnit(), fridgeId)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
}
