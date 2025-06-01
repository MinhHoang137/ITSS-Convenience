package controller.group;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import session.Session;

public class IntroduceGroupController {

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
