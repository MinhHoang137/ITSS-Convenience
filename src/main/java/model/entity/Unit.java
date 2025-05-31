package model.entity;

public enum Unit {
    kg("kg"),
    l("l"),
    pcs("pcs");
    private final String displayName;
    Unit(String displayName) {
        this.displayName = displayName;
    }
    public String toString() {
        return displayName;
    }
}
