/**
 * ViewController.java
 * lớp điều khiển giao diện người dùng của ứng dụng JavaFX.
 * @Author Minh Hoàng
 */
package controller;

import controller.meal.AddMealView;
import controller.meal.MealPlanController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ViewController extends Application {
    private  static ViewController instance;
    @Override
    public void start(Stage stage) throws Exception {
        ViewController.getInstance().openController(new MealPlanController(),
                "Kế hoạch bữa ăn", 1000, 600);
    }

    /**
     * Mở một cửa sổ mới với controller đã cho
     * @param controller Controller cần mở
     * @param title Tên của cửa sổ
     * @param width Chiều rộng của cửa sổ
     * @param height Chiều cao của cửa sổ
     * @return Controller đã được mở, hoặc null nếu có lỗi xảy ra
     */
    public BaseController openController(BaseController controller, String title, int width, int height) {
        try {
            System.out.println(controller.getFxmlPath());
            Stage stage = new Stage();
            return controller.loadAndShow(stage, title, width, height);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
    public static ViewController getInstance() {
        if (instance == null) {
            instance = new ViewController();
        }
        return instance;
    }
}
