package controller.meal;

import controller.BaseController;
import controller.NotificationView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.dao.MealPlanDAO;
import model.entity.Dish;
import model.entity.Meal;
import model.entity.MealType;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddMealView extends BaseController {
    private static AddMealView current;
    private Meal meal = new Meal();
    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();

    @FXML
    private VBox addedContainer;
    @FXML
    private VBox foundContainer;
    @FXML
    private TextField dishName;
    @FXML
    private ComboBox<String> eatTImeBox;
    @FXML
    private ComboBox<String> eatDateBox;

    private void putDishesTo(Pane container, ArrayList<Dish> dishes) {
        container.getChildren().clear();
        for (Dish dish : dishes) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/meal/dish_card.fxml"));
            try {
                Parent parent = loader.load();
                DishCard dishCard = loader.getController();
                dishCard.setDish(dish);
                dishCard.setParent(parent);
                container.getChildren().add(parent);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    @FXML
    private void OnSearchByName(ActionEvent actionEvent) {
        foundContainer.getChildren().clear();
        String name = dishName.getText().trim();
        //System.out.println(name);
        ArrayList<Dish> dishes = new ArrayList<>();
        int dateIndex = getEatDate();
        if (name.isEmpty()) {
            if (dateIndex != -1) {
                dishes = mealPlanDAO.getCookableDishesByDayIndex(1, dateIndex);
            }
            else {
                dishes = mealPlanDAO.getCookableDishes(1);
            }
        } else {
            dishes = mealPlanDAO.getDishLike(name);
        }
        putDishesTo(foundContainer, dishes);
    }

    @FXML
    private void OnAddMeal(ActionEvent event) {
        try {
            mealPlanDAO.addMeal(meal);
            // Hiển thị thông báo thành công
            NotificationView.Create("Đã thêm bữa ăn thành công!");
            // Tạo danh sách mua sắm nếu thiếu nguyên liệu
        }
        catch (Exception e) {
            // Hiển thị thông báo lỗi
            NotificationView.Create("Lỗi khi thêm bữa ăn: " + e.getMessage());
        }
    }

    public static AddMealView getCurrent() {
        return current;
    }

    private boolean addDishToMeal(Dish dish) {
        if (dish == null || dish.getId() <= 0) {
            return false;
        }
        ArrayList<Dish> dishList = meal.getDishList();
        for (Dish existingDish : dishList) {
            if (existingDish.getId() == dish.getId()) {
                return false;
            }
        }
        dishList.add(dish);
        return true;
    }

    public boolean removeDishFromMeal(DishCard dishCard) {
        Dish dish = dishCard.getDish();
        if (dish == null || dish.getId() <= 0) {
            return false;
        }
        ArrayList<Dish> dishList = meal.getDishList();
        if(dishList.removeIf(existingDish -> existingDish.getId() == dish.getId())){
            dishCard.setSelected(false);
            addedContainer.getChildren().remove(dishCard.getParent());
            return true;
        }
        return false;
    }

    public VBox getAddedContainer() {
        return addedContainer;
    }
    public void addFoundDish(Dish dish) {
        if (dish == null || dish.getId() <= 0) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/meal/dish_card.fxml"));
        try {
            if (!addDishToMeal(dish)) {
                NotificationView.Create("Món ăn đã được thêm vào bữa ăn.");
                return;
            }
            Parent parent = loader.load();
            DishCard dishCard = loader.getController();
            dishCard.setDish(dish);
            dishCard.setSelected(true);
            dishCard.setParent(parent);
            addedContainer.getChildren().add(parent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public VBox getFoundContainer() {
        return foundContainer;
    }

    @Override
    public String getFxmlPath() {
        return "/meal/add_meal_plan.fxml";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eatTImeBox.getItems().addAll("Bữa sáng", "Bữa trưa", "Bữa tối");
        eatDateBox.getItems().addAll("Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy");
        ArrayList<Dish> dishes = mealPlanDAO.getCookableDishes(1);
        putDishesTo(foundContainer, dishes);
    }

    @Override
    public BaseController loadAndShow(Stage stage, String title, int width, int height) {
        current = (AddMealView) super.loadAndShow(stage, title, width, height);
        return current;
    }

    private MealType getEatTime() {
        String selected = eatTImeBox.getValue();
        if (selected == null) return null;
        return switch (selected) {
            case "Bữa sáng" -> MealType.breakfast;
            case "Bữa trưa" -> MealType.lunch;
            case "Bữa tối" -> MealType.dinner;
            default -> null;
        };
    }

    private int getEatDate() {
        String selected = eatDateBox.getValue();
        if (selected == null) return -1;
        return switch (selected) {
            case "Thứ hai"   -> 0;
            case "Thứ ba"    -> 1;
            case "Thứ tư"    -> 2;
            case "Thứ năm"   -> 3;
            case "Thứ sáu"   -> 4;
            case "Thứ bảy"   -> 5;
            case "Chủ nhật"  -> 6;
            default          -> -1;
        };
    }

    public void OnEatTimeChange(ActionEvent actionEvent) {
        MealType selectedMealType = getEatTime();
        if (selectedMealType != null) {
            meal.setMealType(selectedMealType);
        } else {
            System.out.println("Chưa chọn bữa ăn hợp lệ.");
        }
    }

    public void OnEatDateChange(ActionEvent actionEvent) {
        int selectedDateIndex = getEatDate();
        if (selectedDateIndex >= 0) {
            meal.setDateIndex(selectedDateIndex);
        } else {
            System.out.println("Chưa chọn ngày hợp lệ.");
        }
    }


}
