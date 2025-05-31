package controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.entity.User;
import model.service.IUserService;
import model.service.UserService;
import session.Session;

public class LoginController {

    @FXML private TextField txtUsername;
    private final IUserService userService = new UserService();

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText();
        User user = userService.login(username);
        if (user != null) {
            Session.setCurrentUser(user);
            if (user.getGroupId() == 0) {
                switchScene("/itss/convenience/introduce_group.fxml");
            } else {
                switchScene("/itss/convenience/dashboard.fxml");
            }
        } else {
            showAlert("Tài khoản không tồn tại.");
        }
    }

    @FXML
    public void goToRegister() {
        switchScene("/itss/convenience/register.fxml");
    }

    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
