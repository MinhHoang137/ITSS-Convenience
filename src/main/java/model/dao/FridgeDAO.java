package model.dao;

import model.entity.Dish;
import model.entity.Fridge;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.*;


import java.util.List;

public class FridgeDAO {
    private static FridgeDAO instance;

    private final IngredientService ingredientService = new IngredientService();
    private final DishService dishService = new DishService();
    private final FridgeService fridgeQueryService = new FridgeService();

    private FridgeDAO() {
        // Private constructor để đảm bảo Singleton
    }

    public static FridgeDAO getInstance() {
        if (instance == null) {
            instance = new FridgeDAO();
        }
        return instance;
    }

    // Thêm nguyên liệu có sẵn ID
    public void addIngredient(Ingredient ingredient, int fridgeId) {
        ingredientService.addIngredient(ingredient, fridgeId);
    }

    // Thêm nguyên liệu không có sẵn ID
    public void addIngredientToFridge(Ingredient ingredient, int fridgeId) {
        ingredientService.addIngredientToFridge(ingredient, fridgeId);
    }

    // Lấy tất cả nguyên liệu trong tủ lạnh (gộp)
    public List<Ingredient> getAllIngredients(int fridgeId) {
        return ingredientService.getAllIngredients(fridgeId);
    }

    // Lấy nguyên liệu sắp hết hạn trong N ngày tới
    public List<Ingredient> getExpiringIngredients(int fridgeId, int daysBeforeExpire) {
        return ingredientService.getExpiringIngredients(fridgeId, daysBeforeExpire);
    }

    // Trừ nguyên liệu trong tủ lạnh khi sử dụng
    public boolean useIngredient(String name, double usedQuantity, Unit unit, int fridgeId) {
        return ingredientService.useIngredient(name, usedQuantity, unit, fridgeId);
    }

    // Nấu món ăn nếu đủ nguyên liệu
    public boolean cookDish(Dish dish, int fridgeId) {
        return dishService.cookDish(dish, fridgeId);
    }

    // Tiêu thụ nguyên liệu cho món ăn (giống cookDish nhưng rõ ràng hơn)
    public boolean consumeIngredientsForDish(Dish dish, int fridgeId) {
        return dishService.consumeIngredientsForDish(dish, fridgeId);
    }

    // Lấy fridgeId từ groupId
    public int getFridgeIdByGroupId(int groupId) {
        return fridgeQueryService.getFridgeIdByGroupId(groupId);
    }

    // Lấy tất cả các tủ lạnh trong hệ thống
    public List<Fridge> getAllFridges() {
        return fridgeQueryService.getAllFridges();
    }
}
