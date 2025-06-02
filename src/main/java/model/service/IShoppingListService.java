package model.service;

import model.entity.Ingredient;
import model.entity.ShoppingList;
import java.util.List;

public interface IShoppingListService {
    boolean createShoppingList(ShoppingList list);
    boolean deleteShoppingList(int shoppingListId);
    List<ShoppingList> getShoppingListsByGroupId(int groupId);
    public boolean addIngredientsToShoppingList(List<Ingredient> ingredients, int shoppingListId);
}

