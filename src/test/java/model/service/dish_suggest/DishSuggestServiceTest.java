package model.service.dish_suggest;

import model.entity.Dish;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DishSuggestServiceTest {
    DishSuggestService dishSuggestService = new DishSuggestService();
    @Test
    public void suggestDishesFromFridge() {
        int fridgeId = 1;
        List<Dish> suggestedDishes = dishSuggestService.suggestDishesFromFridge(fridgeId);
        assertNotNull(suggestedDishes, "Suggested dishes should not be null");
        assertFalse(suggestedDishes.isEmpty(), "Suggested dishes should not be empty");
        for(Dish dish : suggestedDishes) {
            assertNotNull(dish.getName(), "Dish name should not be null");
            assertNotNull(dish.getDescription(), "Dish description should not be null");
            assertFalse(dish.getIngredients().isEmpty(), "Dish ingredients should not be empty");
        }
    }
}