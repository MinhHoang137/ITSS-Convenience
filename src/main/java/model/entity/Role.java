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
    public static Role fromString(String value) {
        for (Role r : Role.values()) {
            if (r.displayName.equalsIgnoreCase(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy role: " + value);
    }
}
