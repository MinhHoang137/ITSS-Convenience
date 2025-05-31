package model.service;

import model.entity.Role;
import model.entity.User;

import java.sql.*;
import java.util.List;

public class UserGroupService extends BaseService implements IUserGroupService {

    @Override
    public int createGroup(String groupName) {
        String sql = "INSERT INTO UserGroup (name) VALUES (?)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, groupName);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // ID nhóm vừa tạo
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo nhóm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return -1;
    }

    // Gán nhóm cho người tạo và đặt vai trò là housewife
    public boolean setAsHousewife(int userId, int groupId) {
        String sql = "UPDATE User SET groupId = ?, role = 'housewife' WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi gán housewife: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    // Dành cho người tham gia nhóm, role giữ nguyên
    @Override
    public boolean addUserToGroup(int userId, int groupId) {
        String sql = "UPDATE User SET groupId = ? WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm người vào nhóm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    @Override
    public boolean checkGroupExists(int groupId) {
        String sql = "SELECT id FROM UserGroup WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra nhóm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }
    @Override
    public String getGroupNameById(int groupId) {
        String sql = "SELECT name FROM UserGroup WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tên nhóm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return "Không xác định";
    }

    @Override
    public List<User> getMembersByGroup(int groupId) {
        List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM User WHERE groupId = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setGroupId(rs.getInt("groupId"));
                u.setRole(Role.fromString(rs.getString("role")));
                list.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thành viên nhóm: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return list;
    }

    @Override
    public boolean addUserToGroupByUsername(String username, int groupId) {
        String sql = "UPDATE User SET groupId = ? WHERE username = ? AND (groupId IS NULL OR groupId = 0)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm user vào nhóm bằng username: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }
    @Override
    public boolean updateUserRole(int userId, Role role) {
        String sql = "UPDATE User SET role = ? WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role.toString().toLowerCase()); // Lưu "MEMBER", "HOUSEWIFE",...
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật role: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

}
