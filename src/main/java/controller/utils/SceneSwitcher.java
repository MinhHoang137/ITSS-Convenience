package controller.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

/**
 * Lớp tiện ích để chuyển scene trong JavaFX.
 * Được sử dụng khi cần chuyển đổi giữa các giao diện dựa trên file FXML.
 */
public class SceneSwitcher {

    /**
     * Chuyển sang một scene mới dựa trên FXML được chỉ định.
     *
     * @param control   Một control bất kỳ trong scene hiện tại (để lấy được Stage hiện tại).
     * @param fxmlPath  Đường dẫn đến file FXML của scene mới.
     * @param title     Tiêu đề cửa sổ khi hiển thị scene mới.
     */
    public static void switchScene(Control control, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
            Stage stage = (Stage) control.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
