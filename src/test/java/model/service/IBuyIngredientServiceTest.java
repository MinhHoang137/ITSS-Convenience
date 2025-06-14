package model.service;

import model.entity.BuyIngredient;
import model.entity.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IBuyIngredientServiceTest {
    IBuyIngredientService buyIngredientService = new BuyIngredientService();
    IShoppingListService shoppingListService = new ShoppingListService();
    String ingredientName = "Test Ingredient";
    int quantity = 2; // Example quantity
    String unit = Unit.kg.toString();

    @Test
    void addIngredientToList() {
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        BuyIngredient buyIngredient = new BuyIngredient(shoppingListId, ingredientName, quantity, unit);
        boolean result = buyIngredientService.addIngredientToList(buyIngredient);
        assertTrue(result, "Ingredient should be added successfully to the shopping list");
        // Delete the ingredient after test
        //buyIngredientService.removeIngredientFromList(shoppingListId, ingredientName);
    }

    @Test
    void removeIngredientFromList() {
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        BuyIngredient buyIngredient = new BuyIngredient(shoppingListId, ingredientName, quantity, unit);
        buyIngredientService.addIngredientToList(buyIngredient); // Ensure the ingredient is added first
        boolean result = buyIngredientService.removeIngredientFromList(shoppingListId, ingredientName);
        assertTrue(result, "Ingredient should be removed successfully from the shopping list");
    }

    @Test
    void getIngredientsByShoppingListId() {
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        BuyIngredient retrievedIngredient = buyIngredientService.getIngredientsByShoppingListId(shoppingListId).getFirst();
        assertNotNull(retrievedIngredient, "Retrieved ingredient should not be null");
    }
}