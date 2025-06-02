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

public class DashboardController {

    @FXML
    private Label lblGroupName;
    @FXML
    private Button fridge;
    @FXML
    private Label lblRole;

    private final UserGroupService groupService = new UserGroupService();

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

    @FXML
    public void goToShoppingList() {
        switchScene("/itss/convenience/shopping_list.fxml");
    }

    @FXML
    public void goToFridge() {
        SceneSwitcher.switchScene(fridge, "/fridge/fridge.fxml", "Nguyên liệu trong tủ lạnh");
    }

    @FXML
    public void goToSuggestion() {
        SceneSwitcher.switchScene(fridge, "/dish/dish_suggest.fxml", "Gợi ý món ăn");
    }

    @FXML
    public void goToMealPlan() {
        ViewController.getInstance().openController(new MealPlanController(),
                "Kế hoạch bữa ăn", 1000, 600);
    }

    @FXML
    public void handleLogout() {
        Session.clear();
        switchScene("/itss/convenience/login.fxml");
    }

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
