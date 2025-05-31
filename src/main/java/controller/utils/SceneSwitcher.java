package controller.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

public class SceneSwitcher {
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
