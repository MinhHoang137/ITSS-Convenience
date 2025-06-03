package controller.meal;

import controller.ViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.dao.MealPlanDAO;
import model.entity.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishDetailView {
    private static DishDetailView current;

    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();
    @FXML
    private VBox dishHolder;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button addButton;

    private List<Dish> dishList;

    public void setDishes(List<Dish> dishes) {
        this.dishList = dishes;
        dishHolder.getChildren().clear();
        for (Dish dish : dishes) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dish/dish_admin_card.fxml"));
                Parent card = loader.load();
                DishAdminCard dishAdminCard = loader.getController();
                dishAdminCard.setDish(dish);
                dishHolder.getChildren().add(card);

            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @FXML
    private void OnAddDish() {
        // Mở dialog hoặc FXML mới để thêm món ăn
        System.out.println("Thêm món ăn mới");
        // TODO: Hiển thị màn hình thêm món ăn mới (DishUpdateAdminView)
        ViewController.getInstance().openController(new DishUpdateAdminView(),
                "Thêm món ăn", 1000, 700);
    }
    public static DishDetailView getCurrent() {
        return current;
    }
    public static void setCurrent(DishDetailView current) {
        DishDetailView.current = current;
    }
    @FXML
    public void initialize() {
        ArrayList<Dish> dishes = mealPlanDAO.getAllDishes();
        setDishes(dishes);
    }
    public void refresh() {

        ArrayList<Dish> dishes = mealPlanDAO.getAllDishes();
        setDishes(dishes);
    }
    public static void open(StackPane parent) {
        try {
            FXMLLoader loader = new FXMLLoader(DishDetailView.class.getResource("/dish/dish_detail_view.fxml"));
            Parent root = loader.load();
            DishDetailView dishDetailView = loader.getController();
            dishDetailView.setDishes(new ArrayList<>());
            current = dishDetailView;
            dishDetailView.refresh();
            parent.getChildren().setAll(root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }
}
