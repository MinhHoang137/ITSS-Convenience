package controller.admin;

import controller.dish.DishDetailView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import model.entity.User;
import model.service.UserGroupService;
import session.Session;

/**
 * Controller cho giao diện quản trị viên (Admin Dashboard).
 * Quản lý hiển thị thông tin người dùng, điều hướng đến các chức năng như
 * quản lý món ăn, người dùng, và xử lý đăng xuất.
 */
public class AdminDashboardController {

    @FXML private Label lblGroupName;
    @FXML private Label lblRole;
    @FXML private StackPane mainContent;

    private final UserGroupService groupService = new UserGroupService();

    /**
     * Hàm khởi tạo khi giao diện được load.
     * Hiển thị tên nhóm và vai trò của người dùng hiện tại.
     */
    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            lblRole.setText(user.getRole().name().toLowerCase());
            String groupName = groupService.getGroupNameById(user.getGroupId());
            lblGroupName.setText(groupName != null ? groupName : "(Không có nhóm)");
        } else {
            lblGroupName.setText("Không xác định người dùng");
            lblRole.setText("");
        }
    }

    /**
     * Load nội dung trung tâm (mainContent) từ một file FXML cụ thể.
     *
     * @param fxmlPath đường dẫn tới file FXML
     */
    private void setCenterContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Điều hướng tới trang chi tiết danh mục món ăn.
     */
    @FXML
    public void goToDishCategory() {
        DishDetailView.open(this.mainContent);
    }

    /**
     * Điều hướng tới trang quản lý người dùng.
     */
    @FXML
    public void goToUserManagement() {
        setCenterContent("/itss/convenience/user.fxml");
    }

    /**
     * Xử lý hành động đăng xuất.
     * Xóa phiên người dùng và chuyển về màn hình đăng nhập.
     */
    @FXML
    public void handleLogout() {
        Session.clear();
        switchScene("/itss/convenience/login.fxml");
    }

    /**
     * Chuyển toàn bộ scene sang FXML khác.
     *
     * @param path đường dẫn tới file FXML
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) lblGroupName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
