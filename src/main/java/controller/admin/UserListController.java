package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.entity.Role;
import model.entity.User;
import model.service.UserGroupService;
import model.service.UserService;

/**
 * Controller cho màn hình quản lý danh sách người dùng.
 * Cho phép admin cập nhật vai trò và nhóm người dùng, cũng như xóa người dùng.
 */
public class UserListController {

    @FXML private TableView<User> tableUser;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colGroupId;
    @FXML private TableColumn<User, String> colGroup;
    @FXML private ComboBox<String> cbRole;
    @FXML private TextField txtGroupId;

    private User selectedUser = null;
    private final UserService userService = new UserService();
    private final UserGroupService groupService = new UserGroupService();
    private final ObservableList<User> userData = FXCollections.observableArrayList();

    /**
     * Khởi tạo dữ liệu khi FXML được load.
     * Thiết lập bảng người dùng, dữ liệu vai trò và xử lý khi chọn dòng.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        colUsername.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole().name()));
        colGroupId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getGroupId())));
        colGroup.setCellValueFactory(data -> {
            int gid = data.getValue().getGroupId();
            String name = groupService.getGroupNameById(gid);
            return new javafx.beans.property.SimpleStringProperty(name != null ? name : "(Chưa có)");
        });

        userData.setAll(userService.getAllUsers());
        tableUser.setItems(userData);
        cbRole.getItems().addAll("HOUSEWIFE", "MEMBER");

        tableUser.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedUser = newSel;
            if (newSel != null) {
                cbRole.setValue(newSel.getRole().name());
                txtGroupId.setText(String.valueOf(newSel.getGroupId()));
            }
        });
    }

    /**
     * Cập nhật vai trò và nhóm cho người dùng được chọn.
     * Kiểm tra logic đảm bảo nhóm luôn có ít nhất một HOUSEWIFE.
     */
    @FXML
    public void handleUpdateUser() {
        if (selectedUser == null) {
            showAlert("Vui lòng chọn một người dùng để cập nhật.");
            return;
        }

        try {
            String roleStr = cbRole.getValue();
            int newGroupId = Integer.parseInt(txtGroupId.getText());

            boolean isDemotingLastHousewife = selectedUser.getRole() == Role.housewife &&
                    !roleStr.equalsIgnoreCase("HOUSEWIFE") &&
                    userService.countHousewivesInGroup(selectedUser.getGroupId()) <= 1;

            if (isDemotingLastHousewife) {
                showAlert("Mỗi nhóm cần ít nhất 1 người có vai trò HOUSEWIFE.");
                return;
            }

            selectedUser.setRole(Role.fromString(roleStr));
            selectedUser.setGroupId(newGroupId);

            boolean ok = userService.updateUser(selectedUser);
            if (ok) {
                showAlert("Cập nhật thành công.");
                userData.setAll(userService.getAllUsers());
            } else {
                showAlert("Cập nhật thất bại.");
            }

        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Xóa người dùng được chọn.
     * Ngăn không cho xóa nếu là HOUSEWIFE cuối cùng trong nhóm.
     */
    @FXML
    public void handleDeleteUser() {
        if (selectedUser == null) {
            showAlert("Vui lòng chọn một người dùng để xóa.");
            return;
        }

        if (selectedUser.getRole() == Role.housewife &&
                userService.countHousewivesInGroup(selectedUser.getGroupId()) <= 1) {
            showAlert("Không thể xóa HOUSEWIFE cuối cùng của nhóm.");
            return;
        }

        boolean confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa người dùng này?", ButtonType.YES, ButtonType.NO)
                .showAndWait().orElse(ButtonType.NO) == ButtonType.YES;

        if (confirm) {
            boolean ok = userService.deleteUserById(selectedUser.getId());
            if (ok) {
                showAlert("Đã xóa người dùng.");
                userData.setAll(userService.getAllUsers());
            } else {
                showAlert("Xóa thất bại.");
            }
        }
    }

    /**
     * Hiển thị hộp thoại thông báo với nội dung truyền vào.
     *
     * @param message nội dung thông báo
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
