package model.entity;

import java.util.ArrayList;

public class Fridge {
    private int id;
    private int userGroupId;
    private ArrayList<Ingredient> ingredients;

    public Fridge(int id, int userGroupId, ArrayList<Ingredient> ingredientArrayList) {
        this.id = id;
        this.userGroupId = userGroupId;
        this.ingredients = ingredientArrayList;
    }
    public Fridge() {
        this.ingredients = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
