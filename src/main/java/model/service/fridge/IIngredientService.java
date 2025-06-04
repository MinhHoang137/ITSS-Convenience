package model.service.fridge;

import java.util.List;
import model.entity.Ingredient;
import model.entity.Unit;

public interface IIngredientService {
    void addIngredient(Ingredient ingredient, int fridgeId);
    void addIngredientToFridge(Ingredient ingredient, int fridgeId);
    List<Ingredient> getAllIngredients(int fridgeId);
    List<Ingredient> getExpiringIngredients(int fridgeId, int daysBeforeExpire);
    boolean useIngredient(String name, double usedQuantity, Unit unit, int fridgeId);
}
