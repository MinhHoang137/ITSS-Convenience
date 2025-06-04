package model.service.fridge;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.entity.Fridge;
import model.service.BaseService;

public class FridgeService extends BaseService implements IFridgeService{

    public int getFridgeIdByGroupId(int groupId) {
        getConnection();
        int fridgeId = -1;
        try {
            String sql = "SELECT fridgeId FROM fridge WHERE groupId = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fridgeId = rs.getInt("fridgeId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return fridgeId;
    }

    public List<Fridge> getAllFridges() {
        getConnection();
        List<Fridge> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fridge";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Fridge fridge = new Fridge();
                fridge.setId(rs.getInt("fridgeId"));
                fridge.setUserGroupId(rs.getInt("groupId"));
                list.add(fridge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }
}
