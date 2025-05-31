package model.entity;

public class BuyIngredient {
    private int shoppingListId;
    private String ingredientName;
    private double quantity;
    private String unitType;

    public BuyIngredient() {}

    public BuyIngredient(int shoppingListId, String ingredientName, double quantity, String unitType) {
        this.shoppingListId = shoppingListId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unitType = unitType;
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @Override
    public String toString() {
        return "BuyIngredient{" +
                "shoppingListId=" + shoppingListId +
                ", ingredientName='" + ingredientName + '\'' +
                ", quantity=" + quantity +
                ", unitType='" + unitType + '\'' +
                '}';
    }
}
