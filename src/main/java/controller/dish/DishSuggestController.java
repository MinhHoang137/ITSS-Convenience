package controller.dish;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import model.entity.Dish;
import model.entity.Ingredient;
import model.service.fridge.FridgeService;
import model.service.dish_suggest.DishSuggestService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DishSuggestController implements Initializable {

    @FXML private Button btnSuggest;
    @FXML private Button btnShowIngredients;
    @FXML private ComboBox<Integer> fridgeSelector;
    @FXML private ListView<String> lvDishes;
    @FXML private ListView<String> lvIngredients;

    private final DishSuggestService suggestService = new DishSuggestService();
    private final FridgeService fridgeService = new FridgeService();

    private int selectedFridgeId = 1; // mặc định chọn tủ 1

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gán danh sách tủ lạnh
        fridgeSelector.getItems().addAll(1, 2);
        fridgeSelector.setValue(1); // mặc định

        fridgeSelector.setOnAction(e -> {
            Integer id = fridgeSelector.getValue();
            if (id != null) {
                selectedFridgeId = id;
            }
        });

        btnSuggest.setOnAction(e -> handleSuggest());
        btnShowIngredients.setOnAction(e -> handleShowIngredients());
    }

    private void handleSuggest() {
        lvDishes.getItems().clear();
        List<Dish> dishes = suggestService.suggestDishesFromFridge(selectedFridgeId);
        if (dishes.isEmpty()) {
            lvDishes.getItems().add("Không có món nào phù hợp nguyên liệu.");
        } else {
            for (Dish dish : dishes) {
                lvDishes.getItems().add(dish.getName() + " - " + dish.getDescription());
            }
        }
    }

    private void handleShowIngredients() {
        lvIngredients.getItems().clear();
        List<Ingredient> ingredients = fridgeService.getAllIngredients(selectedFridgeId);
        if (ingredients.isEmpty()) {
            lvIngredients.getItems().add("Tủ lạnh không có nguyên liệu nào.");
        } else {
            for (Ingredient ing : ingredients) {
                lvIngredients.getItems().add("- " + ing.getName() + ": " + ing.getQuantity() + " " + ing.getUnit());
            }
        }
    }
}
