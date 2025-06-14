package model.service;

import model.entity.Role;
import model.entity.User;

import java.util.List;

public interface IUserGroupService {
    int createGroup(String groupName);
    boolean setAsHousewife(int userId, int groupId);
    boolean addUserToGroup(int userId, int groupId);
    boolean addUserToGroupByUsername(String username, int groupId);
    boolean checkGroupExists(int groupId);
    String getGroupNameById(int groupId);
    List<User> getMembersByGroup(int groupId);
    boolean updateUserRole(int userId, Role role);
    int getMaxGroupId();
}
