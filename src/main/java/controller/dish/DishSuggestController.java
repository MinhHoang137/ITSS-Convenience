package controller.dish;

import java.util.List;

import controller.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import model.dao.FridgeDAO;
import model.entity.Dish;
import model.entity.Ingredient;
import model.service.dish_suggest.DishSuggestService;
import model.service.dish_suggest.IDishSuggestService;
import session.Session;

public class DishSuggestController {

    @FXML private Button btnBack;
    @FXML private Button btnCook;
    @FXML private ListView<String> lvDishes;
    @FXML private ListView<String> lvIngredients;

    private final FridgeDAO fridgeDAO = FridgeDAO.getInstance();

    // ✅ Dùng interface thay vì class trực tiếp
    private final IDishSuggestService suggestService = new DishSuggestService();

    private List<Dish> cookableDishes;
    private int currentFridgeId;

    @FXML
    public void initialize() {
        // Lấy fridgeId từ groupId hiện tại của user
        currentFridgeId = fridgeDAO.getFridgeIdByGroupId(Session.getCurrentUser().getGroupId());

        // Hiển thị danh sách khi khởi động
        updateDishes();
        updateIngredients();

        btnCook.setOnAction(e -> handleCook());
        btnBack.setOnAction(e -> SceneSwitcher.switchScene(btnBack, "/itss/convenience/dashboard.fxml", "Trang chính"));
    }

    private void updateDishes() {
        cookableDishes = suggestService.suggestDishesFromFridge(currentFridgeId);
        lvDishes.getItems().clear();
        for (Dish d : cookableDishes) {
            lvDishes.getItems().add("🍽 " + d.getName() + " — " + d.getDescription());
        }
    }

    private void updateIngredients() {
        lvIngredients.getItems().clear();
        List<Ingredient> ingredients = fridgeDAO.getAllIngredients(currentFridgeId);
        for (Ingredient i : ingredients) {
            lvIngredients.getItems().add(i.getName() + " - " + i.getQuantity() + " " + i.getUnit());
        }
    }

    private void handleCook() {
        int selectedIndex = lvDishes.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= cookableDishes.size()) {
            showAlert("Vui lòng chọn món để nấu.");
            return;
        }

        Dish selectedDish = cookableDishes.get(selectedIndex);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận nấu ăn");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn nấu món '" + selectedDish.getName() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = fridgeDAO.consumeIngredientsForDish(selectedDish, currentFridgeId);
            if (success) {
                showInfo("Đã nấu món '" + selectedDish.getName() + "' và cập nhật nguyên liệu.");
                updateDishes();
                updateIngredients();
            } else {
                showAlert("Không thể trừ nguyên liệu. Kiểm tra lại tủ lạnh.");
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
