package controller.dish;

import java.util.List;

import controller.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import model.entity.Dish;
import model.entity.Fridge;
import model.entity.Ingredient;
import model.service.dish_suggest.DishSuggestService;
import model.service.fridge.FridgeService;

public class DishSuggestController {

    @FXML
    private Button btnSuggest;
    @FXML
    private Button btnShowIngredients;
    @FXML
    private Button btnBack;

    @FXML
    private ComboBox<Fridge> fridgeSelector;
    @FXML
    private ListView<String> lvDishes;
    @FXML
    private ListView<String> lvIngredients;

    private final DishSuggestService suggestService = new DishSuggestService();
    private final FridgeService fridgeService = new FridgeService();

    @FXML
    public void initialize() {
        fridgeSelector.getItems().addAll(fridgeService.getAllFridges());
        btnSuggest.setOnAction(event -> handleSuggest());
        btnShowIngredients.setOnAction(event -> handleShowIngredients());
        btnBack.setOnAction(event -> handleBack());
    }

    private void handleSuggest() {
        Fridge selectedFridge = fridgeSelector.getValue();
        if (selectedFridge == null) {
            showError("Vui lòng chọn tủ lạnh.");
            return;
        }

        List<Dish> dishes = suggestService.suggestDishesFromFridge(selectedFridge.getId());
        lvDishes.getItems().clear();
        for (Dish d : dishes) {
            lvDishes.getItems().add("🍽 " + d.getName() + " — " + d.getDescription());
        }
    }

    private void handleShowIngredients() {
        Fridge selectedFridge = fridgeSelector.getValue();
        if (selectedFridge == null) {
            showError("Vui lòng chọn tủ lạnh.");
            return;
        }

        List<Ingredient> ingredients = fridgeService.getAllIngredients(selectedFridge.getId());
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
