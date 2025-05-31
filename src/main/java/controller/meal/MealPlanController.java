package controller.meal;

import controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class MealPlanController extends BaseController {

    @FXML private Button byDateButton;
    @FXML private Button byWeekButton;

    @Override
    public String getFxmlPath() {
        return "/meal/meal_plan.fxml";
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khởi tạo nếu cần
    }

    @FXML private void onPlanByDate(ActionEvent e) {
        // Xử lý lên kế hoạch theo ngày
    }

    @FXML private void onPlanByWeek(ActionEvent e) {
        // Xử lý lên kế hoạch theo tuần
    }

}
