/**
 * MealPlanDAO.java
 * lớp MealPlanDAO cung cấp các phương thức để quản lý kế hoạch bữa ăn,
 * bao gồm việc lấy, thêm, cập nhật và xóa các món ăn
 * cũng như các nguyên liệu liên quan.
 * Nó sử dụng các dịch vụ khác nhau để thực hiện các thao tác này,
 * @author Minh Hoàng
 */
package model.dao;

import model.entity.*;
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
     * Lấy ra món ăn theo ID.
     * @param id Id của món ăn cần lấy
     * @return một {@link Dish} chi tiết về món ăn nếu tìm thấy, {@code null} nếu không tìm thấy
     */
    public Dish getDish(int id) {
        return mealPlanGetService.getDish(id);
    }

    /**
     * Lấy ra danh sách món ăn theo tên.
     * @param name tên của món ăn cần tìm kiếm hoặc là một phần của tên món ăn
     * @return một {@link ArrayList} chứa các món ăn có tên giống hoặc chứa tên đã cho
     */
    public ArrayList<Dish> getDishLike(String name) {
        return mealPlanGetService.getDishLike(name);
    }

    /**
     * Lấy ra tất cả các món ăn có trong hệ thống.
     * @return một {@link ArrayList} chứa tất cả các món ăn
     */
    public ArrayList<Dish> getAllDishes() {
        return mealPlanGetService.getAllDishes();
    }

    /**
     * Lấy ra danh sách các món ăn có thể nấu dựa trên nguyên liệu trong tủ lạnh.
     * @param fridgeId ID của tủ lạnh để kiểm tra nguyên liệu
     * @return một {@link ArrayList} chứa các món ăn có thể nấu
     */
    public ArrayList<Dish> getCookableDishes(int fridgeId) {
        return mealPlanGetService.getCookableDishes(fridgeId);
    }

    /**
     * Lưu một món ăn vào hệ thống. Nếu món ăn đã tồn tại, nó sẽ cập nhật thông tin của món ăn đó.
     * @param dish món ăn cần lưu, bao gồm tên, mô tả, thời gian ăn, ngày ăn và danh sách nguyên liệu
     * @return {@code true} nếu lưu thành công, {@code false} nếu có lỗi xảy ra hoặc món ăn không hợp lệ
     */
    public boolean saveDish(Dish dish) {
        if (!mealPlanGetService.isDishExists(dish.getName())){
            return mealPlanCreateService.addDish(dish);
        } else {
            return mealPlanUpdateService.updateDish(dish);
        }
    }

    /**
     * Lấy danh sách các món ăn có thể nấu theo chỉ số ngày trong kế hoạch bữa ăn.
     * @param fridgeId ID của tủ lạnh để kiểm tra nguyên liệu
     * @param dayIndex chỉ số ngày trong tuần (0 cho Thứ hai, 1 cho Thứ ba, ..., 6 cho Chủ nhật)
     * @return một {@link ArrayList} chứa các món ăn có thể nấu trong ngày chỉ định
     */
    public ArrayList<Dish> getCookableDishesByDayIndex(int fridgeId, int dayIndex) {
        return mealPlanGetService.getCookableDishesByDayIndex(fridgeId, dayIndex);
    }

    /**
     * Lấy danh sách các món ăn trong kế hoạch bữa ăn theo ID nhóm người dùng.
     * @param groupId ID của nhóm người dùng
     * @return một {@link ArrayList} chứa các món ăn trong kế hoạch bữa ăn của nhóm
     */
    public ArrayList<Meal> getAllMeals(int groupId) {
        return mealPlanGetService.getAllMeals(groupId);
    }

    /**
     * Lấy danh sách các nguyên liệu bị thiếu để nấu một món ăn hoặc một tập hợp các món ăn nhất định,
     * dựa trên lượng nguyên liệu có sẵn trong một tủ lạnh cụ thể.
     *
     * <p>Phương thức này duyệt qua một danh sách các nguyên liệu tổng cộng cần thiết
     * (ví dụ: cho một món ăn cụ thể hoặc cho nhiều món ăn đã chọn).
     * Với mỗi nguyên liệu cần thiết, nó sẽ so sánh số lượng cần thiết
     * với số lượng hiện có trong tủ lạnh được chỉ định.
     * Nếu số lượng có sẵn ít hơn số lượng yêu cầu, nguyên liệu đó
     * cùng với số lượng thiếu sẽ được thêm vào danh sách các nguyên liệu bị thiếu.
     *
     * @param fridgeId ID của tủ lạnh để kiểm tra các nguyên liệu có sẵn.
     * @param totalIngredients Một {@code ArrayList} các đối tượng {@code Ingredient}
     * đại diện cho tổng số lượng nguyên liệu cần thiết.
     * Mỗi {@code Ingredient} trong danh sách này nên chứa tên,
     * số lượng yêu cầu và đơn vị của nguyên liệu.
     * @return Một {@code ArrayList} các đối tượng {@code Ingredient} đại diện
     * cho các nguyên liệu bị thiếu và số lượng thiếu tương ứng.
     * Nếu không thiếu nguyên liệu nào, một danh sách rỗng sẽ được trả về.
     */
    public ArrayList<Ingredient> getMissingIngredients(int fridgeId, ArrayList<Ingredient> totalIngredients){
        return mealPlanGetService.getMissingIngredients(fridgeId, totalIngredients);
    }
    /**
     * Kiểm tra xem một món ăn có thể nấu được hay không dựa trên ID món ăn và ID tủ lạnh.
     * @param mealId ID của món ăn cần kiểm tra
     * @param fridgeId ID của tủ lạnh
     * @return {@code true} nếu món ăn có thể nấu được, {@code false} nếu không
     */
    public boolean canCookMeal(int mealId, int fridgeId){
        return mealPlanGetService.canCookMeal(mealId, fridgeId);
    }

    // Create
    /**
     * Thêm một món ăn vào kế hoạch bữa ăn.
     * @param meal đối tượng Meal chứa thông tin về món ăn cần thêm
     * @param groupId ID của nhóm người dùng mà món ăn này thuộc về
     * @return {@code true} nếu thêm thành công, {@code false} nếu có lỗi xảy ra
     */
    public boolean addMeal(Meal meal, int groupId) {
        return mealPlanCreateService.addMeal(meal, groupId);
    }
    // Delete
    /**
     * Xóa một món ăn khỏi kế hoạch bữa ăn theo ID.
     * @param mealId ID của món ăn cần xóa
     * @return {@code true} nếu xóa thành công, {@code false} nếu có lỗi xảy ra
     */
    public boolean deleteMeal(int mealId) {
        return mealDeleteService.deleteMeal(mealId);
    }
    /**
     * Xóa một món ăn khỏi hệ thống theo ID.
     * @param dishId ID của món ăn cần xóa
     * @return {@code true} nếu xóa thành công, {@code false} nếu có lỗi xảy ra
     */
    public boolean deleteDish(int dishId) {
        return mealDeleteService.deleteDish(dishId);
    }
}
