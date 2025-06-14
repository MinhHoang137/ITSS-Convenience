package model.dao;

import model.entity.Dish;
import model.entity.Ingredient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FridgeDAOTest {
    FridgeDAO fridgeDAO = FridgeDAO.getInstance();
    MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();

    @Test
    void getAllIngredients() {
        // Test case to check if all ingredients are retrieved correctly
        int fridgeId = 1; // Example fridge ID
        List<Ingredient> ingredients = fridgeDAO.getAllIngredients(fridgeId);
        assertNotNull(ingredients, "Ingredients list should not be null");
        assertFalse(ingredients.isEmpty(), "Ingredients list should not be empty");
        for (Ingredient ingredient : ingredients) {
            assertNotNull(ingredient.getName(), "Ingredient name should not be null");
            assertTrue(ingredient.getQuantity() > 0, "Ingredient quantity should be greater than zero");
            assertNotNull(ingredient.getUnit(), "Ingredient unit should not be null");
        }
    }

    @Test
    void getExpiringIngredients() {
        // Test case to check if expiring ingredients are retrieved correctly
        int fridgeId = 1; // Example fridge ID
        int daysBeforeExpire = 3; // Example number of days before expiration
        List<Ingredient> expiringIngredients = fridgeDAO.getExpiringIngredients(fridgeId, daysBeforeExpire);
        assertNotNull(expiringIngredients, "Expiring ingredients list should not be null");
        assertFalse(expiringIngredients.isEmpty(), "Expiring ingredients list should not be empty");
        for (Ingredient ingredient : expiringIngredients) {
            assertNotNull(ingredient.getName(), "Ingredient name should not be null");
            assertTrue(ingredient.getQuantity() >= 0);
            assertNotNull(ingredient.getUnit(), "Ingredient unit should not be null");
        }
    }

    @Test
    void getFridgeIdByGroupId() {
        // Test case to check if fridge ID is retrieved correctly by group ID
        int groupId = 1; // Example group ID
        int fridgeId = fridgeDAO.getFridgeIdByGroupId(groupId);
        assertTrue(fridgeId > 0, "Fridge ID should be greater than zero");
    }

    @Test
    void consumeIngredientsForDish() {
        Dish cookableDish = mealPlanDAO.getCookableDishes(1).getFirst(); // Get a cookable dish
        ArrayList<Ingredient> ingredients = cookableDish.getIngredients();
        int fridgeId = 1; // Example fridge ID
//        System.out.println(cookableDish.getName());
//        System.out.println("Ingredients needed:");
//        for (Ingredient ingredient : ingredients) {
//            System.out.println(ingredient.getName() + " - " + ingredient.getQuantity() + " " + ingredient.getUnit());
//        }
        boolean result = fridgeDAO.consumeIngredientsForDish(cookableDish, fridgeId);
        assertTrue(result, "Should be able to consume ingredients for the dish");
        for (Ingredient ingredient : ingredients) {
           ingredient.setExpirationDate(LocalDate.of(2025, 12, 31)); // Set a future expiration date for testing
            fridgeDAO.addIngredientToFridge(ingredient, fridgeId); // Add ingredients back to fridge for next tests
        }
    }
}