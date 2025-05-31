package controller.shopping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import model.entity.ShoppingList;
import model.service.IShoppingListService;
import model.service.ShoppingListService;
import session.Session;

import java.time.LocalDate;
import java.util.List;

public class ShoppingListController {

    @FXML private DatePicker dateBuyDate;
    @FXML private TableView<ShoppingList> tableShoppingList;
    @FXML private TableColumn<ShoppingList, Integer> colId;
    @FXML private TableColumn<ShoppingList, LocalDate> colDate;
    @FXML private VBox sectionCreate;
    @FXML private Button btnDeleteList;
    private final IShoppingListService shoppingListService = new ShoppingListService();
    private final ObservableList<ShoppingList> listData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ánh xạ dữ liệu cho các cột
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getShoppingListId()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getBuyDate()));
        tableShoppingList.setItems(listData);

        // Kiểm tra quyền HOUSEWIFE
        boolean isHousewife = Session.getCurrentUser().getRole().name().equalsIgnoreCase("HOUSEWIFE");

        // HOUSEWIFE mới được tạo + xóa
        sectionCreate.setVisible(isHousewife);
        sectionCreate.setManaged(isHousewife);

        btnDeleteList.setVisible(isHousewife);
        btnDeleteList.setManaged(isHousewife);

        // Load danh sách theo group người dùng
        loadShoppingListsByGroup();
    }


    private void loadShoppingListsByGroup() {
        int groupId = Session.getCurrentUser().getGroupId();
        List<ShoppingList> result = shoppingListService.getShoppingListsByGroupId(groupId);
        listData.setAll(result);
    }

    @FXML
    public void handleAddShoppingList() {
        LocalDate date = dateBuyDate.getValue();
        int groupId = Session.getCurrentUser().getGroupId();

        if (date == null) {
            showAlert("Vui lòng chọn ngày mua.");
            return;
        }

        ShoppingList list = new ShoppingList();
        list.setBuyDate(date);
        list.setGroupId(groupId);

        boolean success = shoppingListService.createShoppingList(list);
        if (success) {
            listData.add(list);
            dateBuyDate.setValue(null);
        } else {
            showAlert("Không thể thêm danh sách. Vui lòng thử lại.");
        }
    }

    @FXML
    public void handleViewSelectedList() {
        ShoppingList selected = tableShoppingList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một danh sách.");
            return;
        }

        // Lưu lại ID danh sách được chọn
        Session.setSelectedShoppingListId(selected.getShoppingListId());

        String role = Session.getCurrentUser().getRole().name();
        switchScene("/itss/convenience/buy_ingredient.fxml");
    }

    @FXML
    public void handleBack() {
        switchScene("/itss/convenience/dashboard.fxml");
    }

    private void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) tableShoppingList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleDeleteShoppingList() {
        ShoppingList selected = tableShoppingList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn một danh sách.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa danh sách này?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = shoppingListService.deleteShoppingList(selected.getShoppingListId());
            if (deleted) {
                listData.remove(selected);
                showAlert("✅ Đã xóa thành công.");
            } else {
                showAlert("❌ Không thể xóa.");
            }
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
