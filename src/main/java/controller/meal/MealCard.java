package controller.meal;

import controller.BaseController;
import controller.NotificationView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.dao.FridgeDAO;
import model.dao.MealPlanDAO;
import model.entity.Dish;
import model.entity.Meal;
import model.entity.MealType;
import model.entity.Role;
import model.service.fridge.DishService;
import model.service.fridge.FridgeService;
import session.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class MealCard extends BaseController {
    private Parent parent;
    @FXML
    private Label dishNameText;
    @FXML
    private Label timeToEatText;
    @FXML
    private Label dateToEatText;
    @FXML
    private Button deleteButton;

    private Meal meal;

    private Runnable onDeletedCallback; // Để thông báo cho View cha cập nhật lại sau khi xóa

    public void setMeal(Meal meal) {
        this.meal = meal;
        // Hiển thị thông tin
        if (meal != null) {
            dishNameText.setText(getMealNameForDisplay());
            timeToEatText.setText(getTimeText(meal.getMealType()));
            dateToEatText.setText(getDateText(meal.getDateIndex()));
        }
    }

    public void setOnDeletedCallback(Runnable callback) {
        this.onDeletedCallback = callback;
    }

    @FXML
    private void OnDelete() {
        MealPlanController mealPlanController = MealPlanController.getCurrent();
        mealPlanController.removeMeal(this);
    }

    private String getDateText(int index) {
        String[] days = {"Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy", "Chủ nhật"};
        return (index >= 0 && index < 7) ? days[index] : "Không rõ";
    }
    private String getTimeText(MealType mealType) {
        switch (mealType.toString().toLowerCase()) {
            case "breakfast":
                return "Bữa sáng";
            case "lunch":
                return "Bữa trưa";
            case "dinner":
                return "Bữa tối";
            default:
                return "Không rõ";
        }
    }
    private String getMealNameForDisplay() {
        if (meal.getDishList() != null && !meal.getDishList().isEmpty()) {
            String dishNames = meal.getDishList().stream()
                    .map(Dish::getName)
                    .reduce((first, second) -> first + ", " + second)
                    .orElse("");
            return dishNames.isEmpty() ? "(Không có món)" : dishNames;
        } else {
            return "(Không có món)";
        }
    }
    @Override
    public String getFxmlPath() {
        return "/meal/meal_card.fxml";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public Meal getMeal() {
        return meal;
    }

    public Parent getParent() {
        return parent;
    }
    public void setParent(Parent parent) {
        this.parent = parent;
    }

@FXML
private void OnCook(ActionEvent actionEvent) {
    Role role = Session.getCurrentUser().getRole();
    if (role != Role.housewife) {
        NotificationView.Create("Chỉ người nội trợ mới có thể đánh dấu đã nấu bữa ăn.");
        return;
    }

    MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();
    int groupId = Session.getCurrentUser().getGroupId();
    FridgeDAO fridgeDAO = FridgeDAO.getInstance();
    int fridgeId = fridgeDAO.getFridgeIdByGroupId(groupId);
    DishService dishService = new DishService();

    if (!mealPlanDAO.canCookMeal(meal.getId(), fridgeId)) {
        NotificationView.Create("Không đủ nguyên liệu để nấu bữa ăn này: " + meal.getId());
        return;
    }

    // Trừ nguyên liệu khỏi tủ lạnh
    boolean allSuccess = true;
    for (Dish dish : meal.getDishList()) {
        boolean success = dishService.cookDish(dish, fridgeId);
        if (!success) {
            allSuccess = false;
            break;
        }
    }

    if (allSuccess) {
        MealPlanController.getCurrent().removeMeal(this);
        NotificationView.Create("Đã đánh dấu bữa ăn đã nấu xong và trừ nguyên liệu.");
    } else {
        NotificationView.Create("Xảy ra lỗi khi trừ nguyên liệu. Có thể có món ăn không đủ nguyên liệu.");
    }
}

}
