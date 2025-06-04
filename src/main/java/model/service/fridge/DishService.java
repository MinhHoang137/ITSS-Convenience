package model.service.fridge;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.BaseService;
import model.service.IBuyIngredientService;

public class DishService extends BaseService implements IDishService {

    private final IngredientService ingredientService = new IngredientService();

    public boolean cookDish(Dish dish, int fridgeId) {
        for (Ingredient ing : dish.getIngredients()) {
            boolean ok = ingredientService.useIngredient(ing.getName(), ing.getQuantity(), ing.getUnit(), fridgeId);
            if (!ok) return false;
        }
        return true;
    }

    public boolean consumeIngredientsForDish(Dish dish, int fridgeId) {
        try {
            for (Ingredient i : dish.getIngredients()) {
                if (!ingredientService.useIngredient(i.getName(), i.getQuantity(), i.getUnit(), fridgeId)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
