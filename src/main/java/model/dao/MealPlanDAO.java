package model.dao;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.Meal;
import model.service.meal_plan.*;


import java.util.ArrayList;

public class MealPlanDAO {
    private static MealPlanDAO instance;

    private final IMealPlanGetService mealPlanGetService = MealPlanGetService.getInstance();
    private final IMealPlanCreateService mealPlanCreateService = MealPlanCreateService.getInstance();
    private final IMealDeleteService mealDeleteService = MealDeleteService.getInstance();
    private final IMealPlanUpdateService mealPlanUpdateService = MealPlanUpdateService.getInstance();
    private MealPlanDAO() {
        // Private constructor to prevent instantiation
    }
    public static MealPlanDAO getInstance() {
        if (instance == null) {
            instance = new MealPlanDAO();
        }
        return instance;
    }

    //Read
    /**
     * Get a dish by its ID.
     * @param id the ID of the dish
     * @return the Dish object if found, null otherwise
     */
    public Dish getDish(int id) {
        return mealPlanGetService.getDish(id);
    }

    /**
     * Get a dish by its name.
     * @param name the name of the dish
     * @return the Dish object if found, null otherwise
     */
    public ArrayList<Dish> getDishLike(String name) {
        return mealPlanGetService.getDishLike(name);
    }

    /**
     * Get all dishes from the database.
     * @return an ArrayList of Dish objects containing all dishes
     */
    public ArrayList<Dish> getAllDishes() {
        return mealPlanGetService.getAllDishes();
    }

    /**
     * Get cookable dishes in a fridge.
     * @param fridgeId
     * @return
     */
    public ArrayList<Dish> getCookableDishes(int fridgeId) {
        return mealPlanGetService.getCookableDishes(fridgeId);
    }
    public boolean saveDish(Dish dish) {
        if (!mealPlanGetService.isDishExists(dish.getName())){
            return mealPlanCreateService.addDish(dish);
        } else {
            return mealPlanUpdateService.updateDish(dish);
        }
    }
/**
 * Retrieves a list of cookable dishes for a specific day index.
 * @param fridgeId The ID of the fridge to check ingredients against.
 * @param dayIndex The index of the day (0-6) to filter dishes by their eat date.
 * @return A list of Dish objects that can be cooked on the specified day.
 */
    public ArrayList<Dish> getCookableDishesByDayIndex(int fridgeId, int dayIndex) {
        return mealPlanGetService.getCookableDishesByDayIndex(fridgeId, dayIndex);
    }
    // Create

    /**
     * Adds a new meal for the meal plan table of the database.
     * @param meal the Meal object to be added
     * @return true if the meal was added successfully, false otherwise
     */
    public boolean addMeal(Meal meal, int groupId) {
        return mealPlanCreateService.addMeal(meal, groupId);
    }
    // Delete
    /**
     * Deletes a meal from the meal plan table of the database.
     * @param mealId the ID of the meal to be deleted
     * @return true if the meal was deleted successfully, false otherwise
     */
    public boolean deleteMeal(int mealId) {
        return mealDeleteService.deleteMeal(mealId);
    }
    public boolean deleteDish(int dishId) {
        return mealDeleteService.deleteDish(dishId);
    }

    public ArrayList<Meal> getAllMeals(int groupId) {
        return mealPlanGetService.getAllMeals(groupId);
    }
    public ArrayList<Ingredient> getMissingIngredients(int fridgeId, ArrayList<Ingredient> totalIngredients){
        return mealPlanGetService.getMissingIngredients(fridgeId, totalIngredients);
    }
    public boolean canCookMeal(int mealId, int groupId){
        return mealPlanGetService.canCookMeal(mealId, groupId);
    }
}
