package controller.meal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.entity.Dish;

import java.net.URL;
import java.util.ResourceBundle;

public class DishCard implements Initializable {
    private boolean isSelected = false;
    private AddMealView addMealView;
    private Dish dish;
    private Parent parent;

    @FXML
    private Label dishNameText;
    @FXML
    private Button interactButton;

    @FXML
    private void OnClick(ActionEvent actionEvent) {
        addMealView = AddMealView.getCurrent();
        if (isSelected) {
            // Xóa món ăn khỏi danh sách đã chọn
            addMealView.removeDishFromMeal(this);
        } else {
            // Thêm món ăn vào danh sách đã chọn
            addMealView.addFoundDish(dish);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addMealView = AddMealView.getCurrent();
        if (dish != null) {
            dishNameText.setText(dish.getName());
        }
        interactButton.setText(isSelected ? "Xóa" : "Thêm");
    }

    public void setDish(Dish dish) {
        this.dish = dish;
        if (dish != null) {
            dishNameText.setText(dish.getName());
        }
    }

    public Dish getDish() {
        return dish;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
        interactButton.setText(selected ? "Xóa" : "Thêm");
    }

    public Parent getParent() {
        return parent;
    }

    @FXML
    private void OnDetailClick(ActionEvent actionEvent) {
        addMealView = AddMealView.getCurrent();
        addMealView.setDish(dish);
    }
}
