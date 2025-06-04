package controller.fridge;

import controller.BaseController;
import controller.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dao.FridgeDAO;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.FridgeService;
import javafx.scene.control.Button;
import model.service.fridge.IngredientService;
import session.Session;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình hiển thị nguyên liệu sắp hết hạn trong tủ lạnh.
 * Hiển thị danh sách các nguyên liệu có ngày hết hạn gần và cho phép quay lại màn hình trước đó.
 */
public class ExpiringIngredientController extends BaseController {

    @FXML
    private TableView<Ingredient> expiredTableView;

    @FXML
    private Button goBack;

    @FXML
    private TableColumn<Ingredient, Integer> id;

    @FXML
    private TableColumn<Ingredient, String> name;

    @FXML
    private TableColumn<Ingredient, Double> quantity;

    @FXML
    private TableColumn<Ingredient, Unit> unit;

    @FXML
    private TableColumn<Ingredient, LocalDate> expirationDate;

    /** Service để truy xuất dữ liệu nguyên liệu từ database. */


    /** ID của tủ lạnh, được xác định từ groupId của người dùng hiện tại. */
    private int fridgeId;

    private final FridgeDAO fridgeDAO = FridgeDAO.getInstance();

    /**
     * Khởi tạo controller. Thiết lập dữ liệu ban đầu cho bảng nguyên liệu sắp hết hạn.
     * @param url URL được truyền vào (không sử dụng).
     * @param resourceBundle ResourceBundle được truyền vào (không sử dụng).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int groupId = Session.getCurrentUser().getGroupId();
        fridgeId = fridgeDAO.getFridgeIdByGroupId(groupId);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        expirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        expiredTableView.setItems(loadExpiringIngredients());
    }

    /**
     * Tải danh sách nguyên liệu sắp hết hạn từ cơ sở dữ liệu (trong vòng 3 ngày tới).
     * @return Danh sách nguyên liệu sắp hết hạn dưới dạng ObservableList.
     */
    private ObservableList<Ingredient> loadExpiringIngredients() {
        List<Ingredient> list = fridgeDAO.getExpiringIngredients(fridgeId, 3);
        return FXCollections.observableArrayList(list);
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút quay lại.
     * Chuyển về màn hình hiển thị tủ lạnh.
     */
    @FXML
    public void goBack() {
        SceneSwitcher.switchScene(goBack, "/fridge/fridge.fxml", "Nguyên liệu trong tủ lạnh");
    }

    /**
     * Trả về đường dẫn FXML của màn hình hiện tại.
     * @return Chuỗi đường dẫn tới file FXML.
     */
    @Override
    public String getFxmlPath() {
        return "/fridge/expiring.fxml";
    }
}
