package model.service;

import model.entity.User;

import java.util.List;

public interface IUserService {
    User login(String username);
    boolean register(String username);
    User getUserById(int userId);
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUserById(int id);
    int countHousewivesInGroup(int groupId);
    int getMaxUserId();
}
