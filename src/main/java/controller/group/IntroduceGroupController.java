package controller.group;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import session.Session;

/**
 * Controller cho màn hình giới thiệu nhóm.
 * Cho phép người dùng chọn tạo nhóm mới, gia nhập nhóm hiện có hoặc đăng xuất.
 */
public class IntroduceGroupController {

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Tạo nhóm".
     * Chuyển sang màn hình tạo nhóm mới.
     *
     * @param event Sự kiện nhấn nút tạo nhóm.
     */
    @FXML
    public void handleCreateGroup(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/itss/convenience/create_group.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tạo nhóm");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Gia nhập nhóm".
     * Chuyển sang màn hình nhập mã nhóm để tham gia nhóm đã có.
     *
     * @param event Sự kiện nhấn nút gia nhập nhóm.
     */
    @FXML
    public void handleJoinGroup(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/itss/convenience/join_group.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gia nhập nhóm");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Đăng xuất".
     * Xóa thông tin đăng nhập khỏi session và chuyển về màn hình đăng nhập.
     *
     * @param event Sự kiện nhấn nút đăng xuất.
     */
    @FXML
    public void handleLogout(javafx.event.ActionEvent event) {
        Session.clear(); // Xóa thông tin đăng nhập

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/itss/convenience/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
