package controller.fridge;

import controller.BaseController;
import controller.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.FridgeService;
import javafx.scene.control.Button;
import session.Session;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

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

    private final FridgeService fridgeService = new FridgeService();
    private int fridgeId ; // ⚠️ Bạn có thể truyền thực tế từ user đăng nhập

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int groupId = Session.getCurrentUser().getGroupId();
        // Lấy fridgeId từ groupId
        fridgeId = fridgeService.getFridgeIdByGroupId(groupId);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        expirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        expiredTableView.setItems(loadExpiringIngredients());
    }

    private ObservableList<Ingredient> loadExpiringIngredients() {
        List<Ingredient> list = fridgeService.getExpiringIngredients(fridgeId, 3);
        return FXCollections.observableArrayList(list);
    }
    @FXML
    public void goBack() {

        SceneSwitcher.switchScene(goBack, "/fridge/fridge.fxml", "Nguyên liệu trong tủ lạnh");

    }

    @Override
    public String getFxmlPath() {
        return "/fridge/expiring.fxml";
    }
}
