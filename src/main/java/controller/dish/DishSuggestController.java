package controller.dish;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.entity.Dish;
import model.service.dish_suggest.DishSuggestService;

public class DishSuggestController {

    @FXML
    private Button btnSuggest;

    @FXML
    private ListView<String> dishList;

    @FXML
    private Label lblStatus;

    private final DishSuggestService service = new DishSuggestService();

    @FXML
    public void initialize() {
        btnSuggest.setOnAction(e -> suggestDishes());
    }

    private void suggestDishes() {
        dishList.getItems().clear();
        List<Dish> dishes = service.suggestCookableDishes(1);

        if (dishes.isEmpty()) {
            lblStatus.setText("Không có món nào đủ nguyên liệu!");
        } else {
            lblStatus.setText("Các món có thể nấu được:");
            for (Dish dish : dishes) {
                dishList.getItems().add(dish.getName());
            }
        }
    }
}
