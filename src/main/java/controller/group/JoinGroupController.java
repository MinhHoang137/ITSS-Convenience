package controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.service.UserGroupService;
import model.service.IUserGroupService;
import session.Session;

public class JoinGroupController {

    @FXML private TextField txtGroupId;
    private final IUserGroupService groupService = new UserGroupService();

    @FXML
    public void handleJoin() {
        try {
            int groupId = Integer.parseInt(txtGroupId.getText());
            boolean exists = groupService.checkGroupExists(groupId);
            if (!exists) {
                showAlert("Nhóm không tồn tại.");
                return;
            }

            boolean linked = groupService.addUserToGroup(Session.getCurrentUser().getId(), groupId);
            if (linked) {
                Session.getCurrentUser().setGroupId(groupId);
                switchScene("/itss/convenience/dashboard.fxml");
            } else {
                showAlert("Lỗi khi thêm vào nhóm.");
            }
        } catch (Exception e) {
            showAlert("Mã nhóm không hợp lệ.");
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtGroupId.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleBack(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/itss/convenience/introduce_group.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Chọn nhóm");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
