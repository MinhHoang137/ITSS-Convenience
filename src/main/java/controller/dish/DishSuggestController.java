package controller.dish;

import java.util.List;

import controller.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.User;
import model.service.dish_suggest.DishSuggestService;
import model.service.fridge.FridgeService;
import session.Session;

public class DishSuggestController {

    @FXML
    private Button btnSuggest;
    @FXML
    private Button btnShowIngredients;
    @FXML
    private Button btnBack;

    @FXML
    private ListView<String> lvDishes;
    @FXML
    private ListView<String> lvIngredients;

    private final DishSuggestService suggestService = new DishSuggestService();
    private final FridgeService fridgeService = new FridgeService();

    private int fridgeId = -1;

    @FXML
    public void initialize() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            fridgeId = fridgeService.getFridgeIdByGroupId(currentUser.getGroupId());
        }

        btnSuggest.setOnAction(event -> handleSuggest());
        btnShowIngredients.setOnAction(event -> handleShowIngredients());
        btnBack.setOnAction(event -> handleBack());
    }

    private void handleSuggest() {
        if (fridgeId == -1) {
            showError("Không tìm thấy tủ lạnh.");
            return;
        }

        List<Dish> dishes = suggestService.suggestDishesFromFridge(fridgeId);
        lvDishes.getItems().clear();
        for (Dish d : dishes) {
            lvDishes.getItems().add("🍽 " + d.getName() + " — " + d.getDescription());
        }
    }

    private void handleShowIngredients() {
        if (fridgeId == -1) {
            showError("Không tìm thấy tủ lạnh.");
            return;
        }

        List<Ingredient> ingredients = fridgeService.getAllIngredients(fridgeId);
        lvIngredients.getItems().clear();
        for (Ingredient i : ingredients) {
            lvIngredients.getItems().add(i.getName() + " - " + i.getQuantity() + " " + i.getUnit());
        }
    }

    private void handleBack() {
        SceneSwitcher.switchScene(btnBack, "/itss/convenience/dashboard.fxml", "Trang chính");
    }

    private void showError(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
