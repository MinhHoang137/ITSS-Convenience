package model.service.meal_plan;

public interface IMealDeleteService {
    /**
     * Xóa một món ăn khỏi cơ sở dữ liệu.
     * @param id ID của món ăn cần xóa
     * @return true nếu xóa thành công, ngược lại false
     */
    boolean deleteMeal(int id);
}
