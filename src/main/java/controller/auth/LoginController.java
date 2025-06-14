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

/**
 * Controller xử lý logic đăng nhập trong ứng dụng.
 * Xác thực tên người dùng và điều hướng đến màn hình phù hợp theo vai trò và nhóm.
 */
public class LoginController {

    /** Trường nhập tên người dùng */
    @FXML private TextField txtUsername;

    /** Service xử lý người dùng */
    private final IUserService userService = new UserService();

    /**
     * Xử lý đăng nhập khi người dùng nhấn nút.
     * Kiểm tra tên đăng nhập, đặt phiên làm việc, và chuyển đến màn hình phù hợp.
     */
    @FXML
    public void handleLogin() {
        String username = txtUsername.getText();
        User user = userService.login(username);
        if (user != null) {
            Session.setCurrentUser(user);
            if(Session.getCurrentUser().getRole().name().equalsIgnoreCase("ADMIN")) {
                switchScene("/itss/convenience/admin_dashboard.fxml");
            }
            else {
                if (user.getGroupId() == 0) {
                    switchScene("/itss/convenience/introduce_group.fxml");
                } else {
                    switchScene("/itss/convenience/dashboard.fxml");
                }
            }
        } else {
            showAlert("Tài khoản không tồn tại.");
        }
    }

    /**
     * Chuyển người dùng đến giao diện đăng ký.
     */
    @FXML
    public void goToRegister() {
        switchScene("/itss/convenience/register.fxml");
    }

    /**
     * Chuyển sang giao diện mới theo đường dẫn FXML.
     *
     * @param path Đường dẫn tới file FXML cần chuyển đến.
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị hộp thoại cảnh báo với nội dung được cung cấp.
     *
     * @param msg Nội dung cảnh báo hiển thị cho người dùng.
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
