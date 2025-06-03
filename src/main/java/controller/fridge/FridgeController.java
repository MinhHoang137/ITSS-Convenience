package controller.fridge;

import controller.BaseController;
import controller.utils.SceneSwitcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.FridgeService;
import session.Session;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho giao diện hiển thị danh sách nguyên liệu trong tủ lạnh.
 * Hỗ trợ tìm kiếm nguyên liệu, chuyển đến giao diện kiểm tra hạn sử dụng,
 * và quay lại màn hình chính.
 */
public class FridgeController extends BaseController implements Initializable {

    @FXML
    private Button expireCheck;

    @FXML
    private Button goBack;

    @FXML
    private TableView<Ingredient> tableView;

    @FXML
    private TableColumn<Ingredient, String> name;

    @FXML
    private TableColumn<Ingredient, Double> quantity;

    @FXML
    private TableColumn<Ingredient, Unit> unit;

    @FXML
    private javafx.scene.control.TextField searchField;

    /** Service để thao tác với dữ liệu nguyên liệu trong tủ lạnh. */
    private final FridgeService fridgeService = new FridgeService();

    /** ID của tủ lạnh, được ánh xạ từ groupId của người dùng hiện tại. */
    private int fridgeId;

    /**
     * Hàm khởi tạo được gọi khi giao diện được load.
     * Gắn dữ liệu cho bảng và thiết lập hành vi tìm kiếm.
     * @param location URL không được sử dụng.
     * @param resources ResourceBundle không được sử dụng.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int groupId = Session.getCurrentUser().getGroupId();
        fridgeId = fridgeService.getFridgeIdByGroupId(groupId);

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadIngredients();
        searchField.setOnAction(e -> onSearch(null));
    }

    /**
     * Xử lý tìm kiếm nguyên liệu dựa trên từ khóa người dùng nhập.
     * Nếu ô tìm kiếm rỗng thì hiển thị toàn bộ danh sách.
     * @param event Sự kiện kích hoạt (có thể null do gọi trực tiếp).
     */
    @FXML
    private void onSearch(ActionEvent event) {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadIngredients();
            return;
        }

        List<Ingredient> all = fridgeService.getAllIngredients(fridgeId);
        List<Ingredient> filtered = all.stream()
                .filter(i -> i.getName().toLowerCase().contains(keyword))
                .toList();

        ObservableList<Ingredient> observableList = FXCollections.observableArrayList(filtered);
        tableView.setItems(observableList);
    }

    /**
     * Tải và hiển thị danh sách toàn bộ nguyên liệu trong tủ lạnh.
     */
    private void loadIngredients() {
        List<Ingredient> ingredients = fridgeService.getAllIngredients(fridgeId);
        ObservableList<Ingredient> observableList = FXCollections.observableArrayList(ingredients);
        tableView.setItems(observableList);
    }

    /**
     * Chuyển sang giao diện kiểm tra nguyên liệu sắp hết hạn.
     * @param event Sự kiện khi nhấn nút kiểm tra nguyên liệu hết hạn.
     */
    @FXML
    private void getexpiredIngre(ActionEvent event) {
        SceneSwitcher.switchScene(expireCheck, "/fridge/expiring.fxml", "Nguyên liệu còn hạn dưới 3 ngày");
    }

    /**
     * Quay lại giao diện chính (dashboard).
     */
    @FXML
    public void goBack() {
        SceneSwitcher.switchScene(goBack, "/itss/convenience/dashboard.fxml", "Trang chủ");
    }

    /**
     * Trả về đường dẫn đến file FXML tương ứng với controller này.
     * @return Đường dẫn FXML của giao diện tủ lạnh.
     */
    @Override
    public String getFxmlPath() {
        return "/fridge/fridge.fxml";
    }
}
