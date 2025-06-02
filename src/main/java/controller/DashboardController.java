package controller;

import controller.fridge.FridgeController;
import controller.meal.MealPlanController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import model.entity.User;
import model.service.UserGroupService;
import session.Session;

public class DashboardController {

    @FXML
    private Label lblGroupName;
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
        switchScene("/fridge/fridge.fxml");
    }

    @FXML
    public void goToSuggestion() {
        switchScene("/itss/convenience/suggestion.fxml");
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
