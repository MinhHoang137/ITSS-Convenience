package model.service;

import model.entity.Ingredient;
import model.entity.ShoppingList;

import java.time.LocalDate;
import java.util.List;

public interface IShoppingListService {
    boolean createShoppingList(ShoppingList list);
    boolean deleteShoppingList(int shoppingListId);
    List<ShoppingList> getShoppingListsByGroupId(int groupId);
    boolean addIngredientsToShoppingList(List<Ingredient> ingredients, int shoppingListId);
    boolean isDateDuplicated(LocalDate date, int groupId);
    boolean addIngredientsToShoppingList(int groupId, List<Ingredient> ingredients);
    int getMaxShoppingListId();
}

