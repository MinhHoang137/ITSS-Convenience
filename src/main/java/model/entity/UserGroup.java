package model.entity;

import java.util.ArrayList;

public class UserGroup {
    private int id;
    private String name;
    private ArrayList<User> userList;
    private ArrayList<Fridge> fridgeList;
    private ArrayList<Meal> mealList;
    private ArrayList<ShoppingList> shoppingListList;

    public UserGroup(int id, String name, ArrayList<User> userList, ArrayList<Fridge> fridgeList, ArrayList<Meal> mealList, ArrayList<ShoppingList> shoppingListList) {
        this.id = id;
        this.name = name;
        this.userList = userList;
        this.fridgeList = fridgeList;
        this.mealList = mealList;
        this.shoppingListList = shoppingListList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public ArrayList<Fridge> getFridgeList() {
        return fridgeList;
    }

    public void setFridgeList(ArrayList<Fridge> fridgeList) {
        this.fridgeList = fridgeList;
    }

    public ArrayList<Meal> getMealList() {
        return mealList;
    }

    public void setMealList(ArrayList<Meal> mealList) {
        this.mealList = mealList;
    }

    public ArrayList<ShoppingList> getShoppingListList() {
        return shoppingListList;
    }

    public void setShoppingListList(ArrayList<ShoppingList> shoppingListList) {
        this.shoppingListList = shoppingListList;
    }
}
