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

/**
 * Controller cho màn hình tham gia nhóm.
 * Cho phép người dùng nhập mã nhóm để gia nhập một nhóm đã tồn tại.
 */
public class JoinGroupController {

    /** Trường nhập mã nhóm. */
    @FXML private TextField txtGroupId;

    /** Service để xử lý logic liên quan đến nhóm người dùng. */
    private final IUserGroupService groupService = new UserGroupService();

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Tham gia".
     * Kiểm tra mã nhóm, xác nhận nhóm tồn tại, thêm người dùng vào nhóm,
     * rồi chuyển sang dashboard nếu thành công.
     */
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

    /**
     * Hiển thị hộp thoại cảnh báo với thông báo cụ thể.
     *
     * @param msg Nội dung thông báo.
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    /**
     * Chuyển màn hình hiện tại sang một scene khác thông qua đường dẫn FXML.
     *
     * @param path Đường dẫn đến file FXML cần chuyển đến.
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) txtGroupId.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Quay lại".
     * Quay về màn hình giới thiệu nhóm.
     *
     * @param event Sự kiện nhấn nút.
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
