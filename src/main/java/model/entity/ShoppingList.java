package model.entity;

import java.time.LocalDate;

public class ShoppingList {
    private int shoppingListId;
    private LocalDate buyDate;
    private int groupId;

    public ShoppingList() {}

    public ShoppingList(int shoppingListId, LocalDate buyDate, int groupId) {
        this.shoppingListId = shoppingListId;
        this.buyDate = buyDate;
        this.groupId = groupId;
    }

    public int getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(int shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(LocalDate buyDate) {
        this.buyDate = buyDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "shoppingListId=" + shoppingListId +
                ", buyDate=" + buyDate +
                ", groupId=" + groupId +
                '}';
    }
}
