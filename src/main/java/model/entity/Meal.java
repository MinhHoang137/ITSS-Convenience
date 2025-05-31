package model.entity;

import java.util.ArrayList;

public class Meal {
    private int id;
    private int dateIndex;
    private ArrayList<Dish> dishList;
    private MealType mealType;

    public Meal(int id, int dateIndex, ArrayList<Dish> dishList, MealType mealType) {
        this.id = id;
        this.dateIndex = dateIndex;
        this.dishList = dishList;
        this.mealType = mealType;
    }
    public Meal() {
        this.dishList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateIndex() {
        return dateIndex;
    }
    public void setDateIndex(int dateIndex) {
        this.dateIndex = dateIndex;
    }

    public ArrayList<Dish> getDishList() {
        return dishList;
    }

    public void setDishList(ArrayList<Dish> dishList) {
        this.dishList = dishList;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
}
