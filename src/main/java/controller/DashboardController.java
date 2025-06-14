package controller;

import controller.meal.MealPlanController;
import controller.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.entity.User;
import model.service.UserGroupService;
import session.Session;

/**
 * Controller cho màn hình Dashboard chính của người dùng sau khi đăng nhập.
 * Hiển thị thông tin nhóm, vai trò người dùng và cho phép truy cập các tính năng chính như:
 * danh sách mua sắm, tủ lạnh, gợi ý món ăn và kế hoạch bữa ăn.
 */
public class DashboardController {

    @FXML
    private Label lblGroupName;
    @FXML
    private Button fridge;
    @FXML
    private Label lblRole;

    private final UserGroupService groupService = new UserGroupService();

    /**
     * Phương thức được gọi tự động sau khi FXML được load.
     * Khởi tạo thông tin người dùng hiện tại và hiển thị tên nhóm và vai trò.
     */
    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            String groupName = groupService.getGroupNameById(user.getGroupId());
            lblGroupName.setText(groupName);
            lblRole.setText(user.getRole().name().toLowerCase());
        } else {
            lblGroupName.setText("Không xác định người dùng.");
            lblRole.setText("");
        }
    }

    /**
     * Xử lý chuyển đến màn hình danh sách mua sắm.
     */
    @FXML
    public void goToShoppingList() {
        switchScene("/itss/convenience/shopping_list.fxml");
    }

    /**
     * Xử lý chuyển đến màn hình nguyên liệu trong tủ lạnh.
     */
    @FXML
    public void goToFridge() {
        SceneSwitcher.switchScene(fridge, "/fridge/fridge.fxml", "Nguyên liệu trong tủ lạnh");
    }

    /**
     * Xử lý chuyển đến màn hình gợi ý món ăn.
     */
    @FXML
    public void goToSuggestion() {
        SceneSwitcher.switchScene(fridge, "/dish/dish_suggest.fxml", "Gợi ý món ăn");
    }

    /**
     * Xử lý mở màn hình kế hoạch bữa ăn bằng controller riêng.
     */
    @FXML
    public void goToMealPlan() {
        ViewController.getInstance().openController(new MealPlanController(),
                "Kế hoạch bữa ăn", 1000, 600);
    }

    /**
     * Xử lý đăng xuất: xóa session và chuyển về màn hình đăng nhập.
     */
    @FXML
    public void handleLogout() {
        Session.clear();
        switchScene("/itss/convenience/login.fxml");
    }

    /**
     * Phương thức tiện ích để chuyển scene nội bộ trong controller.
     *
     * @param path đường dẫn đến file FXML.
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) lblGroupName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
