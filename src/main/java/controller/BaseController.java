/**
 * BaseController.java
 * Lớp cơ sở cho các controller trong ứng dụng JavaFX.
 * Cung cấp phương thức để tải và hiển thị giao diện từ file FXML.
 * Yêu cầu tất cả các controller kế thừa từ lớp này để sử dụng phương thức loadAndShow.
 * @author Minh Hoàng
 */

package controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public abstract class BaseController implements Initializable {
    public BaseController loadAndShow(Stage stage, String title, int width, int height) {
        try {
            URL resource = getClass().getResource(getFxmlPath());
            System.out.println("Resource: " + resource);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
            return loader.getController();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public abstract String getFxmlPath();
}
