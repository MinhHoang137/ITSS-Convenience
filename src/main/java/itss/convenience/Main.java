package itss.convenience;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML bằng đường dẫn tuyệt đối từ classpath root
        var url = getClass().getResource("login.fxml");
        if (url == null) {
            System.err.println("⚠️ Không tìm thấy FXML: /itss.convenience/shopping_list.fxml");
            return;
        }

        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Test Giao Diện Shopping List");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
