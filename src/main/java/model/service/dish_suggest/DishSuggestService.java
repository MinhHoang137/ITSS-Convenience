package model.service.dish_suggest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.dao.DishDAO;
import model.dao.IngredientDAO;
import model.entity.Dish;
import model.entity.Ingredient;
import model.service.BaseService;

/**
 * DishSuggestService là lớp cung cấp logic để gợi ý các món ăn
 * mà người dùng có thể nấu từ nguyên liệu hiện có trong tủ lạnh.
 */
public class DishSuggestService extends BaseService implements IDishSuggestService {

    private final DishDAO dishDAO = new DishDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();

    /**
     * Gợi ý danh sách các món ăn có thể nấu từ nguyên liệu trong tủ lạnh.
     *
     * @param fridgeId ID của tủ lạnh cần lấy nguyên liệu.
     * @return Danh sách các món ăn có thể nấu được từ các nguyên liệu có sẵn.
     */
    public List<Dish> suggestDishesFromFridge(int fridgeId) {
        List<Dish> possibleDishes = new ArrayList<>();

        try (Connection conn = getConnection()) {
            List<Ingredient> currentIngredients = ingredientDAO.getIngredientsByFridgeId(fridgeId, conn);
            List<Dish> allDishes = dishDAO.getAllDishesWithIngredients(conn);

            for (Dish dish : allDishes) {
                if (canCookDish(dish, currentIngredients)) {
                    possibleDishes.add(dish);
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi gợi ý món ăn:");
            e.printStackTrace();
        }

        return possibleDishes;
    }

    /**
     * Kiểm tra xem một món ăn có thể nấu được từ danh sách nguyên liệu hiện có hay không.
     *
     * @param dish              Món ăn cần kiểm tra.
     * @param fridgeIngredients Danh sách nguyên liệu hiện có trong tủ lạnh.
     * @return true nếu có đủ nguyên liệu để nấu món ăn, false nếu thiếu.
     */
    private boolean canCookDish(Dish dish, List<Ingredient> fridgeIngredients) {
        for (Ingredient required : dish.getIngredients()) {
            boolean found = false;
            for (Ingredient available : fridgeIngredients) {
                if (required.getName().trim().equalsIgnoreCase(available.getName().trim())
                        && required.getUnit().equals(available.getUnit())
                        && required.getQuantity() <= available.getQuantity()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
