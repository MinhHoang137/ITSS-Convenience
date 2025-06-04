package model.service.fridge;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.entity.Ingredient;
import model.entity.Unit;
import model.service.BaseService;

public class IngredientService extends BaseService implements IIngredientService{

    public void addIngredient(Ingredient ingredient, int fridgeId) {
        getConnection();
        try {
            String sql = "INSERT INTO ingredient (ingredientName, quantity, unitType, ingredientId, expirationDate, fridgeId) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getQuantity());
            stmt.setString(3, ingredient.getUnit().toString());
            stmt.setInt(4, ingredient.getId());
            stmt.setDate(5, Date.valueOf(ingredient.getExpirationDate()));
            stmt.setInt(6, fridgeId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void addIngredientToFridge(Ingredient ingredient, int fridgeId) {
        getConnection();
        try {
            String sql = "INSERT INTO ingredient (ingredientName, quantity, unitType, expirationDate, fridgeId) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getQuantity());
            stmt.setString(3, ingredient.getUnit().toString());
            stmt.setDate(4, Date.valueOf(ingredient.getExpirationDate()));
            stmt.setInt(5, fridgeId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public List<Ingredient> getAllIngredients(int fridgeId) {
        getConnection();
        List<Ingredient> list = new ArrayList<>();
        try {
            String sql = """
                    SELECT ingredientName, unitType, SUM(quantity) as totalQuantity
                    FROM ingredient
                    WHERE fridgeId = ?
                    GROUP BY ingredientName, unitType
                    HAVING SUM(quantity) > 0
                    """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(
                        -1,
                        rs.getString("ingredientName"),
                        rs.getDouble("totalQuantity"),
                        Unit.valueOf(rs.getString("unitType")),
                        null
                );
                list.add(ing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    public List<Ingredient> getExpiringIngredients(int fridgeId, int daysBeforeExpire) {
        getConnection();
        List<Ingredient> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate upper = now.plusDays(daysBeforeExpire);
        try {
            String sql = "SELECT * FROM ingredient WHERE fridgeId = ? AND expirationDate <= ? AND expirationDate >= ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, fridgeId);
            stmt.setDate(2, Date.valueOf(upper));
            stmt.setDate(3, Date.valueOf(now));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(
                        rs.getInt("ingredientId"),
                        rs.getString("ingredientName"),
                        rs.getDouble("quantity"),
                        Unit.valueOf(rs.getString("unitType")),
                        rs.getDate("expirationDate").toLocalDate());
                list.add(ing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return list;
    }

    public boolean useIngredient(String name, double usedQuantity, Unit unit, int fridgeId) {
        getConnection();
        try {
            String sql = "SELECT * FROM ingredient WHERE ingredientName = ? AND unitType = ? AND fridgeId = ?";
            PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, name);
            stmt.setString(2, unit.toString());
            stmt.setInt(3, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double available = rs.getDouble("quantity");
                if (available >= usedQuantity) {
                    rs.updateDouble("quantity", available - usedQuantity);
                    rs.updateRow();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return false;
    }
}
