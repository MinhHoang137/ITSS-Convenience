package model.service;

import model.entity.Ingredient;
import model.entity.ShoppingList;
import model.entity.Unit;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IShoppingListServiceTest {
    IShoppingListService shoppingListService = new ShoppingListService();
    private final int groupId = 1; // Example group ID for testing
    @Test
    void createShoppingList() {
        int maxId = shoppingListService.getMaxShoppingListId();
        ShoppingList shoppingList = new ShoppingList(maxId + 1, LocalDate.now(), groupId);
        boolean result = shoppingListService.createShoppingList(shoppingList);
        assertTrue(result, "Shopping list should be created successfully");
    }

    @Test
    void getShoppingListsByGroupId() {
        int groupId = 1; // Example group ID for testing
        var shoppingLists = shoppingListService.getShoppingListsByGroupId(groupId);
        assertNotNull(shoppingLists, "Shopping lists should not be null");
        assertFalse(shoppingLists.isEmpty(), "Shopping lists should not be empty for a valid group ID");
        for (ShoppingList list : shoppingLists) {
            assertEquals(groupId, list.getGroupId(), "Group ID should match the requested group ID");
        }
    }

    @Test
    void addIngredientsToShoppingList() {
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        Ingredient ingredient = new Ingredient(0,"TestIngredient", 2, Unit.kg);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        boolean result = shoppingListService.addIngredientsToShoppingList(ingredients, shoppingListId);
        assertTrue(result, "Ingredient should be added successfully to the shopping list");
    }

    @Test
    void isDateDuplicated() {
        LocalDate date = LocalDate.now().plusDays(1);
        int groupId = 1; // Example group ID for testing
        boolean isDuplicated = shoppingListService.isDateDuplicated(date, groupId);
        assertFalse(isDuplicated, "Date should not be duplicated for a new shopping list");

        // Create a shopping list to test duplication
        ShoppingList shoppingList = new ShoppingList(0, date, groupId);
        shoppingListService.createShoppingList(shoppingList);

        // Check again for duplication
        isDuplicated = shoppingListService.isDateDuplicated(date, groupId);
        assertTrue(isDuplicated, "Date should be duplicated after creating a shopping list with the same date");
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        shoppingListService.deleteShoppingList(shoppingListId); // Clean up after test
    }

    @Test
    void testAddIngredientsToShoppingListWithGroupID() {
        Ingredient ingredient1 = new Ingredient(0, "Test Ingredient 1", 1, Unit.kg);
        Ingredient ingredient2 = new Ingredient(0, "Test Ingredient 2", 2, Unit.l);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        boolean result = shoppingListService.addIngredientsToShoppingList(groupId, ingredients);
        assertTrue(result, "Ingredients should be added successfully to the shopping list");
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        shoppingListService.deleteShoppingList(shoppingListId);
    }

    @Test
    void deleteShoppingList() {
        int shoppingListId = shoppingListService.getMaxShoppingListId();
        boolean result = shoppingListService.deleteShoppingList(shoppingListId);
        assertTrue(result, "Shopping list should be deleted successfully");
    }
}