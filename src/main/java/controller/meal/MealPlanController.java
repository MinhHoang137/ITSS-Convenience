package controller.meal;

import controller.BaseController;
import controller.NotificationView;
import controller.ViewController;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.dao.MealPlanDAO;
import model.entity.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MealPlanController extends BaseController {

    private static MealPlanController current;

    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();

    @FXML private VBox mealPlanContainer;
    @FXML private Button addMealButton;

    public static MealPlanController getCurrent() {
        return current;
    }

    @Override
    public String getFxmlPath() {
        return "/meal/meal_plan.fxml";
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<Meal> meals = mealPlanDAO.getAllMeals();
        displayMealCards(meals);
    }


    @FXML private void onAddMeal(ActionEvent e) {
        ViewController.getInstance().openController(new AddMealView(),
                "Thêm bữa ăn", 800, 600);
    }

    private void displayMealCards(List<Meal> mealList) {
        mealPlanContainer.getChildren().clear(); // Xóa các thẻ cũ nếu có

        for (Meal meal : mealList) {
            try {
                // Tải MealCard từ FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/meal/meal_card.fxml"));
                Parent card = loader.load();

                // Lấy controller của MealCard
                MealCard controller = loader.getController();
                controller.setMeal(meal);
                controller.setParent(card);

                // Xử lý callback khi xóa thẻ
                controller.setOnDeletedCallback(() -> {
                    mealPlanContainer.getChildren().remove(card);
                    // Nếu cần cập nhật lại dữ liệu từ DB, gọi lại loadMealCards()
                });

                // Thêm thẻ vào VBox
                mealPlanContainer.getChildren().add(card);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public void removeMeal(MealCard mealCard) {
        if (mealPlanDAO.deleteMeal(mealCard.getMeal().getId())){
            NotificationView.Create("Đã xóa bữa ăn thành công: " + mealCard.getMeal().getId());
            mealPlanContainer.getChildren().remove(mealCard.getParent());
        } else {
            NotificationView.Create("Đã xóa bữa ăn thất bại: " + mealCard.getMeal().getId());
        }
    }
    @Override
    public BaseController loadAndShow(Stage stage, String title, int width, int height) {
        current = (MealPlanController) super.loadAndShow(stage, title, width, height);
        return current;
    }
}
