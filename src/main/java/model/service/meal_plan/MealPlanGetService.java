/**
 * MealPlanService.java
 * This class provides methods to manage meal plans, including retrieving dishes,
 * checking ingredient availability, and filtering cookable dishes based on fridge contents.
 * It extends BaseService to utilize common database connection methods.
 *  @author Minh Hoàng
 */

package model.service.meal_plan;

import model.entity.*;
import model.service.BaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MealPlanGetService extends BaseService implements IMealPlanGetService {
    private static MealPlanGetService instance;
    /**
     * Singleton pattern to ensure only one instance of MealPlanService exists.
     * @return The single instance of MealPlanService.
     */
    public static MealPlanGetService getInstance() {
        if (instance == null) {
            instance = new MealPlanGetService();
        }
        return instance;
    }
    // Read
    /**
     * Retrieves a Dish by its ID, including its ingredients and meal type.
     * @param id The ID of the dish to retrieve.
     * @return A Dish object containing the dish details, or null if not found.
     */
    public Dish getDish(int id) {
        getConnection();
        Dish dish = null;
        String dishQuery = "SELECT dishName, instruction, eatTime, eatDate FROM dish WHERE dishId = ?";
        String ingredientQuery = "SELECT ingredientName, quantity, unitType FROM dish_use_ingredient WHERE dishId = ?";

        try (PreparedStatement dishStmt = connection.prepareStatement(dishQuery);
             PreparedStatement ingredientStmt = connection.prepareStatement(ingredientQuery)) {

            dishStmt.setInt(1, id);
            ResultSet dishRs = dishStmt.executeQuery();
            if (dishRs.next()) {
                String name = dishRs.getString("dishName");
                String description = dishRs.getString("instruction");
                String eatTimeStr = dishRs.getString("eatTime");
                int eatDate = dishRs.getInt("eatDate");

                // Chuyển đổi eatTime sang Enum MealType
                MealType eatTime = MealType.valueOf(eatTimeStr.toLowerCase());

                ingredientStmt.setInt(1, id);
                ResultSet ingRs = ingredientStmt.executeQuery();
                ArrayList<Ingredient> ingredients = new ArrayList<>();

                while (ingRs.next()) {
                    String ingName = ingRs.getString("ingredientName");
                    double quantity = ingRs.getDouble("quantity");
                    String unitStr = ingRs.getString("unitType");

                    Unit unit = Unit.valueOf(unitStr.toLowerCase());
                    Ingredient ingredient = new Ingredient(0, ingName, quantity, unit);
                    ingredients.add(ingredient);
                }

                dish = new Dish(id, name, description, ingredients, eatTime, eatDate);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnection();
        }

        return dish;
    }

    /**
     * Retrieves a Dish by its name, including its ingredients and meal type.
     * @param dishName The name of the dish to retrieve.
     * @return A Dish object containing the dish details, or null if not found.
     */
    public ArrayList<Dish> getDishLike(String dishName) {
        ArrayList<Dish> dishList = new ArrayList<>();
        getConnection();

        String query = "SELECT dishId FROM dish WHERE dishName LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + dishName + "%"); // tìm gần đúng
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int dishId = rs.getInt("dishId");
                Dish dish = getDish(dishId); // dùng lại hàm đã có
                if (dish != null) {
                    dishList.add(dish);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi truy vấn LIKE: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return dishList;
    }


    /**
     * Retrieves all dishes from the database, including their ingredients.
     * @return A list of Dish objects containing all dishes and their ingredients.
     */
    public ArrayList<Dish> getAllDishes() {
        getConnection();
        ArrayList<Dish> dishes = new ArrayList<>();
        String query = "SELECT dishId, dishName, instruction FROM dish";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("dishId");

                // Lấy nguyên liệu cho từng món ăn
                Dish dish = getDish(id);
                if (dish != null) {
                    dishes.add(dish);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            closeConnection(); // Đóng kết nối sau khi truy vấn xong
        }
        return dishes;
    }
    public boolean isDishExists(String dishName) {
        getConnection();
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM dish WHERE dishName = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dishName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking if dish exists: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return exists;
    }

    /**
     * Retrieves the total quantity of a specific ingredient in a fridge.
     * @param ingredientName The name of the ingredient to check.
     * @param unit The unit of measurement for the ingredient.
     * @param fridgeId The ID of the fridge to check.
     * @return The total quantity of the ingredient in the specified fridge.
     */
    public double getTotalQuantityOfIngredient(String ingredientName, Unit unit, int fridgeId) {
        getConnection();
        double totalQuantity = 0.0;
        String query = "SELECT SUM(quantity) AS total " +
                "FROM ingredient " +
                "WHERE ingredientName = ? " +
                "AND unitType = ? " +
                "AND fridgeId = ? " +
                "AND expirationDate > CURRENT_DATE";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ingredientName);
            stmt.setString(2, unit.toString()); // Unit enum → String (e.g. "kg")
            stmt.setInt(3, fridgeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalQuantity = rs.getDouble("total");
                    if (rs.wasNull()) {
                        totalQuantity = 0.0;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting total quantity: " + e.getMessage());
        }
        finally {
            closeConnection(); // Đóng kết nối sau khi truy vấn xong
        }

        return totalQuantity;
    }

    /**
     * Checks if a dish can be cooked with the available ingredients in a fridge.
     * @param dishId The ID of the dish to check.
     * @param fridgeId The ID of the fridge to check ingredients against.
     * @return True if all ingredients are available, false otherwise.
     */
    public boolean canCookDish(int dishId, int fridgeId) {
        Dish dish = getDish(dishId);
        if (dish == null) {
            System.out.println("Món ăn không tồn tại.");
            return false;
        }
        for (Ingredient ingredient : dish.getIngredients()) {
            double totalQuantity = getTotalQuantityOfIngredient(ingredient.getName(), ingredient.getUnit(), fridgeId);
            if (totalQuantity < ingredient.getQuantity()) {
//                System.out.println("Không đủ nguyên liệu: " + ingredient.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a list of dishes that can be cooked with the ingredients available in a fridge.
     * @param fridgeId The ID of the fridge to check ingredients against.
     * @return A list of Dish objects that can be cooked.
     */
    public ArrayList<Dish> getCookableDishes(int fridgeId) {
        ArrayList<Dish> cookableDishes = new ArrayList<>();
        ArrayList<Dish> allDishes = getAllDishes();

        for (Dish dish : allDishes) {
            if (canCookDish(dish.getId(), fridgeId)) {
                cookableDishes.add(dish);
            }
        }

        return cookableDishes;
    }
      
    /**
     * Retrieves a list of cookable dishes for a specific day index.
     * @param fridgeId The ID of the fridge to check ingredients against.
     * @param dayIndex The index of the day (0-6) to filter dishes by their eat date.
     * @return A list of Dish objects that can be cooked on the specified day.
     */
    public ArrayList<Dish> getCookableDishesByDayIndex(int fridgeId, int dayIndex) {
        ArrayList<Dish> cookableDishes = getCookableDishes(fridgeId);
        ArrayList<Dish> filteredDishes = new ArrayList<>();
        for (Dish dish : cookableDishes) {
            if (dish.getEatDateIndex() == dayIndex) {
                filteredDishes.add(dish);
            }
        }
        return filteredDishes;
    }

    public Meal getMeal(int mealId, int groupId) {
        getConnection();
        Meal meal = null;

        String mealQuery = "SELECT * FROM meal_plan WHERE id = ? AND groupId = ?";
        String dishIdQuery = "SELECT dishId FROM meal_has_dish WHERE mealId = ?";

        try (
                PreparedStatement mealStmt = connection.prepareStatement(mealQuery);
                PreparedStatement dishIdStmt = connection.prepareStatement(dishIdQuery)
        ) {
            mealStmt.setInt(1, mealId);
            mealStmt.setInt(2, groupId);
            ResultSet mealRs = mealStmt.executeQuery();

            if (mealRs.next()) {
                int id = mealRs.getInt("id");
                int dateIndex = mealRs.getInt("eatDate");
                MealType type = MealType.valueOf(mealRs.getString("eatTime").toLowerCase());

                meal = new Meal();
                meal.setId(id);
                meal.setDateIndex(dateIndex);
                meal.setMealType(type);
                meal.setDishList(new ArrayList<>());

                dishIdStmt.setInt(1, mealId);
                ResultSet dishRs = dishIdStmt.executeQuery();
                while (dishRs.next()) {
                    int dishId = dishRs.getInt("dishId");
                    Dish dish = getDish(dishId);
                    if (dish != null) {
                        meal.getDishList().add(dish);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error in getMeal: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return meal;
    }

    public boolean canCookMeal(int mealId, int fridgeId) {
        Meal meal = getMeal(mealId, fridgeId);
        if (meal == null) {
            System.out.println("Bữa ăn không tồn tại.");
            return false;
        }
        for (Dish dish : meal.getDishList()) {
            if (!canCookDish(dish.getId(), fridgeId)) {
                System.out.println("Không đủ nguyên liệu để nấu món: " + dish.getName());
                return false;
            }
        }
        return true;
    }


    public ArrayList<Meal> getAllMeals(int groupId) {
        getConnection();
        ArrayList<Meal> mealList = new ArrayList<>();
        String query = "SELECT id FROM meal_plan WHERE groupId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int mealId = rs.getInt("id");
                Meal meal = getMeal(mealId, groupId);
                if (meal != null) {
                    mealList.add(meal);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error in getAllMeals: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return mealList;
    }
    public ArrayList<Ingredient> getMissingIngredients(int fridgeId, ArrayList<Ingredient> totalIngredients) {
        ArrayList<Ingredient> missingList = new ArrayList<>();

        for (Ingredient required : totalIngredients) {
            double availableQty = getTotalQuantityOfIngredient(required.getName(), required.getUnit(), fridgeId);

            if (availableQty < required.getQuantity()) {
                double missingQty = required.getQuantity() - availableQty;

                // Tạo bản sao với số lượng thiếu
                Ingredient missing = new Ingredient(
                        0,
                        required.getName(),
                        missingQty,
                        required.getUnit()
                );
                missingList.add(missing);
            }
        }

        return missingList;
    }

    /**
     * Retrieves the maximum dish ID from the dish table.
     * Use for generating new dish IDs and testing purposes.
     * @return The maximum dish ID, or -1 if no dishes exist.
     */
    public int getMaxDishId() {
        getConnection();
        int maxId = -1;
        String query = "SELECT MAX(dishId) AS maxId FROM dish";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }

        } catch (SQLException e) {
            System.out.println("Error in getMaxDishId: " + e.getMessage());
        } finally {
            closeConnection();
        }

        return maxId;
    }
    /**
     * Retrieves the maximum meal ID from the meal_plan table.
     * Use for generating new meal IDs and testing purposes.
     * @return The maximum meal ID, or -1 if no meals exist.
     */
    public int getMaxMealId() {
        getConnection();
        int maxId = -1;
        String query = "SELECT MAX(id) AS maxId FROM meal_plan";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }
        } catch (SQLException e) {
            System.out.println("Error in getMaxMealId: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return maxId;
    }



    public static void main(String[] args) {
        MealPlanGetService mealPlanGetService = new MealPlanGetService();
        mealPlanGetService.getConnection(); // Mở kết nối đến CSDL
        ArrayList<Dish> dishes = mealPlanGetService.getDishLike("Bánh");
        for (Dish dish : dishes) {
            System.out.println("Dish ID: " + dish.getId() + ", Name: " + dish.getName());
            for (Ingredient ingredient : dish.getIngredients()) {
                System.out.println("  Ingredient: " + ingredient.getName() + ", Quantity: " + ingredient.getQuantity() + " " + ingredient.getUnit());
            }
        }
    }
}
