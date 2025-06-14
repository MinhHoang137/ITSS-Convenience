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

/**
 * Controller quản lý giao diện và xử lý logic cho màn hình danh sách mua sắm.
 * Cho phép người dùng tạo, xem, xóa các danh sách mua sắm theo nhóm.
 */
public class ShoppingListController {

    @FXML private DatePicker dateBuyDate;
    @FXML private TableView<ShoppingList> tableShoppingList;
    @FXML private TableColumn<ShoppingList, Integer> colId;
    @FXML private TableColumn<ShoppingList, LocalDate> colDate;
    @FXML private VBox sectionCreate;
    @FXML private Button btnDeleteList;

    private final IShoppingListService shoppingListService = new ShoppingListService();
    private final ObservableList<ShoppingList> listData = FXCollections.observableArrayList();

    /**
     * Khởi tạo controller khi FXML được load.
     * Gán dữ liệu cho bảng, xác định quyền chỉnh sửa, và tải danh sách mua sắm của nhóm hiện tại.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getShoppingListId()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getBuyDate()));
        tableShoppingList.setItems(listData);

        boolean isHousewife = Session.getCurrentUser().getRole().name().equalsIgnoreCase("HOUSEWIFE");

        sectionCreate.setVisible(isHousewife);
        sectionCreate.setManaged(isHousewife);
        btnDeleteList.setVisible(isHousewife);
        btnDeleteList.setManaged(isHousewife);

        loadShoppingListsByGroup();
    }

    /**
     * Tải tất cả danh sách mua sắm theo group của người dùng hiện tại.
     */
    private void loadShoppingListsByGroup() {
        int groupId = Session.getCurrentUser().getGroupId();
        List<ShoppingList> result = shoppingListService.getShoppingListsByGroupId(groupId);
        listData.setAll(result);
    }

    /**
     * Xử lý khi nhấn nút "Tạo danh sách mua sắm".
     * Kiểm tra hợp lệ, kiểm tra trùng ngày và thêm vào cơ sở dữ liệu.
     */
    @FXML
    public void handleAddShoppingList() {
        try {
            LocalDate date = dateBuyDate.getValue();
            int groupId = Session.getCurrentUser().getGroupId();

            if (date == null) {
                showAlert("Vui lòng chọn ngày mua.");
                return;
            }

            if (shoppingListService.isDateDuplicated(date, groupId)) {
                showAlert("Nhóm đã có danh sách mua sắm cho ngày này.");
                return;
            }

            ShoppingList list = new ShoppingList();
            list.setBuyDate(date);
            list.setGroupId(groupId);

            boolean success = shoppingListService.createShoppingList(list);
            if (success) {
                listData.add(list);
                clearInputs();
            } else {
                showAlert("Thêm không thành công. Kiểm tra dữ liệu.");
            }
        } catch (Exception e) {
            showAlert("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Xử lý khi nhấn nút "Xem danh sách".
     * Lưu lại ID danh sách được chọn vào session và chuyển sang màn hình nguyên liệu.
     */
    @FXML
    public void handleViewSelectedList() {
        ShoppingList selected = tableShoppingList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một danh sách.");
            return;
        }

        Session.setSelectedShoppingListId(selected.getShoppingListId());

        String role = Session.getCurrentUser().getRole().name();  // hiện tại chưa dùng đến
        switchScene("/itss/convenience/buy_ingredient.fxml");
    }

    /**
     * Xử lý khi nhấn nút "Quay lại".
     * Chuyển về màn hình dashboard chính.
     */
    @FXML
    public void handleBack() {
        switchScene("/itss/convenience/dashboard.fxml");
    }

    /**
     * Chuyển sang một màn hình khác theo đường dẫn FXML.
     *
     * @param fxmlPath Đường dẫn đến file FXML đích.
     */
    private void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) tableShoppingList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi nhấn nút "Xóa danh sách".
     * Hiển thị xác nhận, và nếu đồng ý thì xóa danh sách được chọn.
     */
    @FXML
    public void handleDeleteShoppingList() {
        ShoppingList selected = tableShoppingList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một danh sách.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa danh sách này?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = shoppingListService.deleteShoppingList(selected.getShoppingListId());
            if (deleted) {
                listData.remove(selected);
                showAlert("Đã xóa thành công.");
            } else {
                showAlert("Không thể xóa.");
            }
        }
    }

    /**
     * Hiển thị hộp thoại cảnh báo/thông báo.
     *
     * @param msg Nội dung hiển thị trong hộp thoại.
     */
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    /**
     * Xóa giá trị nhập trong DatePicker.
     */
    private void clearInputs() {
        dateBuyDate.setValue(null);
    }
}
