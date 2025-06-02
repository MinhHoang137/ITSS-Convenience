package controller.fridge;

import controller.BaseController;
import controller.utils.SceneSwitcher;
import controller.DashboardController;
import controller.ViewController;
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

import static controller.utils.SceneSwitcher.switchScene;

public class FridgeController extends BaseController implements Initializable {

    @FXML
    private Button expireCheck;
    @FXML
    private TableView<Ingredient> tableView;

    @FXML
    private TableColumn<Ingredient, String> name;

    @FXML
    private TableColumn<Ingredient, Double> quantity;

    @FXML
    private TableColumn<Ingredient, Unit> unit;

    private final FridgeService fridgeService = new FridgeService();

    private int fridgeId; // Không khởi tạo mặc định nữa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lấy groupId từ session
        int groupId = Session.getCurrentUser().getGroupId();
        // Lấy fridgeId từ groupId
        fridgeId = fridgeService.getFridgeIdByGroupId(groupId);

        // Gắn cột với thuộc tính tương ứng trong entity Ingredient
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadIngredients();
    }

    private void loadIngredients() {
        List<Ingredient> ingredients = fridgeService.getAllIngredients(fridgeId);
        ObservableList<Ingredient> observableList = FXCollections.observableArrayList(ingredients);
        tableView.setItems(observableList);
    }

    @FXML
    private void getexpiredIngre(ActionEvent event) {
        switchScene(expireCheck, "/fridge/expiring.fxml", "Nguyên liệu sắp hết hạn");
    }
    @FXML
    public void goBack() {

    }


    @Override
    public String getFxmlPath() {
        return "/fridge/fridge.fxml";
    }
}
