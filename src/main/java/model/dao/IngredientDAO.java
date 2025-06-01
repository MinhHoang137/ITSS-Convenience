package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entity.Ingredient;
import model.entity.Unit;

public class IngredientDAO {

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
