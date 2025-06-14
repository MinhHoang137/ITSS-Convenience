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

/**
 * Controller cho màn hình tạo nhóm người dùng.
 * Cho phép người dùng nhập tên nhóm, tạo nhóm mới và gán vai trò "HOUSEWIFE" cho người tạo.
 */
public class CreateGroupController {

    @FXML
    private TextField txtGroupName;

    private final IUserGroupService groupService = new UserGroupService();
    private final UserService userService = new UserService();

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Tạo nhóm".
     * - Tạo nhóm mới nếu tên hợp lệ.
     * - Gán người dùng hiện tại vào nhóm với vai trò housewife.
     * - Chuyển sang màn hình dashboard nếu thành công.
     */
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
            // Cập nhật lại session với người dùng mới có groupId
            Session.getCurrentUser().setGroupId(newGroupId);
            User user = userService.getUserById(Session.getCurrentUser().getId());
            Session.setCurrentUser(user);
            switchScene("/itss/convenience/dashboard.fxml");
        } else {
            showAlert("Lỗi khi thêm người dùng vào nhóm.");
        }
    }

    /**
     * Hiển thị hộp thoại thông báo lỗi.
     *
     * @param msg Nội dung thông báo.
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    /**
     * Chuyển sang scene được chỉ định bởi đường dẫn FXML.
     *
     * @param path Đường dẫn đến file FXML.
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtGroupName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút "Quay lại".
     * Quay về màn hình giới thiệu chọn nhóm.
     *
     * @param event Sự kiện ActionEvent.
     */
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
