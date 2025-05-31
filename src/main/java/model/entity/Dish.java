package model.entity;

import java.util.ArrayList;

public class Dish {

    public static String[] weekdays = {
            "Thứ Hai", "Thứ Ba", "Thứ Tư",
            "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"
    };

    private int id;
    private String name;
    private String description;
    private ArrayList<Ingredient> ingredients;
    private MealType eatTime;
    private int eatDate; // 0 -> 6
    public Dish(){}
    public Dish(int id, String name, String description, ArrayList<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
    }
    public Dish(int id, String name, String description, ArrayList<Ingredient> ingredients, MealType eatTime, int eatDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.eatTime = eatTime;
        this.eatDate = eatDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    public MealType getEatTime() {
        return eatTime;
    }
    public void setEatTime(MealType eatTime) {
        this.eatTime = eatTime;
    }
    public String getEatDate() {
        return weekdays[eatDate];
    }
    public int getEatDateIndex() {
        return eatDate;
    }
    public void setEatDate(int eatDate) {
        this.eatDate = eatDate;
    }
}