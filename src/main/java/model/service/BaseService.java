/**
 * BaseService.java
 * Lớp cơ sở cho các dịch vụ kết nối đến cơ sở dữ liệu.
 * Cung cấp phương thức để lấy và đóng kết nối.
 * Yêu cầu tất cả các lớp dịch vụ kế thừa từ lớp này để sử dụng kết nối CSDL.
 * @author Minh Hoàng
 * @version 1.0
 */
package model.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseService {
    protected String url = "jdbc:mysql://localhost:3306/Convenience"; // Tên database bạn tạo
    protected String username = "root"; // Tên người dùng MySQL
    protected String password = "Hoang!3070$"; // Mật khẩu MySQL
    protected Connection connection = null;
    protected Connection getConnection() {
        try {
            // Bắt buộc nạp driver nếu không dùng build tool như Maven hoặc chạy từ IDE
            Class.forName("com.mysql.cj.jdbc.Driver"); // Hoặc com.mysql.jdbc.Driver với bản cũ
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Kết nối đến CSDL thành công!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy JDBC Driver. Kiểm tra thư viện mysql-connector-j.");
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối đến CSDL:");
            System.err.println(e.getMessage());
        }
        return null;
    }
    protected void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối:");
                System.err.println(e.getMessage());
            }
        }
    }
}
