package model.service.meal_plan;

import model.service.BaseService;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MealDeleteService extends BaseService implements IMealDeleteService {
    private static MealDeleteService instance;
    private MealDeleteService() {
        // Private constructor to prevent instantiation
    }
    public static MealDeleteService getInstance() {
        if (instance == null) {
            instance = new MealDeleteService();
        }
        return instance;
    }
    public boolean deleteMeal(int id) {
        getConnection();
        boolean success = false;

        String deleteLinksQuery = "DELETE FROM meal_has_dish WHERE mealId = ?";
        String deleteMealQuery = "DELETE FROM meal_plan WHERE id = ?";

        try (
                PreparedStatement deleteLinksStmt = connection.prepareStatement(deleteLinksQuery);
                PreparedStatement deleteMealStmt = connection.prepareStatement(deleteMealQuery)
        ) {
            // Xóa liên kết món ăn trong meal_has_dish
            deleteLinksStmt.setInt(1, id);
            deleteLinksStmt.executeUpdate();

            // Xóa meal chính
            deleteMealStmt.setInt(1, id);
            int rowsAffected = deleteMealStmt.executeUpdate();

            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in deleteMeal: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return success;
    }
    public boolean deleteDish(int dishId){
        getConnection();
        boolean success = false;

        String deleteMealDishQuery = "DELETE FROM meal_has_dish WHERE dishId = ?";
        String deleteDishIngredientQuery = "DELETE FROM dish_use_ingredient WHERE dishId = ?";
        String deleteDishQuery = "DELETE FROM dish WHERE dishId = ?";
        try (
                PreparedStatement deleteMealDishStmt = connection.prepareStatement(deleteMealDishQuery);
                PreparedStatement deleteDishIngredientStmt = connection.prepareStatement(deleteDishIngredientQuery);
                PreparedStatement deleteDishStmt = connection.prepareStatement(deleteDishQuery)
        ) {
            // Xóa liên kết món ăn trong meal_has_dish
            deleteMealDishStmt.setInt(1, dishId);
            deleteMealDishStmt.executeUpdate();

            // Xóa liên kết nguyên liệu trong dish_has_ingredient
            deleteDishIngredientStmt.setInt(1, dishId);
            deleteDishIngredientStmt.executeUpdate();

            // Xóa món ăn chính
            deleteDishStmt.setInt(1, dishId);
            int rowsAffected = deleteDishStmt.executeUpdate();

            success = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in deleteDish: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return success;
    }
}
