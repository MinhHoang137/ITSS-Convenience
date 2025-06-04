package model.service.fridge;

import java.util.List;
import model.entity.Fridge;

public interface IFridgeService {
    int getFridgeIdByGroupId(int groupId);
    List<Fridge> getAllFridges();
}