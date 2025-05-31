package model.entity;

public enum Role {
    admin("ADMIN"),
    housewife("HOUSEWIFE"),
    member("MEMBER");;
    private  final String displayName;
    Role(String displayName) {
        this.displayName = displayName;
    }
    public String toString() {
        return displayName;
    }
}
