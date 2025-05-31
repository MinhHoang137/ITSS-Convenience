package model.service;

import model.entity.BuyIngredient;
import java.util.List;

public interface IBuyIngredientService {
    boolean addIngredientToList(BuyIngredient ingredient);
    boolean removeIngredientFromList(int shoppingListId, String ingredientName);
    List<BuyIngredient> getIngredientsByShoppingListId(int shoppingListId);
}

