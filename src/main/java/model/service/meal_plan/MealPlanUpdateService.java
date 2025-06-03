package model.service.meal_plan;

import model.entity.Dish;
import model.entity.Ingredient;
import model.service.BaseService;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class MealPlanUpdateService extends BaseService implements IMealPlanUpdateService {
    private static MealPlanUpdateService instance;
    private IMealPlanCreateService mealPlanCreateService = MealPlanCreateService.getInstance();

    private MealPlanUpdateService() {
        // Private constructor to prevent instantiation
    }
    public static MealPlanUpdateService getInstance() {
        if (instance == null) {
            instance = new MealPlanUpdateService();
        }
        return instance;
    }
    public boolean updateDish(Dish dish) {
        getConnection();
        boolean success = false;
        String updateDishTableQuery = """
                UPDATE dish
                SET dishName = ?,
                    instruction = ?,
                    eatTime = ?,
                    eatDate = ?
                WHERE dishId = ?
                """;
        try (PreparedStatement updateDishStmt = connection.prepareStatement(updateDishTableQuery)) {
            updateDishStmt.setString(1, dish.getName());
            updateDishStmt.setString(2, dish.getDescription());
            updateDishStmt.setString(3, dish.getEatTime().toString());
            updateDishStmt.setInt(4, dish.getEatDateIndex());
            updateDishStmt.setInt(5, dish.getId());

            int rowsAffected = updateDishStmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (Exception e) {
            System.out.println("Error in updateDish: " + e.getMessage());
        }
        String deleteIngredientsQuery = "DELETE FROM dish_use_ingredient WHERE dishId = ?";
        try (PreparedStatement deleteIngredientsStmt = connection.prepareStatement(deleteIngredientsQuery)) {
            deleteIngredientsStmt.setInt(1, dish.getId());
            deleteIngredientsStmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error in deleting ingredients: " + e.getMessage());
        }
        ArrayList<Ingredient> ingredients = dish.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
                mealPlanCreateService.addIngredientToDish(dish.getId(), ingredient);
            }
        }
        closeConnection();
        return success;
    }
}
