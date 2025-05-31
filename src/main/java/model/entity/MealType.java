package model.entity;

public enum MealType {
    breakfast("breakfast"),
    lunch("lunch"),
    dinner("dinner"),;

    private final String displayName;

    MealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
