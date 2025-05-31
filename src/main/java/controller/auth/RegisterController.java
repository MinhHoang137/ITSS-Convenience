package controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.service.IUserService;
import model.service.UserService;

public class RegisterController {

    @FXML private TextField txtUsername;
    private final IUserService userService = new UserService();

    @FXML
    public void handleRegister() {
        String username = txtUsername.getText();
        boolean success = userService.register(username);
        if (success) {
            showAlert("Đăng ký thành công! Đăng nhập ngay.");
            goToLogin();
        } else {
            showAlert("Tên tài khoản đã tồn tại.");
        }
    }

    @FXML
    public void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/itss/convenience/login.fxml"));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
