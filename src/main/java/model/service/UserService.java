package model.service;

import model.entity.Role;
import model.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service xử lý các thao tác liên quan đến người dùng.
 */
public class UserService extends BaseService implements IUserService {

    /**
     * Đăng nhập người dùng theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return đối tượng {@link User} nếu tìm thấy, ngược lại trả về null
     */
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

    /**
     * Đăng ký tài khoản người dùng mới.
     *
     * @param username tên đăng nhập
     * @return true nếu đăng ký thành công, ngược lại false
     */
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

    /**
     * Kiểm tra người dùng đã tồn tại chưa.
     *
     * @param username tên đăng nhập
     * @return true nếu đã tồn tại, ngược lại false
     */
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

    /**
     * Lấy người dùng theo ID.
     *
     * @param userId ID người dùng
     * @return đối tượng {@link User} nếu tồn tại, ngược lại null
     */
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

    /**
     * Lấy danh sách tất cả người dùng, loại trừ role ADMIN.
     *
     * @return danh sách {@link User}
     */
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

    /**
     * Cập nhật thông tin người dùng.
     *
     * @param user đối tượng {@link User} cần cập nhật
     * @return true nếu cập nhật thành công, ngược lại false
     */
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

    /**
     * Xóa người dùng theo ID.
     *
     * @param id ID người dùng
     * @return true nếu xóa thành công, ngược lại false
     */
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

    /**
     * Đếm số lượng người có vai trò HOUSEWIFE trong một nhóm.
     *
     * @param groupId ID nhóm
     * @return số lượng housewife trong nhóm
     */
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

    /**
     * Lấy ID người dùng lớn nhất hiện có trong bảng.
     *
     * @return ID lớn nhất, hoặc -1 nếu lỗi
     */
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
        return -1;
    }
}
