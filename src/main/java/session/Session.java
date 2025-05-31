package session;

import model.entity.User;

public class Session {
    private static User currentUser;
    private static int selectedShoppingListId;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static void setSelectedShoppingListId(int id) {
        selectedShoppingListId = id;
    }

    public static int getSelectedShoppingListId() {
        return selectedShoppingListId;
    }

}
