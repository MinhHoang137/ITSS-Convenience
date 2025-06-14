package model.service;

import model.entity.Role;
import model.entity.User;

import java.sql.*;
import java.util.List;

/**
 * Dịch vụ quản lý nhóm người dùng, bao gồm tạo nhóm, thêm thành viên,
 * gán vai trò, và lấy thông tin liên quan đến nhóm.
 */
public class UserGroupService extends BaseService implements IUserGroupService {

    /**
     * Tạo một nhóm mới với tên cho trước.
     *
     * @param groupName Tên của nhóm.
     * @return ID của nhóm mới được tạo, hoặc -1 nếu thất bại.
     */
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

    /**
     * Gán người dùng vào nhóm với vai trò là housewife (nội trợ).
     *
     * @param userId  ID của người dùng.
     * @param groupId ID của nhóm.
     * @return true nếu thành công, false nếu thất bại.
     */
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

    /**
     * Thêm người dùng vào nhóm nhưng không thay đổi vai trò hiện tại.
     *
     * @param userId  ID người dùng.
     * @param groupId ID nhóm.
     * @return true nếu thành công, false nếu thất bại.
     */
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

    /**
     * Kiểm tra xem nhóm có tồn tại hay không.
     *
     * @param groupId ID nhóm.
     * @return true nếu tồn tại, false nếu không tồn tại.
     */
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

    /**
     * Lấy tên của nhóm theo ID.
     *
     * @param groupId ID nhóm.
     * @return Tên nhóm nếu tồn tại, ngược lại trả về "Không xác định".
     */
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

    /**
     * Lấy danh sách thành viên thuộc một nhóm.
     *
     * @param groupId ID nhóm.
     * @return Danh sách các đối tượng User thuộc nhóm.
     */
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

    /**
     * Thêm người dùng vào nhóm theo tên đăng nhập, nếu người đó chưa thuộc nhóm nào.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param groupId  ID nhóm.
     * @return true nếu thành công, false nếu thất bại.
     */
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

    /**
     * Cập nhật vai trò cho một người dùng.
     *
     * @param userId ID người dùng.
     * @param role   Vai trò mới.
     * @return true nếu thành công, false nếu thất bại.
     */
    @Override
    public boolean updateUserRole(int userId, Role role) {
        String sql = "UPDATE User SET role = ? WHERE id = ?";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role.toString().toLowerCase()); // Lưu "member", "housewife",...
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật role: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return false;
    }

    /**
     * Lấy ID lớn nhất hiện tại trong bảng UserGroup.
     *
     * @return ID lớn nhất, hoặc -1 nếu không tìm thấy.
     */
    public int getMaxGroupId() {
        String sql = "SELECT MAX(id) AS maxId FROM UserGroup";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("maxId");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ID nhóm lớn nhất: " + e.getMessage());
        } finally {
            closeConnection();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }
}
