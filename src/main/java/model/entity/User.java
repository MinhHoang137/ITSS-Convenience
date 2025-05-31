package model.entity;

public class User {
    private int id;
    private String username;
    private int groupId;
    private Role role;

    public User(int id, String name, int userGroupId, Role role) {
        this.id = id;
        this.username = name;
        this.groupId = userGroupId;
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

}
