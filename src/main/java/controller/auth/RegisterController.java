package controller.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.service.IUserService;
import model.service.UserService;

/**
 * Controller chịu trách nhiệm xử lý logic đăng ký người dùng mới.
 */
public class RegisterController {

    @FXML private TextField txtUsername;
    private final IUserService userService = new UserService();

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Đăng ký".
     * Nếu tên người dùng chưa tồn tại thì tạo tài khoản mới và chuyển sang màn hình đăng nhập.
     * Nếu đã tồn tại, hiển thị cảnh báo.
     */
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

    /**
     * Chuyển sang màn hình đăng nhập.
     */
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

    /**
     * Hiển thị hộp thoại thông báo với nội dung cho trước.
     *
     * @param msg Nội dung thông báo
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
