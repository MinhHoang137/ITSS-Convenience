package model.service.dish_suggest;

import java.util.List;

import model.entity.Dish;

public interface IDishSuggestService {
    /**
     * Gợi ý các món có thể nấu từ nguyên liệu trong tủ lạnh.
     *
     * @param fridgeId ID của tủ lạnh
     * @return Danh sách món ăn có thể nấu được
     */
    List<Dish> suggestDishesFromFridge(int fridgeId);
}
