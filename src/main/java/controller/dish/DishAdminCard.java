package controller.dish;

import controller.ViewController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.dao.MealPlanDAO;
import model.entity.Dish;

public class DishAdminCard {
    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();

    @FXML
    private ListView <String> ingredientListView;
    @FXML
    private Label dishNameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label eatTimeLabel;

    @FXML
    private Label eatDateLabel;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private Dish dish;

    public void setDish(Dish dish) {
        this.dish = dish;
        updateUI();
    }

    private void updateUI() {
        dishNameLabel.setText(dish.getName());
        descriptionLabel.setText(dish.getDescription());
        eatTimeLabel.setText(dish.getEatTime().toString());
        eatDateLabel.setText(dish.getEatDate());
        ingredientListView.setItems(FXCollections.observableArrayList(dish.getIngredients().stream()
                .map(ingredient -> ingredient.getName() + " (" + ingredient.getQuantity() + " "
                        + ingredient.getUnit().toString() + ")")
                .toList()));
    }

    @FXML
    private void onEdit() {
        ViewController.getInstance().openController(new DishUpdateAdminView(),
                "Cập nhật món ăn", 1000, 700);
        DishUpdateAdminView.getCurrent().setDish(dish);
    }

    @FXML
    private void onDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Bạn có chắc chắn muốn xóa món ăn này?");
        confirm.setContentText(dish.getName());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Thực hiện xóa dish
                boolean success = mealPlanDAO.deleteDish(dish.getId());

                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Đã xóa món ăn thành công!");
                    successAlert.showAndWait();

                    // Xử lý cập nhật lại giao diện nếu cần
                    DishDetailView.getCurrent().refresh();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText("Không thể xóa món ăn");
                    errorAlert.setContentText("Đã xảy ra lỗi khi xóa món ăn. Vui lòng thử lại.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

}
