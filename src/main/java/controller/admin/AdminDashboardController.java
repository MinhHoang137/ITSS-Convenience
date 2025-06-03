package controller.admin;

import controller.meal.DishDetailView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import model.entity.User;
import model.service.UserGroupService;
import session.Session;

public class AdminDashboardController {

    @FXML private Label lblGroupName;
    @FXML private Label lblRole;
    @FXML private StackPane mainContent;

    private final UserGroupService groupService = new UserGroupService();

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            lblRole.setText(user.getRole().name().toLowerCase());
            String groupName = groupService.getGroupNameById(user.getGroupId());
            lblGroupName.setText(groupName != null ? groupName : "(Không có nhóm)");
        } else {
            lblGroupName.setText("Không xác định người dùng");
            lblRole.setText("");
        }
    }

    private void setCenterContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToFoodCategory() {
        setCenterContent("/view/food.fxml");
    }

    @FXML
    public void goToDishCategory() {
        DishDetailView.open(this.mainContent);
    }

    @FXML
    public void goToUserManagement() {
        setCenterContent("/itss/convenience/user.fxml");
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
