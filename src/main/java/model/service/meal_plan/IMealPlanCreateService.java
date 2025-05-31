package model.service.meal_plan;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.Meal;

public interface IMealPlanCreateService {
    /**
     * Thêm một món ăn vào bảng dish.
     * @param dish Món ăn cần thêm
     * @return true nếu thành công, ngược lại false
     */
    boolean addToDishTable(Dish dish);

    /**
     * Lấy ID món ăn được thêm gần nhất.
     * @return ID món ăn hoặc -1 nếu không tìm thấy
     */
    int getLastInsertedDishId();

    /**
     * Thêm một nguyên liệu vào món ăn cụ thể.
     * @param dishId ID món ăn
     * @param ingredient Nguyên liệu cần thêm
     * @return true nếu thành công
     */
    boolean addIngredientToDish(int dishId, Ingredient ingredient);

    /**
     * Thêm món ăn và toàn bộ nguyên liệu của nó vào CSDL.
     * @param dish Món ăn cần thêm
     * @return true nếu thành công
     */
    boolean addDish(Dish dish);

    /**
     * Thêm thông tin một bữa ăn vào bảng meal_plan (chưa có dish).
     * @param meal Bữa ăn
     * @return true nếu thành công
     */
    boolean addMealToTable(Meal meal);

    /**
     * Thêm quan hệ giữa bữa ăn và các món ăn vào bảng meal_has_dish.
     * @param meal Bữa ăn đã có id và danh sách món ăn
     * @return true nếu thành công
     */
    boolean addMealDishRelations(Meal meal);

    /**
     * Thêm bữa ăn và các món ăn liên kết.
     * @param meal Bữa ăn
     * @return true nếu thành công
     */
    boolean addMeal(Meal meal);
}
