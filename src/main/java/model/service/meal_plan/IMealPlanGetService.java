package model.service.meal_plan;

import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.Meal;
import model.entity.Unit;

import java.util.ArrayList;

public interface IMealPlanGetService {
    /**
     * Lấy một món ăn theo ID.
     * @param id ID của món ăn cần lấy.
     * @return Một đối tượng Dish chứa thông tin về món ăn, hoặc null nếu không tìm thấy.
     */
    public Dish getDish(int id);

    /**
     * Truy xuất danh sách các món ăn dựa trên tên, bao gồm các nguyên liệu và loại bữa ăn của chúng.
     *
     * @param dishName Tên của món ăn cần tìm.
     * @return Một đối tượng {@code Dish} chứa thông tin chi tiết về món ăn, hoặc {@code null} nếu không tìm thấy.
     */
    public ArrayList<Dish> getDishLike(String dishName);

    /**
     * Truy xuất tất cả các món ăn từ cơ sở dữ liệu.
     *
     * @return Một {@code ArrayList} các đối tượng {@code Dish} chứa tất cả các món ăn.
     */
    public ArrayList<Dish> getAllDishes();
    /**
     * Kiểm tra xem một món ăn có tồn tại trong cơ sở dữ liệu theo tên của nó hay không.
     *
     * @param dishName Tên của món ăn cần kiểm tra.
     * @return {@code true} nếu món ăn tồn tại, ngược lại là {@code false}.
     */
    boolean isDishExists(String dishName);

    /**
     * Truy xuất tổng số lượng của một nguyên liệu cụ thể trong tủ lạnh.
     *
     * @param ingredientName Tên của nguyên liệu cần kiểm tra.
     * @param unit Đơn vị đo lường của nguyên liệu.
     * @param fridgeId ID của tủ lạnh cần kiểm tra.
     * @return Tổng số lượng của nguyên liệu trong tủ lạnh đã chỉ định.
     */
    public double getTotalQuantityOfIngredient(String ingredientName, Unit unit, int fridgeId);

    /**
     * Kiểm tra xem một món ăn có thể được nấu bằng các nguyên liệu có sẵn trong tủ lạnh hay không.
     *
     * <p>Phương thức này sẽ so sánh các nguyên liệu cần thiết để nấu món ăn được chỉ định
     * với các nguyên liệu hiện có trong tủ lạnh. Nó sẽ trả về {@code true} nếu tất cả
     * các nguyên liệu cần thiết đều có đủ số lượng trong tủ lạnh, và {@code false} nếu thiếu bất kỳ nguyên liệu nào.
     *
     * @param dishId ID của món ăn cần kiểm tra.
     * @param fridgeId ID của tủ lạnh để kiểm tra nguyên liệu.
     * @return {@code true} nếu tất cả các nguyên liệu cần thiết để nấu món ăn đều có sẵn trong tủ lạnh, ngược lại là {@code false}.
     */
    public boolean canCookDish(int dishId, int fridgeId);

    /**
     * Truy xuất danh sách các món ăn có thể được nấu bằng các nguyên liệu có sẵn trong tủ lạnh.
     *
     * <p>Phương thức này sẽ kiểm tra tất cả các món ăn có trong cơ sở dữ liệu
     * và so sánh các nguyên liệu cần thiết cho mỗi món ăn với các nguyên liệu hiện có
     * trong tủ lạnh được chỉ định. Nó sẽ trả về một danh sách các đối tượng {@code Dish}
     * mà người dùng có thể nấu được với các nguyên liệu hiện có.
     *
     * @param fridgeId ID của tủ lạnh để kiểm tra nguyên liệu.
     * @return Một {@code List} các đối tượng {@code Dish} có thể được nấu bằng nguyên liệu trong tủ lạnh.
     * Trả về một danh sách rỗng nếu không có món ăn nào có thể được nấu.
     */
    public ArrayList<Dish> getCookableDishes(int fridgeId);

    /**
     * Truy xuất danh sách món ăn có thể nấu được cho một chỉ số ngày cụ thể.
     *
     * @param fridgeId ID tủ lạnh.
     * @param dayIndex Chỉ số ngày (0-6) để lọc món ăn.
     * @return Danh sách các đối tượng {@code Dish} có thể nấu vào ngày đã chỉ định.
     */
    public ArrayList<Dish> getCookableDishesByDayIndex(int fridgeId, int dayIndex);

    /**
     * Lấy một Bữa ăn (Meal) theo ID của nó và ID nhóm.
     *
     * @param id ID của bữa ăn cần truy xuất.
     * @param groupId ID của nhóm mà bữa ăn thuộc về.
     * @return Một đối tượng {@code Meal} chứa chi tiết bữa ăn, hoặc {@code null} nếu không tìm thấy.
     */
    public Meal getMeal(int id, int groupId);

    /**
     * Lấy danh sách tất cả các bữa ăn thuộc về một nhóm cụ thể.
     *
     * @param groupId ID của nhóm cần truy xuất bữa ăn.
     * @return Một {@code ArrayList} chứa tất cả các bữa ăn thuộc về nhóm đã chỉ định.
     */
    public ArrayList<Meal> getAllMeals(int groupId);

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
    public ArrayList<Ingredient> getMissingIngredients(int fridgeId, ArrayList<Ingredient> totalIngredients);

    /**
     * Kiểm tra xem một bữa ăn có thể được nấu với các nguyên liệu có sẵn trong tủ lạnh hay không.
     *
     * @param mealId ID của bữa ăn cần kiểm tra.
     * @param fridgeId ID của tủ lạnh để kiểm tra nguyên liệu.
     * @return {@code true} nếu bữa ăn có thể được nấu, ngược lại là {@code false}.
     */
    boolean canCookMeal(int mealId, int fridgeId);

    /**
     * lấy id lớn nhất của món ăn hiện có trong cơ sở dữ liệu, phục vụ cho việc tạo món ăn mới
     * và kiểm thử.
     * @return ID lớn nhất của món ăn, hoặc -1 nếu không tìm thấy.
     */
    int getMaxDishId();
    /**
     * Lấy ID lớn nhất của bữa ăn hiện có trong cơ sở dữ liệu, phục vụ cho việc tạo bữa ăn mới
     * và kiểm thử.
     *
     * @return ID lớn nhất của bữa ăn, hoặc -1 nếu không tìm thấy.
     */
    int getMaxMealId();
}
