package itss.convenience;

import controller.fridge.FridgeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/fridge/fridge.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 470, 450);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        // new FridgeController().loadAndShow(stage, "Fridge Management", 1000, 1600);
    }

    public static void main(String[] args) {
        launch();
    }
}