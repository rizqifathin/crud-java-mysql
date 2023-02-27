package crud;

import java.sql.*;

public class DatabaseUtil {
    public static Connection conn;
    public static Statement stmt;
    public static ResultSet rs;

    public static void checkConnection(String jdbcDriver, String dbUrl, String user, String pass){
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbUrl, user, pass);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(){
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
