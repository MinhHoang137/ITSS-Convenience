package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.MealType;
import model.entity.Unit;

public class DishDAO {

    public List<Dish> getAllDishesWithIngredients(Connection conn) {
        Map<Integer, Dish> dishMap = new LinkedHashMap<>(); // đảm bảo thứ tự giữ nguyên

        String query = """
            SELECT d.dishId, d.dishName, d.instruction, d.eatTime, d.eatDate,
                   di.ingredientName, di.quantity, di.unitType
            FROM dish d
            JOIN dish_use_ingredient di ON d.dishId = di.dishId
            ORDER BY d.dishId
            """;

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int dishId = rs.getInt("dishId");

                // Nếu món chưa có trong map, tạo mới
                Dish dish = dishMap.get(dishId);
                if (dish == null) {
                    dish = new Dish(
                            dishId,
                            rs.getString("dishName"),
                            rs.getString("instruction"),
                            new ArrayList<>(),
                            MealType.valueOf(rs.getString("eatTime")),
                            rs.getInt("eatDate")
                    );
                    dishMap.put(dishId, dish);
                }

                // Thêm nguyên liệu cho món ăn
                Ingredient ingredient = new Ingredient();
                ingredient.setName(rs.getString("ingredientName"));
                ingredient.setQuantity(rs.getDouble("quantity"));
                ingredient.setUnit(Unit.valueOf(rs.getString("unitType")));

                dish.getIngredients().add(ingredient);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn dish và nguyên liệu:");
            e.printStackTrace();
        }

        return new ArrayList<>(dishMap.values());
    }
}
