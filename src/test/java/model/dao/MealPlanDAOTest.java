package model.dao;

import model.entity.*;
import model.service.meal_plan.IMealPlanGetService;
import model.service.meal_plan.MealPlanGetService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MealPlanDAOTest {
    MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();
    IMealPlanGetService mealPlanGetService = MealPlanGetService.getInstance();
    @Test
    void getDish() {
        // Test with a valid dish ID
        int validDishId = 2; // Assuming this ID exists in the database
        Dish dish = mealPlanDAO.getDish(validDishId);
        assertNotNull(dish, "Dish should not be null for a valid ID");
        assertEquals(validDishId, dish.getId(), "Dish ID should match the requested ID");
        String expectedName = "Phở bò"; // Assuming this is the expected name for ID 2
        expectedName = expectedName.toLowerCase(); // Normalize case for comparison
        assertEquals(expectedName, dish.getName().toLowerCase(), "Dish name should match the expected name");
        // Test with an invalid dish ID
        int invalidDishId = -1; // Assuming this ID does not exist in the database
        Dish invalidDish = mealPlanDAO.getDish(invalidDishId);
        assertNull(invalidDish, "Dish should be null for an invalid ID");
    }
    @Test
    void getDishLike() {
        String partialName = "Canh";
        partialName = partialName.toLowerCase(); // Normalize case for comparison
        ArrayList<Dish> dishes = mealPlanDAO.getDishLike(partialName);
        assertNotNull(dishes, "Dishes list should not be null");
        assertFalse(dishes.isEmpty(), "Dishes list should not be empty for a valid search term");
        for (Dish dish : dishes) {
            assertTrue(dish.getName().toLowerCase().contains(partialName), "Dish name should contain the search term");
        }
    }
    @Test
    void getAllDishes() {
        ArrayList<Dish> dishes = mealPlanDAO.getAllDishes();
        assertNotNull(dishes, "Dishes list should not be null");
        assertFalse(dishes.isEmpty(), "Dishes list should not be empty");
        for (Dish dish : dishes) {
            assertNotNull(dish.getName(), "Dish name should not be null");
            assertNotNull(dish.getDescription(), "Dish description should not be null");
            assertNotNull(dish.getIngredients(), "Dish ingredients should not be null");
        }
    }
    @Test
    void getCookableDishes() {
        int fridgeId = 1; // Assuming this fridge ID exists in the database
        ArrayList<Dish> cookableDishes = mealPlanDAO.getCookableDishes(fridgeId);
        assertNotNull(cookableDishes, "Cookable dishes list should not be null");
        assertFalse(cookableDishes.isEmpty(), "Cookable dishes list should not be empty for a valid fridge ID");
        IMealPlanGetService mealPlanGetService = MealPlanGetService.getInstance();
        for (Dish dish : cookableDishes) {
            boolean res = mealPlanGetService.canCookDish(dish.getId(), fridgeId);
            assertTrue(res, "Dish should be cookable with the available ingredients in the fridge");
        }
    }
    @Test
    void saveDish() {
        Ingredient ingredient1 = new Ingredient(0, "Rice", 1, Unit.kg);
        Ingredient ingredient2 = new Ingredient(0, "Chicken", 1, Unit.kg);
        Dish newDish = new Dish(0, "Test Dish", "This is a test dish", new ArrayList<>());
        newDish.setEatTime(MealType.breakfast);
        newDish.setEatDate(0); // Assuming 0 corresponds to the first day of the week
        newDish.getIngredients().add(ingredient1);
        System.out.println(newDish.getEatTime().toString());
        boolean isSaved = mealPlanDAO.saveDish(newDish);
        assertTrue(isSaved, "New dish should be saved successfully");
        int lastDishId = mealPlanGetService.getMaxDishId();
        assertTrue(lastDishId > 0, "Last dish ID should be greater than zero after saving a new dish");
        newDish.setId(lastDishId);
        // Test updating an existing dish
        newDish.getIngredients().add(ingredient2);
        newDish.setEatTime(MealType.lunch);
        isSaved = mealPlanDAO.saveDish(newDish);
        assertTrue(isSaved, "Existing dish should be updated successfully");

        // Test saving a dish with an existing name
        Dish duplicateDish = new Dish(0, "Test Dish", "This is a duplicate test dish", new ArrayList<>());
        isSaved = mealPlanDAO.saveDish(duplicateDish);
        assertFalse(isSaved, "Duplicate dish should not be saved");
    }
    @Test
    void getCookableDishesByDayIndex() {
        int fridgeId = 1; // Assuming this fridge ID exists in the database
        int dayIndex = 0; // Assuming 0 corresponds to the first day of the week
        ArrayList<Dish> cookableDishes = mealPlanDAO.getCookableDishesByDayIndex(fridgeId, dayIndex);
        assertNotNull(cookableDishes, "Cookable dishes list should not be null");
        assertFalse(cookableDishes.isEmpty(), "Cookable dishes list should not be empty for a valid fridge ID and day index");
        IMealPlanGetService mealPlanGetService = MealPlanGetService.getInstance();
        for (Dish dish : cookableDishes) {
            boolean res = mealPlanGetService.canCookDish(dish.getId(), fridgeId);
            assertTrue(res, "Dish should be cookable with the available ingredients in the fridge");
            assertEquals(dayIndex, dish.getEatDateIndex(), "Dish should match the specified day index");
        }
    }
    @Test
    void addMeal() {
        // Test adding a meal with valid data
        ArrayList<Dish> dishes = mealPlanDAO.getCookableDishesByDayIndex(1,0);
        MealType mealType = MealType.lunch;
        int dayIndex = 0; // Assuming 0 corresponds to the first day of the week
        Meal meal = new Meal(0,dayIndex, dishes, mealType);
        boolean isAdded = mealPlanDAO.addMeal(meal, 1); // Assuming groupId is 1
        assertTrue(isAdded, "Meal should be added successfully");
        int lastMealId = mealPlanGetService.getMaxMealId();
        assertTrue(lastMealId > 0, "Last meal ID should be greater than zero after adding a new meal");
    }
    @Test
    void deleteMeal() {
        int lastMealId = mealPlanGetService.getMaxMealId(); // Assuming this ID exists in the database
        assertTrue(lastMealId > 0, "There should be at least one meal to delete");
        boolean isDeleted = mealPlanDAO.deleteMeal(lastMealId);
        assertTrue(isDeleted, "Meal should be deleted successfully");
    }
    @Test
    void deleteDish() {
        int lastDishId = mealPlanGetService.getMaxDishId(); // Assuming this ID exists in the database
        assertTrue(lastDishId > 0, "There should be at least one dish to delete");
        boolean isDeleted = mealPlanDAO.deleteDish(lastDishId);
        assertTrue(isDeleted, "Dish should be deleted successfully");
    }
    @Test
    void getAllMeals() {
        int groupId = 1; // Assuming this group ID exists in the database
        ArrayList<Meal> meals = mealPlanDAO.getAllMeals(groupId);
        assertNotNull(meals, "Meals list should not be null");
        assertFalse(meals.isEmpty(), "Meals list should not be empty for a valid group ID");
        for (Meal meal : meals) {
            assertNotNull(meal.getDishList(), "Meal's dish list should not be null");
            assertFalse(meal.getDishList().isEmpty(), "Meal's dish list should not be empty");
            assertNotNull(meal.getMealType(), "Meal type should not be null");
            assertTrue(meal.getDateIndex() >= 0 && meal.getDateIndex() < 7, "Eat date index should be between 0 and 6");
        }
    }
    @Test
    void getMissingIngredients() {
        int fridgeId = 1; // Assuming this fridge ID exists in the database
        ArrayList<Ingredient> totalIngredients = new ArrayList<>();
        totalIngredients.add(new Ingredient(0, "Rice", 2, Unit.kg));
        totalIngredients.add(new Ingredient(0, "Chicken", 1, Unit.kg));
        ArrayList<Ingredient> missingIngredients = mealPlanDAO.getMissingIngredients(fridgeId, totalIngredients);
        assertNotNull(missingIngredients, "Missing ingredients list should not be null");
        assertFalse(missingIngredients.isEmpty(), "Missing ingredients list should not be empty for a valid fridge ID and total ingredients");
        for (Ingredient ingredient : missingIngredients) {
            assertTrue(ingredient.getQuantity() > 0, "Missing ingredient quantity should be greater than zero");
        }
    }
    @Test
    void canCookMeal() {
        int mealId = 15; // Assuming this meal ID exists in the database
        int fridgeId = 1; // Assuming this fridge ID exists in the database
        boolean canCook = mealPlanDAO.canCookMeal(mealId, fridgeId);
        assertTrue(canCook, "Meal should be cookable with the available ingredients in the fridge");
    }
}