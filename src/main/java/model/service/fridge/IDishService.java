package model.service.fridge;

import model.entity.Dish;
import java.util.List;

public interface IDishService {
    boolean cookDish(Dish dish, int fridgeId);
    boolean consumeIngredientsForDish(Dish dish, int fridgeId);
}

