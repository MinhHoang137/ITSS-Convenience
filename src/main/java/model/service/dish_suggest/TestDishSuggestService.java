package model.service.dish_suggest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestDishSuggestService extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dish/dish_suggest.fxml")); // ✅ Đường dẫn đúng
        Parent root = loader.load();

        stage.setTitle("Test Gợi ý món ăn");
        stage.setScene(new Scene(root, 700, 550));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
