package model.service;

import model.entity.BuyIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyIngredientService extends BaseService implements IBuyIngredientService {

    @Override
    public boolean addIngredientToList(BuyIngredient ingredient) {
        String sql = "INSERT INTO buy_ingredient (shoppingListId, ingredientName, quantity, unitType) VALUES (?, ?, ?, ?)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, ingredient.getShoppingListId());
            ps.setString(2, ingredient.getIngredientName());
            ps.setDouble(3, ingredient.getQuantity());
            ps.setString(4, ingredient.getUnitType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nguyên liệu: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }


    @Override
    public boolean removeIngredientFromList(int shoppingListId, String ingredientName) {
        String sql = "DELETE FROM buy_ingredient WHERE shoppingListId = ? AND ingredientName = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shoppingListId);
            ps.setString(2, ingredientName);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nguyên liệu khỏi danh sách: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    @Override
    public List<BuyIngredient> getIngredientsByShoppingListId(int shoppingListId) {
        List<BuyIngredient> list = new ArrayList<>();
        String sql = "SELECT * FROM buy_ingredient WHERE shoppingListId = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shoppingListId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BuyIngredient bi = new BuyIngredient();
                bi.setShoppingListId(rs.getInt("shoppingListId"));
                bi.setIngredientName(rs.getString("ingredientName"));
                bi.setQuantity(rs.getDouble("quantity"));
                bi.setUnitType(rs.getString("unitType"));
                list.add(bi);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nguyên liệu: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return list;
    }

}
