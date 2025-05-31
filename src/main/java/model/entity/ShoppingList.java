package model.entity;

import java.time.LocalDate;
import java.util.ArrayList;

public class ShoppingList {
    private int id;
    private LocalDate buyDate;
    private ArrayList<Ingredient> ingredients;

    public  ShoppingList(int id, LocalDate buyDate, ArrayList<Ingredient> ingredients) {
        this.id = id;
        this.buyDate = buyDate;
        this.ingredients = ingredients;
    }
    public ShoppingList() {
        this.ingredients = new ArrayList<>();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(LocalDate buyDate) {
        this.buyDate = buyDate;
    }
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
    
