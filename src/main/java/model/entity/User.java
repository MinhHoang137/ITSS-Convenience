package model.entity;

public class User {
    private int id;
    private String username;
    private int groupId; // 0 = chưa có nhóm
    private Role role;

    public User() {
    }

    public User(int id, String username, int groupId, Role role) {
        this.id = id;
        this.username = username;
        this.groupId = groupId;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getGroupId() {
        return groupId;
    }

    public Role getRole() {
        return role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", groupId=" + groupId +
                ", role=" + role +
                '}';
    }
}
