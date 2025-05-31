package controller.fridge;

import controller.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.FridgeService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    private final int fridgeId = 1; // Nếu bạn có hệ thống nhiều tủ lạnh, sửa thành giá trị động.

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        new ExpiringIngredientController().loadAndShow(new Stage(), "Nguyên liệu sắp hết hạn", 800, 600);
    }

    @Override
    public String getFxmlPath() {
        return "/view/fridge/fridge.fxml";
    }
}
