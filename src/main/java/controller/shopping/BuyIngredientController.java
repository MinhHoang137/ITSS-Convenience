package controller.shopping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.BuyIngredient;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.BuyIngredientService;
import model.service.IBuyIngredientService;
import model.service.fridge.*;
import session.Session;

import java.util.List;

/**
 * Controller quản lý giao diện và hành vi của màn hình danh sách nguyên liệu cần mua.
 */
public class BuyIngredientController {

    @FXML private TextField txtIngredientName;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<Unit> cbUnit;

    @FXML private TableView<BuyIngredient> tableIngredient;
    @FXML private TableColumn<BuyIngredient, String> colName;
    @FXML private TableColumn<BuyIngredient, Double> colQty;
    @FXML private TableColumn<BuyIngredient, String> colUnit;
    @FXML private Button btnDeleteIngredient;

    @FXML private VBox sectionAdd;

    private final IBuyIngredientService buyIngredientService = new BuyIngredientService();
    private final ObservableList<BuyIngredient> data = FXCollections.observableArrayList();

    private final int shoppingListId = Session.getSelectedShoppingListId();
    private final int groupId = Session.getCurrentUser().getGroupId();

    /**
     * Khởi tạo dữ liệu khi controller được load:
     * - Gán giá trị cho bảng.
     * - Kiểm tra vai trò người dùng để hiển thị quyền chỉnh sửa.
     * - Tải nguyên liệu từ danh sách mua.
     */
    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitType"));

        tableIngredient.setItems(data);
        tableIngredient.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        boolean isHousewife = Session.getCurrentUser().getRole().name().equalsIgnoreCase("HOUSEWIFE");
        sectionAdd.setVisible(isHousewife);
        sectionAdd.setManaged(isHousewife);
        btnDeleteIngredient.setVisible(isHousewife);
        btnDeleteIngredient.setManaged(isHousewife);

        cbUnit.getItems().addAll(Unit.values());
        cbUnit.setValue(Unit.kg);

        loadIngredients();
    }

    /**
     * Tải danh sách nguyên liệu từ service và gán vào bảng.
     */
    private void loadIngredients() {
        List<BuyIngredient> list = buyIngredientService.getIngredientsByShoppingListId(shoppingListId);
        data.setAll(list);
    }

    /**
     * Xử lý khi người dùng nhấn nút "Thêm nguyên liệu".
     * Thêm nguyên liệu vào danh sách mua nếu hợp lệ.
     */
    @FXML
    public void handleAddIngredient() {
        try {
            String name = txtIngredientName.getText();
            double qty = Double.parseDouble(txtQuantity.getText());
            String unit = cbUnit.getValue().toString();

            BuyIngredient bi = new BuyIngredient(shoppingListId, name, qty, unit);
            if (buyIngredientService.addIngredientToList(bi)) {
                data.add(bi);
                clearInputFields();
            } else {
                showAlert("Thêm thất bại. Có thể nguyên liệu đã tồn tại.");
            }
        } catch (Exception e) {
            showAlert("Lỗi nhập liệu: " + e.getMessage());
        }
    }

    /**
     * Xử lý khi người dùng nhấn "Chuyển vào tủ lạnh".
     * Chuyển các nguyên liệu đã chọn sang tủ lạnh và xóa khỏi danh sách mua.
     */
    @FXML
    public void handleTransferToFridge() {
        List<BuyIngredient> selected = tableIngredient.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            showAlert("Vui lòng chọn ít nhất một nguyên liệu để chuyển.");
            return;
        }

        FridgeService fridgeService = new FridgeService();
        IngredientService ingredientService = new IngredientService();
        int fridgeId = fridgeService.getFridgeIdByGroupId(groupId);

        for (BuyIngredient bi : selected) {
            Ingredient ing = new Ingredient();
            ing.setName(bi.getIngredientName());
            ing.setQuantity(bi.getQuantity());
            ing.setUnit(Unit.valueOf(bi.getUnitType()));
            ing.setExpirationDate(java.time.LocalDate.now().plusDays(30));

            ingredientService.addIngredientToFridge(ing, fridgeId);
            buyIngredientService.removeIngredientFromList(shoppingListId, bi.getIngredientName());
        }

        loadIngredients();
        showAlert("Đã chuyển nguyên liệu vào tủ lạnh.");
    }

    /**
     * Quay trở về giao diện danh sách mua sắm.
     */
    @FXML
    public void handleBack() {
        switchScene("/itss/convenience/shopping_list.fxml");
    }

    /**
     * Xóa các giá trị trong ô nhập nguyên liệu.
     */
    private void clearInputFields() {
        txtIngredientName.clear();
        txtQuantity.clear();
    }

    /**
     * Chuyển sang scene khác được chỉ định bởi đường dẫn FXML.
     *
     * @param path Đường dẫn đến file FXML cần chuyển đến.
     */
    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) tableIngredient.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi người dùng nhấn "Xóa nguyên liệu".
     * Hiển thị hộp thoại xác nhận và xóa nguyên liệu nếu được đồng ý.
     */
    @FXML
    public void handleDeleteIngredient() {
        BuyIngredient selected = tableIngredient.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một nguyên liệu.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa nguyên liệu này khỏi danh sách?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = buyIngredientService.removeIngredientFromList(shoppingListId, selected.getIngredientName());
            if (deleted) {
                data.remove(selected);
                showAlert("Đã xóa nguyên liệu.");
            } else {
                showAlert("Xóa thất bại.");
            }
        }
    }

    /**
     * Hiển thị hộp thoại thông báo với nội dung cho trước.
     *
     * @param msg Nội dung cần hiển thị.
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
