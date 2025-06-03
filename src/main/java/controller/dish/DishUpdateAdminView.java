package controller.dish;

import controller.BaseController;
import controller.meal.IngredientCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.dao.MealPlanDAO;
import model.entity.Dish;
import model.entity.Ingredient;
import model.entity.MealType;
import model.entity.Unit;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class DishUpdateAdminView extends BaseController {
    private static DishUpdateAdminView current;

    @FXML private VBox ingredientContainer;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<MealType> eatTimeComboBox;
    @FXML private ComboBox<String> eatDateComboBox;
    @FXML private Button addIngredientButton;
    @FXML private Button saveButton;

    private final ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();
    private Dish currentDish;

    private ArrayList<IngredientCard> ingredientCards = new ArrayList<>();

    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();


    public void setDish(Dish dish) {
        this.currentDish = dish;
        nameField.setText(dish.getName());
        descriptionArea.setText(dish.getDescription());
        eatTimeComboBox.setValue(dish.getEatTime());
        eatDateComboBox.setValue(Dish.weekdays[dish.getEatDateIndex()]);
        ingredientList.setAll(dish.getIngredients());
        setIngredients();
    }
    private void setIngredients() {
        ingredientContainer.getChildren().clear();
        ingredientCards.clear();
        for (Ingredient ingredient : ingredientList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dish/ingredient_card.fxml"));
            try{
                Parent card = loader.load();
                IngredientCard ingredientCard = loader.getController();
                ingredientCard.setIngredient(ingredient);
                ingredientCards.add(ingredientCard);
                ingredientContainer.getChildren().add(card);
            } catch (Exception e){
                System.out.println("Error loading ingredient card: " + e.getMessage());
            }
        }
    }
    public void deleteIngredient(Ingredient ingredient) {
        ingredientList.remove(ingredient);
        setIngredients();
    }
    @FXML
    private void onAddIngredient() {
        Dialog<Pair<String, Pair<Double, Unit>>> dialog = new Dialog<>();
        dialog.setTitle("Thêm nguyên liệu mới");

        ButtonType addButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField quantityField = new TextField();
        ComboBox<Unit> unitComboBox = new ComboBox<>(FXCollections.observableArrayList(Unit.values()));

        grid.add(new Label("Tên:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Số lượng:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Đơn vị:"), 0, 2);
        grid.add(unitComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    double quantity = Double.parseDouble(quantityField.getText());
                    return new Pair<>(nameField.getText(), new Pair<>(quantity, unitComboBox.getValue()));
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Pair<String, Pair<Double, Unit>>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String name = pair.getKey();
            double quantity = pair.getValue().getKey();
            Unit unit = pair.getValue().getValue();
            ingredientList.add(new Ingredient(0, name, quantity, unit));
            setIngredients();
        });
    }

    @FXML
    private void onSave() {
        if (currentDish == null) {
            currentDish = new Dish();
        }
        currentDish.setName(nameField.getText());
        currentDish.setDescription(descriptionArea.getText());
        currentDish.setEatTime(eatTimeComboBox.getValue());
        currentDish.setEatDate(eatDateComboBox.getSelectionModel().getSelectedIndex());
        currentDish.setIngredients(new ArrayList<>(ingredientList));
        if (mealPlanDAO.saveDish(currentDish)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lưu thành công!", ButtonType.OK);
            DishDetailView.getCurrent().refresh();
            alert.showAndWait();
        } else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lưu thất bại!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @Override
    public String getFxmlPath() {
        return "/dish/dish_update_admin_view.fxml";
    }
    @Override
    public BaseController loadAndShow(Stage stage, String title, int width, int height){
        current = (DishUpdateAdminView) super.loadAndShow(stage, title, width, height);
        return current;
    }
    public static DishUpdateAdminView getCurrent() {
        return current;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eatTimeComboBox.setItems(FXCollections.observableArrayList(MealType.values()));
        eatDateComboBox.setItems(FXCollections.observableArrayList(Dish.weekdays));
    }
}
