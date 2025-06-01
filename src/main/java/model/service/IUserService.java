package model.service;

import model.entity.User;

public interface IUserService {
    User login(String username);
    boolean register(String username);
    User getUserById(int userId);
}
