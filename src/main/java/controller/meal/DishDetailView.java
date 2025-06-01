package controller.meal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.entity.Dish;
import model.entity.Ingredient;

public class DishDetailView {

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label eatTimeLabel;

    @FXML
    private Label eatDateLabel;

    @FXML
    private ListView<String> ingredientList;

    public void setDish(Dish dish) {
        nameLabel.setText(dish.getName());
        descriptionLabel.setText(dish.getDescription());
        eatTimeLabel.setText(dish.getEatTime().toString());
        eatDateLabel.setText(dish.getEatDate());

        ingredientList.getItems().clear();
        for (Ingredient i : dish.getIngredients()) {
            ingredientList.getItems().add(
                    String.format("%s: %.2f %s", i.getName(), i.getQuantity(), i.getUnit())
            );
        }
    }
}
