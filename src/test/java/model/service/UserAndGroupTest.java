package model.service;

import model.entity.Role;
import model.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserAndGroupTest {
    IUserGroupService userGroupService = new UserGroupService();
    IUserService userService = new UserService();
    // Test cases for Group Service
    @Test
    void createGroup() {
        int groupId = userGroupService.createGroup("Test Group");
        assertTrue(groupId > 0, "Group should be created with a valid ID");
    }
    @Test
    void setAsHousewife() {
        int groupId = userGroupService.getMaxGroupId();
        User user = new User(0, "testUser" + groupId, groupId, Role.member);
        int userId = userService.register(user.getUsername()) ? userService.getMaxUserId() : 0;
        int maxGroupId = userGroupService.getMaxGroupId();
        boolean result = userGroupService.setAsHousewife(userId, maxGroupId);
        assertTrue(result, "User should be set as housewife in the group");
    }
    @Test
    void addUserToGroup() {
        int groupId = userGroupService.getMaxGroupId();
        User user = new User(0, "testUser2", groupId, Role.member);
        int userId = userService.register(user.getUsername()) ? userService.getMaxUserId() : 0;
        boolean result = userGroupService.addUserToGroup(userId, groupId);
        assertTrue(result, "User should be added to the group successfully");
        userService.deleteUserById(userId);
    }
    @Test
    void checkGroupExists() {
        int groupId = userGroupService.getMaxGroupId();
        boolean exists = userGroupService.checkGroupExists(groupId);
        assertTrue(exists, "Group should exist with the given ID");
    }
    @Test
    void getGroupNameById() {
        int groupId = userGroupService.getMaxGroupId();
        String groupName = userGroupService.getGroupNameById(groupId);
        assertTrue(groupName != null && !groupName.isEmpty(), "Group name should not be null or empty");
    }
    //Test cases for User Service
    @Test
    void login() {
        String username = "testUser3";
        userService.register(username);
        User user = userService.login(username);
        assertTrue(user != null && user.getUsername().equals(username), "User should be logged in successfully");
        String nonExistentUser = "nonExistentUser";
        User nonExistent = userService.login(nonExistentUser);
        assertNull(nonExistent, "Non-existent user should return null");
        userService.deleteUserById(user.getId());
    }
    @Test
    void register() {
        String username = "testUser4";
        boolean registered = userService.register(username);
        assertTrue(registered, "User should be registered successfully");
        int userId = userService.getMaxUserId();
        userService.deleteUserById(userId);
    }
    @Test
    void getUserById() {
        int userId = userService.getMaxUserId();
        User user = userService.getUserById(userId);
        assertTrue(user != null && user.getId() == userId, "User should be retrieved successfully by ID");
        User nonExistentUser = userService.getUserById(-1);
        assertNull(nonExistentUser, "Non-existent user ID should return null");
    }
    @Test
    void getAllUsers() {
        var users = userService.getAllUsers();
        assertTrue(users != null && !users.isEmpty(), "User list should not be null or empty");
        for (User user : users) {
            assertTrue(user.getId() > 0, "User ID should be greater than zero");
            assertTrue(user.getUsername() != null && !user.getUsername().isEmpty(), "Username should not be null or empty");
            assertNotNull(user.getRole(), "User role should not be null");
        }
    }
    @Test
    void updateUser() {
        int userId = 1;
        User user = userService.getUserById(userId);
        Role originalRole = user.getRole();
        user.setRole(Role.member);
        boolean updated = userService.updateUser(user);
        assertTrue(updated, "User should be updated successfully");
        User updatedUser = userService.getUserById(userId);
        assertEquals(Role.member, updatedUser.getRole());
        // Revert changes
        user.setRole(originalRole);
        userService.updateUser(user);
    }
    @Test
    void deleteUserById() {
        userService.register("testUser" + userGroupService.getMaxGroupId() + 1);
        int userId = userService.getMaxUserId();
        boolean deleted = userService.deleteUserById(userId);
        assertTrue(deleted, "User should be deleted successfully");
        User deletedUser = userService.getUserById(userId);
        assertNull(deletedUser, "Deleted user should return null");
    }
    @Test
    void countHousewivesInGroup() {
        int groupId = userGroupService.getMaxGroupId();
        int count = userService.countHousewivesInGroup(groupId);
        assertTrue(count >= 0, "Count of housewives in group should be non-negative");
    }
}
