/**
 * AddMealView.java
 * Lớp này quản lý giao diện và logic để thêm bữa ăn vào kế hoạch bữa ăn.
 * Nó cho phép người dùng tìm kiếm món ăn, thêm món ăn vào bữa ăn,
 * xóa món ăn khỏi bữa ăn,
 * và lưu bữa ăn vào cơ sở dữ liệu.
 * * Nó cũng kiểm tra nguyên liệu cần thiết và tạo danh sách mua sắm nếu thiếu nguyên liệu.
 * * @author Minh Hoàng
 */
package controller.meal;

import controller.BaseController;
import controller.NotificationView;
import controller.dish.DishCard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.dao.FridgeDAO;
import model.dao.MealPlanDAO;
import model.entity.*;
import model.service.IShoppingListService;
import model.service.ShoppingListService;
import session.Session;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddMealView extends BaseController {
    private static AddMealView current;

    private Dish dish;


    private MealPlanController mealPlanController = MealPlanController.getCurrent();
    private final FridgeDAO fridgeDAO = FridgeDAO.getInstance();
    private Meal meal = new Meal();
    private MealPlanDAO mealPlanDAO = MealPlanDAO.getInstance();
    private int groupId = Session.getCurrentUser().getGroupId();
    private final int fridgeId = fridgeDAO.getFridgeIdByGroupId(groupId);
    @FXML
    private Button goBackButton;
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
    @FXML
    private Label dishNameText;
    @FXML
    private Label instructionText;
    @FXML
    private ListView<String> ingredientList;


    private void putDishesTo(Pane container, ArrayList<Dish> dishes) {
        container.getChildren().clear();
        for (Dish dish : dishes) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dish/dish_card.fxml"));
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
                dishes = mealPlanDAO.getCookableDishesByDayIndex(fridgeId, dateIndex);
            }
            else {
                dishes = mealPlanDAO.getCookableDishes(fridgeId);
            }
        } else {
            dishes = mealPlanDAO.getDishLike(name);
        }
        putDishesTo(foundContainer, dishes);
    }

    @FXML
    private void OnAddMeal(ActionEvent event) {
        if (meal.getDishList().isEmpty()) {
            NotificationView.Create("Vui lòng thêm ít nhất một món ăn vào bữa ăn.");
            return;
        }
        MealType mealType = getEatTime();
        if (mealType == null) {
            NotificationView.Create("Vui lòng chọn thời gian ăn hợp lệ.");
            return;
        }
        try {
            if (mealPlanDAO.addMeal(meal, groupId)){
                NotificationView.Create("Bữa ăn đã được thêm thành công!");
                mealPlanController = MealPlanController.getCurrent();
                mealPlanController.refreshMealPlan();
                // Tạo danh sách mua sắm nếu thiếu nguyên liệu
                ArrayList<Ingredient> totalIngredients = getTotalIngredient(meal);
                // Kiểm tra nguyên liệu có đủ không
                ArrayList<Ingredient> missingIngredients = mealPlanDAO.getMissingIngredients(fridgeId, totalIngredients);
                if (!missingIngredients.isEmpty()) {
                    // Nếu thiếu nguyên liệu, tạo danh sách mua sắm
                    IShoppingListService service = new ShoppingListService();
                    if (service.addIngredientsToShoppingList(groupId, missingIngredients)) {
                        // Hiển thị danh sách mua sắm
                        NotificationView.Create("Đã tạo danh sách mua sắm cho nguyên liệu thiếu.");
                    } else {
                        NotificationView.Create("Không thể tạo danh sách mua sắm. Vui lòng kiểm tra lại.");
                    }
                } else {
                    NotificationView.Create("Không có nguyên liệu nào cần mua thêm.");
                }
            } else {
                NotificationView.Create("Không thể thêm bữa ăn. Vui lòng kiểm tra lại.");
                return;
            }
        }
        catch (Exception e) {
            // Hiển thị thông báo lỗi
            NotificationView.Create("Lỗi khi thêm bữa ăn, hãy đảm bảo mọi giá trị đều là hợp lệ và không bị trống");
            System.out.println("Lỗi khi thêm bữa ăn: " + e.getMessage());
        }
    }
    /**
     * Lấy phiên bản hiện tại của AddMealView.
     * @return phiên bản hiện tại của AddMealView
     */
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

    /**
     * Xóa món ăn khỏi bữa ăn hiện tại.
     * @param dishCard DishCard chứa thông tin món ăn cần xóa
     */
    public void removeDishFromMeal(DishCard dishCard) {
        Dish dish = dishCard.getDish();
        if (dish == null || dish.getId() <= 0) {
            return;
        }
        ArrayList<Dish> dishList = meal.getDishList();
        if(dishList.removeIf(existingDish -> existingDish.getId() == dish.getId())){
            dishCard.setSelected(false);
            addedContainer.getChildren().remove(dishCard.getParent());
        }
    }
    /**
     * Thêm món ăn vào bữa ăn hiện tại và hiển thị nó trong giao diện.
     * @param dish Món ăn cần thêm
     */
    public void addFoundDish(Dish dish) {
        if (dish == null || dish.getId() <= 0) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dish/dish_card.fxml"));
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

    @Override
    public String getFxmlPath() {
        return "/meal/add_meal_plan.fxml";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eatTImeBox.getItems().addAll("Bữa sáng", "Bữa trưa", "Bữa tối");
        eatDateBox.getItems().addAll("Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy");
        ArrayList<Dish> dishes = mealPlanDAO.getCookableDishes(fridgeId);
        putDishesTo(foundContainer, dishes);
    }
    /**
     * Thiết lập món ăn cho AddMealView.
     * @param dish Món ăn cần thiết lập
     */
    public void setDish(Dish dish) {
        this.dish = dish;
        if (dish != null) {
            dishNameText.setText(dish.getName());
            instructionText.setText(dish.getDescription());
            ingredientList.getItems().clear();
            for (Ingredient ingredient : dish.getIngredients()) {
                ingredientList.getItems().add(ingredient.getName() + " - " + ingredient.getQuantity() + " " + ingredient.getUnit());
            }
        } else {
            dishNameText.setText("Chưa chọn món ăn");
            instructionText.setText("");
            ingredientList.getItems().clear();
        }
    }

    @Override
    public BaseController loadAndShow(Stage stage, String title, int width, int height) {
        Role role = Session.getCurrentUser().getRole();
        if (role != Role.housewife){
            NotificationView.Create("Chỉ người nội trợ mới có thể thêm bữa ăn.");
            return null;
        }
        current = (AddMealView) super.loadAndShow(stage, title, width, height);
        return current;
    }
    private ArrayList<Ingredient> getTotalIngredient(Meal meal) {
        ArrayList<Ingredient> totalIngredients = new ArrayList<>();

        for (Dish dish : meal.getDishList()) {
            for (Ingredient ingredient : dish.getIngredients()) {
                boolean found = false;

                for (Ingredient total : totalIngredients) {
                    if (total.equals(ingredient)) {
                        // Nếu đã có nguyên liệu giống, cộng thêm số lượng
                        total.setQuantity(total.getQuantity() + ingredient.getQuantity());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Nếu chưa có, thêm mới một bản sao
                    Ingredient copy = new Ingredient(
                            ingredient.getId(),
                            ingredient.getName(),
                            ingredient.getQuantity(),
                            ingredient.getUnit(),
                            ingredient.getExpirationDate()
                    );
                    totalIngredients.add(copy);
                }
            }
        }

        return totalIngredients;
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

    @FXML
    private void onSearchAll(ActionEvent actionEvent) {
        foundContainer.getChildren().clear();
        ArrayList<Dish> dishes = mealPlanDAO.getAllDishes();
        putDishesTo(foundContainer, dishes);
    }

    @FXML
    private void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.close();
        if (mealPlanController != null) {
            mealPlanController.refreshMealPlan();
        }
    }
}
