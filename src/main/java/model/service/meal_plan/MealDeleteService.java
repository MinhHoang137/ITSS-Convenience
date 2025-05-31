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

}
