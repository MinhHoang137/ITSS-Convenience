package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entity.Ingredient;
import model.entity.Unit;

/**
 * IngredientDAO là lớp chịu trách nhiệm thao tác với cơ sở dữ liệu
 * để truy xuất thông tin các nguyên liệu trong tủ lạnh.
 */
public class IngredientDAO {

    /**
     * Lấy danh sách các nguyên liệu trong tủ lạnh dựa trên fridgeId.
     *
     * @param fridgeId   ID của tủ lạnh cần lấy nguyên liệu.
     * @param connection Kết nối đến cơ sở dữ liệu đang sử dụng.
     * @return Danh sách các nguyên liệu thuộc tủ lạnh có ID tương ứng.
     */
    public List<Ingredient> getIngredientsByFridgeId(int fridgeId, Connection connection) {
        List<Ingredient> ingredients = new ArrayList<>();

        String query = "SELECT * FROM ingredient WHERE fridgeId = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, fridgeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("ingredientId"));
                ing.setName(rs.getString("ingredientName"));
                ing.setQuantity(rs.getDouble("quantity"));
                ing.setUnit(Unit.valueOf(rs.getString("unitType")));
                ing.setExpirationDate(rs.getDate("expirationDate").toLocalDate());
                ingredients.add(ing);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn nguyên liệu theo fridgeId:");
            e.printStackTrace();
        }

        return ingredients;
    }

}
