package model.service.meal_plan;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.Meal;
import model.service.BaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MealPlanCreateService extends BaseService implements IMealPlanCreateService {
    private static MealPlanCreateService instance;
    private MealPlanCreateService() {
        // Private constructor to prevent instantiation
    }
    public static MealPlanCreateService getInstance() {
        if (instance == null) {
            instance = new MealPlanCreateService();
        }
        return instance;
    }
    /**
     * Adds a new dish to dish table of database.
     * @param dish
     * @return
     */
    public boolean addToDishTable(Dish dish) {
        getConnection();
        String query = "INSERT INTO dish (dishName, instruction, eatTime, eatDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDescription());
            stmt.setString(3, dish.getEatTime().toString());
            stmt.setInt(4, dish.getEatDateIndex());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding dish: " + e.getMessage());
            return false;
        } finally {
            closeConnection();
        }
    }

    /**
     * Retrieves the last inserted dish ID from the database.
     * @return The last inserted dish ID, or -1 if not found.
     */
    public int getLastInsertedDishId() {
        getConnection();
        String query = "Select MAX(dishId) AS lastId FROM dish";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("lastId");
            }
        } catch (SQLException e) {
            System.out.println("Error getting last inserted dish ID: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return -1; // Trả về -1 nếu không tìm thấy ID
    }

    /**
     * Adds a new ingredient to the dish in the database.
     * @param dishId The ID of the dish to which the ingredient will be added.
     * @param ingredient The ingredient to add.
     * @return True if the ingredient was added successfully, false otherwise.
     */
    public boolean addIngredientToDish(int dishId, Ingredient ingredient) {
        getConnection();
        String query = "INSERT INTO dish_use_ingredient (dishId, ingredientName, quantity, unitType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, dishId);
            stmt.setString(2, ingredient.getName());
            stmt.setDouble(3, ingredient.getQuantity());
            stmt.setString(4, ingredient.getUnit().toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding ingredient: " + e.getMessage());
            return false;
        } finally {
            closeConnection();
        }
    }
    /**
     * Adds a new dish with its ingredients to the database.
     * @param dish The Dish object containing the dish details and ingredients.
     * @return True if the dish was added successfully, false otherwise.
     */
    public boolean addDish(Dish dish) {
        if (!addToDishTable(dish)){
            System.out.println("Không thể thêm món ăn vào bảng dish.");
            return false;
        }
        int id = getDish(dish.getName()).getId();
        if (id == -1) {
            System.out.println("Không thể lấy ID của món ăn vừa thêm.");
            return false;
        }
        for (Ingredient ingredient : dish.getIngredients()) {
            if (!addIngredientToDish(id, ingredient)) {
                System.out.println("Không thể thêm nguyên liệu " + ingredient.getName() + " vào món ăn.");
                return false;
            }
        }
        return true;
    }
    private Dish getDish(String name) {
        IMealPlanGetService mealPlanGetService = MealPlanGetService.getInstance();
        return mealPlanGetService.getDish(name);
    }

    public boolean addMealToTable(Meal meal) {
        getConnection();
        boolean success = false;

        String insertQuery = "INSERT INTO meal_plan (eatTime, eatDate) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, meal.getMealType().toString().toLowerCase()); // Đưa về lowercase cho phù hợp với ENUM trong DB
            stmt.setInt(2, meal.getDateIndex());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Lấy id được tự động sinh và gán vào đối tượng meal
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    meal.setId(generatedId);
                }
                success = true;
            }

        } catch (SQLException e) {
            System.out.println("Error in addMealToTable: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return success;
    }
    public boolean addMealDishRelations(Meal meal) {
        getConnection();
        boolean success = false;

        String insertQuery = "INSERT INTO meal_has_dish (mealId, dishId) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            for (Dish dish : meal.getDishList()) {
                stmt.setInt(1, meal.getId());
                stmt.setInt(2, dish.getId());
                stmt.addBatch(); // thêm vào batch
            }

            int[] result = stmt.executeBatch(); // thực thi tất cả insert
            success = result.length == meal.getDishList().size();

        } catch (SQLException e) {
            System.out.println("Error in addMealDishRelations: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return success;
    }

    public boolean addMeal(Meal meal) {
        if (!addMealToTable(meal)) {
            System.out.println("Không thể thêm bữa ăn vào bảng meal_plan.");
            return false;
        }
        if (!addMealDishRelations(meal)) {
            System.out.println("Không thể thêm quan hệ giữa bữa ăn và món ăn.");
            return false;
        }
        return true;
    }
}
