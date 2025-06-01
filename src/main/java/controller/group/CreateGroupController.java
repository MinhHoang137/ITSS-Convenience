package controller.group;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.entity.User;
import model.service.UserGroupService;
import model.service.IUserGroupService;
import model.service.UserService;
import session.Session;

public class CreateGroupController {

    @FXML private TextField txtGroupName;
    private final IUserGroupService groupService = new UserGroupService();
    private final UserService userService = new UserService();
    @FXML
    public void handleCreate() {
        String groupName = txtGroupName.getText();
        if (groupName.isBlank()) {
            showAlert("Tên nhóm không được trống.");
            return;
        }

        int newGroupId = groupService.createGroup(groupName);
        boolean linked = groupService.setAsHousewife(Session.getCurrentUser().getId(), newGroupId);
        if (linked) {
            Session.getCurrentUser().setGroupId(newGroupId);
            User user = userService.getUserById(Session.getCurrentUser().getId());
            Session.setCurrentUser(user);
            switchScene("/itss/convenience/dashboard.fxml");
        } else {
            showAlert("Lỗi khi thêm người dùng vào nhóm.");
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtGroupName.getScene().getWindow();
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
