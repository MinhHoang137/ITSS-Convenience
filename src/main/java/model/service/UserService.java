package model.service;

import model.entity.Role;
import model.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService extends BaseService implements IUserService {

    @Override
    public User login(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setGroupId(rs.getInt("groupId"));
                user.setRole(Role.fromString(rs.getString("role")));
                System.out.println("🧾 Sau đăng nhập: " + user.getUsername() + ", role = " + user.getRole());
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return null;
    }

    @Override
    public boolean register(String username) {
        if (checkUserExists(username)) return false;

        String sql = "INSERT INTO User (username, role) VALUES (?, 'member')";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng ký: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    private boolean checkUserExists(String username) {
        String sql = "SELECT id FROM User WHERE username = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra username: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return true;
    }
    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM User WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setGroupId(rs.getInt("groupId"));
                user.setRole(Role.fromString(rs.getString("role")));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy user theo ID: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role != 'ADMIN'";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setGroupId(rs.getInt("groupId"));
                user.setRole(Role.valueOf(rs.getString("role")));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return users;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE user SET role = ?, groupId = ? WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getRole().name());
            ps.setInt(2, user.getGroupId());
            ps.setInt(3, user.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean deleteUserById(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public int countHousewivesInGroup(int groupId) {
        String sql = "SELECT COUNT(*) FROM user WHERE groupId = ? AND role = 'HOUSEWIFE'";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return 0;
    }

    @Override
    public int getMaxUserId() {
        String sql = "SELECT MAX(id) FROM User";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ID người dùng lớn nhất: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

}
