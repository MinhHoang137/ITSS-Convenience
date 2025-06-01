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
import model.service.fridge.FridgeService;
import session.Session;

import java.util.List;

public class BuyIngredientController {

    @FXML
    private TextField txtIngredientName;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtUnit;

    @FXML
    private TableView<BuyIngredient> tableIngredient;
    @FXML
    private TableColumn<BuyIngredient, String> colName;
    @FXML
    private TableColumn<BuyIngredient, Double> colQty;
    @FXML
    private TableColumn<BuyIngredient, String> colUnit;
    @FXML
    private Button btnDeleteIngredient;

    @FXML
    private VBox sectionAdd;

    private final IBuyIngredientService buyIngredientService = new BuyIngredientService();
    private final ObservableList<BuyIngredient> data = FXCollections.observableArrayList();

    private final int shoppingListId = Session.getSelectedShoppingListId();
    private final int groupId = Session.getCurrentUser().getGroupId();

    @FXML
    public void initialize() {
        // Gán dữ liệu cho các cột trong bảng
        colName.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitType"));

        // Cho phép chọn nhiều dòng để chuyển sang tủ lạnh
        tableIngredient.setItems(data);
        tableIngredient.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Chỉ HOUSEWIFE mới thấy phần thêm nguyên liệu & nút xóa
        boolean isHousewife = Session.getCurrentUser().getRole().name().equalsIgnoreCase("HOUSEWIFE");

        sectionAdd.setVisible(isHousewife);
        sectionAdd.setManaged(isHousewife);

        btnDeleteIngredient.setVisible(isHousewife);
        btnDeleteIngredient.setManaged(isHousewife);

        // Tải nguyên liệu cho shoppingList hiện tại
        loadIngredients();
    }

    private void loadIngredients() {
        List<BuyIngredient> list = buyIngredientService.getIngredientsByShoppingListId(shoppingListId);
        data.setAll(list);
    }

    @FXML
    public void handleAddIngredient() {
        try {
            String name = txtIngredientName.getText();
            double qty = Double.parseDouble(txtQuantity.getText());
            String unit = txtUnit.getText();

            BuyIngredient bi = new BuyIngredient(shoppingListId, name, qty, unit);
            if (buyIngredientService.addIngredientToList(bi)) {
                data.add(bi);
                clearInputFields();
            } else {
                showAlert("❌ Thêm thất bại. Có thể nguyên liệu đã tồn tại.");
            }
        } catch (Exception e) {
            showAlert("❗ Lỗi nhập liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleTransferToFridge() {
        List<BuyIngredient> selected = tableIngredient.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            showAlert("⚠️ Vui lòng chọn ít nhất một nguyên liệu để chuyển.");
            return;
        }

        FridgeService fridgeService = new FridgeService();
        int groupId = Session.getCurrentUser().getGroupId();
        int fridgeId = fridgeService.getFridgeIdByGroupId(groupId);

        for (BuyIngredient bi : selected) {
            Ingredient ing = new Ingredient();

            ing.setName(bi.getIngredientName());
            ing.setQuantity(bi.getQuantity());
            ing.setUnit(Unit.valueOf(bi.getUnitType()));
            ing.setExpirationDate(java.time.LocalDate.now().plusDays(30));

            fridgeService.addIngredientToFridge(ing, fridgeId);
            buyIngredientService.removeIngredientFromList(shoppingListId, bi.getIngredientName()); // Xoá khỏi danh sách
                                                                                                   // mua
        }

        loadIngredients(); // Reload lại bảng
        showAlert("✅ Đã chuyển nguyên liệu vào tủ lạnh.");
    }

    @FXML
    public void handleBack() {
        switchScene("/itss/convenience/shopping_list.fxml");
    }

    private void clearInputFields() {
        txtIngredientName.clear();
        txtQuantity.clear();
        txtUnit.clear();
    }

    private void switchScene(String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) tableIngredient.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteIngredient() {
        BuyIngredient selected = tableIngredient.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn một nguyên liệu.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa nguyên liệu này khỏi danh sách?", ButtonType.YES,
                ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = buyIngredientService.removeIngredientFromList(shoppingListId,
                    selected.getIngredientName());
            if (deleted) {
                data.remove(selected);
                showAlert("✅ Đã xóa nguyên liệu.");
            } else {
                showAlert("❌ Xóa thất bại.");
            }
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
