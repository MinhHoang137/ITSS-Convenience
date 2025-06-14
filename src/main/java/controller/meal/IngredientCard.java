/**
 * IngredientCard.java
 * Lớp này đại diện cho một thẻ nguyên liệu trong giao diện người dùng,
 * cho phép người dùng nhập và chỉnh sửa thông tin về nguyên liệu,
 * bao gồm tên, số lượng và đơn vị.
 * * Nó cũng cung cấp chức năng xóa nguyên liệu khỏi danh sách.
 * * @author Minh Hoàng
 */
package controller.meal;

import controller.dish.DishUpdateAdminView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.entity.Ingredient;
import model.entity.Unit;

import java.util.function.UnaryOperator;

public class IngredientCard {


    private Ingredient ingredient;
    @FXML
    private TextFormatter numberFormatter;
    @FXML
    private TextField nameField;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private Button deleteButton;

    private VBox ingredientHolder;

    @FXML
    public void initialize() {
        // Chỉ cho nhập số và dấu chấm
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };
        quantityField.setTextFormatter(new TextFormatter<>(filter));

        // Đơn vị (có thể load từ đâu đó)
        for (Unit unit : Unit.values()) {
            unitComboBox.getItems().add(unit.toString());
        }
    }
    @FXML
    private void OnDeleteIngredient(ActionEvent actionEvent) {
        DishUpdateAdminView.getCurrent().deleteIngredient(ingredient);
    }

    /**
     * Thiết lập thông tin cho IngredientCard từ một đối tượng Ingredient.
     * @param ingredient Đối tượng Ingredient chứa thông tin cần hiển thị.
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        nameField.setText(ingredient.getName());
        quantityField.setText(String.valueOf(ingredient.getQuantity()));
        unitComboBox.setValue(ingredient.getUnit().toString());
    }

    /**
     * Lấy thông tin từ IngredientCard và tạo một đối tượng Ingredient mới.
     * @return Đối tượng Ingredient chứa thông tin từ các trường nhập liệu.
     */
    public Ingredient getIngredient() {
        if (ingredient == null) {
            ingredient = new Ingredient();
        }
        ingredient.setName(nameField.getText());
        ingredient.setQuantity(Double.parseDouble(quantityField.getText()));
        ingredient.setUnit(Unit.valueOf(unitComboBox.getValue()));
        return ingredient;
    }
    public void setIngredientHolder(VBox ingredientHolder) {
        this.ingredientHolder = ingredientHolder;
    }
}
