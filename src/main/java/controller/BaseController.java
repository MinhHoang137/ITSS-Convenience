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
    /**
     * Phương thức này được sử dụng để tải giao diện từ file FXML và hiển thị nó trong một cửa sổ mới.
     * @param stage Cửa sổ sẽ hiển thị giao diện.
     * @param title Tiêu đề của cửa sổ.
     * @param width Chiều rộng của cửa sổ.
     * @param height Chiều cao của cửa sổ.
     * @return Trả về controller đã được khởi tạo từ file FXML, hoặc null nếu có lỗi xảy ra.
     */
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
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Phương thức này được sử dụng để lấy đường dẫn đến file FXML của controller.
     * Mỗi controller cần phải cung cấp đường dẫn FXML của mình.
     * @return Đường dẫn đến file FXML.
     */
    public abstract String getFxmlPath();
}
