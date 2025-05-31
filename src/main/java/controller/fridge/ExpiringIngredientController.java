package controller.fridge;

import controller.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.Ingredient;
import model.entity.Unit;
import model.service.fridge.FridgeService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ExpiringIngredientController extends BaseController {

    @FXML
    private TableView<Ingredient> tableView;

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
    private final int fridgeId = 1; // ⚠️ Bạn có thể truyền thực tế từ user đăng nhập

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        expirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        tableView.setItems(loadExpiringIngredients());
    }

    private ObservableList<Ingredient> loadExpiringIngredients() {
        List<Ingredient> list = fridgeService.getExpiringIngredients(fridgeId, 3);
        return FXCollections.observableArrayList(list);
    }

    @Override
    public String getFxmlPath() {
        return "/view/fridge/expiring.fxml";
    }
}
