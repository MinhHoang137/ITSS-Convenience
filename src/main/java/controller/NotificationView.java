/**
* NotificationView.java
 * Lớp hiển thị thông báo cho người dùng.
 * Cung cấp phương thức để tạo và hiển thị thông báo với nội dung tùy chỉnh.
 * @author Minh Hoàng
 */

package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationView extends BaseController {

    public static String message;

    @FXML private Label lblMessage;

    @FXML private void onOKClick() {
        Stage stage = (Stage) lblMessage.getScene().getWindow();
        stage.close();
    }

    @Override
    public String getFxmlPath() {
        return "/notification.fxml";
    }
    @Override
    public BaseController loadAndShow(Stage stage, String title, int width, int height) {
        try {
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);  // khóa các cửa sổ khác
            URL resource = getClass().getResource(getFxmlPath());
            System.out.println("Resource: " + resource);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            ((NotificationView)loader.getController()).setMessage(message);  // truyền message vào controller
            stage.showAndWait();  // hiện modal và chặn đến khi đóng

            return loader.getController();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void setMessage(String message) {
        lblMessage.setText(message);
    }
    /**
     * Tạo và hiển thị thông báo với nội dung tùy chỉnh.
     * @param message Nội dung thông báo cần hiển thị.
     */
    public static void Create(String message) {
        NotificationView.message = message;
        Stage stage = new Stage();
        NotificationView notificationView = new NotificationView();
        notificationView.loadAndShow(stage, "Thông báo", 400, 200);
    }
}
