package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL      = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "mysql@987";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection() {
        // no-op — connections are closed by the try-with-resources in each DAO
    }
}