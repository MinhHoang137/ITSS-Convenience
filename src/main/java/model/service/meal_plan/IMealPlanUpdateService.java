package model.service.meal_plan;

import model.entity.Dish;

public interface IMealPlanUpdateService {
    /**
     * cập nhật thông tin món ăn
     * @param dish đối tượng món ăn cần cập nhật
     * @return {@code true} nếu cập nhật thành công, {@code false} nếu thất bại
     */
    boolean updateDish(Dish dish);
}
