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
import model.service.fridge.DishService;
import model.service.fridge.FridgeService;
import model.service.fridge.IngredientService;
import session.Session;

/**
 * Controller điều khiển giao diện gợi ý món ăn từ tủ lạnh.
 * Cho phép người dùng xem danh sách món có thể nấu và nguyên liệu hiện có,
 * đồng thời thực hiện thao tác "nấu ăn" để trừ nguyên liệu tương ứng.
 */
public class DishSuggestController {

    @FXML private Button btnBack;
    @FXML private Button btnCook;
    @FXML private ListView<String> lvDishes;
    @FXML private ListView<String> lvIngredients;

    private final FridgeDAO fridgeDAO = FridgeDAO.getInstance();
    private final DishSuggestService suggestService = new DishSuggestService();
    private final FridgeService fridgeService = new FridgeService();
    private final IngredientService ingredientService = new IngredientService();
    private final DishService dishService = new DishService();
    private List<Dish> cookableDishes;
    private int currentFridgeId;

    /**
     * Phương thức khởi tạo giao diện, gọi khi FXML được load.
     * Thiết lập fridgeId từ người dùng hiện tại và cập nhật danh sách món ăn, nguyên liệu.
     */
    @FXML
    public void initialize() {
        // ✅ Lấy fridgeId từ groupId của user hiện tại
        currentFridgeId = fridgeDAO.getFridgeIdByGroupId(Session.getCurrentUser().getGroupId());

        // ✅ Hiển thị danh sách món và nguyên liệu ngay khi mở giao diện
        updateDishes();
        updateIngredients();

        btnCook.setOnAction(e -> handleCook());
        btnBack.setOnAction(e -> SceneSwitcher.switchScene(btnBack, "/itss/convenience/dashboard.fxml", "Trang chính"));
    }

    /**
     * Cập nhật danh sách món ăn có thể nấu từ nguyên liệu trong tủ lạnh.
     */
    private void updateDishes() {
        cookableDishes = suggestService.suggestDishesFromFridge(currentFridgeId);
        lvDishes.getItems().clear();
        for (Dish d : cookableDishes) {
            lvDishes.getItems().add("🍽 " + d.getName() + " — " + d.getDescription());
        }
    }

    /**
     * Cập nhật danh sách nguyên liệu đang có trong tủ lạnh.
     */
    private void updateIngredients() {
        lvIngredients.getItems().clear();

        List<Ingredient> ingredients = fridgeDAO.getAllIngredients(currentFridgeId);

        for (Ingredient i : ingredients) {
            lvIngredients.getItems().add(i.getName() + " - " + i.getQuantity() + " " + i.getUnit());
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút "Nấu ăn".
     * Kiểm tra món được chọn, xác nhận với người dùng, và thực hiện trừ nguyên liệu nếu thành công.
     */
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

    /**
     * Hiển thị hộp thoại cảnh báo với nội dung tuỳ chỉnh.
     *
     * @param message Nội dung cảnh báo.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông báo thành công với nội dung tuỳ chỉnh.
     *
     * @param message Nội dung thông báo.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
