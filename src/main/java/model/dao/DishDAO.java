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
        Map<Integer, Dish> dishMap = new LinkedHashMap<>();

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

                // Tạo Dish nếu chưa có
                Dish dish = dishMap.get(dishId);
                if (dish == null) {
                    String eatTimeStr = rs.getString("eatTime").trim().toLowerCase();
                    MealType mealType = null;
                    for (MealType mt : MealType.values()) {
                        if (mt.getDisplayName().equalsIgnoreCase(eatTimeStr)) {
                            mealType = mt;
                            break;
                        }
                    }

                    if (mealType == null) {
                        System.err.println("⚠️ Không tìm thấy MealType tương ứng: " + eatTimeStr);
                        continue;
                    }

                    dish = new Dish(
                            dishId,
                            rs.getString("dishName"),
                            rs.getString("instruction"), // 🟢 Gán vào description
                            new ArrayList<>(),
                            mealType,
                            rs.getInt("eatDate")
                    );
                    dishMap.put(dishId, dish);
                }

                // Thêm nguyên liệu
                String unitStr = rs.getString("unitType").trim().toLowerCase();
                Unit matchedUnit = null;
                for (Unit u : Unit.values()) {
                    if (u.name().equalsIgnoreCase(unitStr) || u.toString().equalsIgnoreCase(unitStr)) {
                        matchedUnit = u;
                        break;
                    }
                }

                if (matchedUnit == null) {
                    System.err.println("⚠️ Không tìm thấy Unit tương ứng: " + unitStr);
                    continue;
                }

                Ingredient ingredient = new Ingredient();
                ingredient.setName(rs.getString("ingredientName"));
                ingredient.setQuantity(rs.getDouble("quantity"));
                ingredient.setUnit(matchedUnit);

                dish.getIngredients().add(ingredient);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn dish và nguyên liệu:");
            e.printStackTrace();
        }

        return new ArrayList<>(dishMap.values());
    }
}
