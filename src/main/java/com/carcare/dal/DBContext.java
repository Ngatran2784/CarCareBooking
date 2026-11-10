package com.carcare.dal;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DBContext quản lý kết nối MySQL JDBC.
 * Không hardcode mật khẩu, ưu tiên đọc từ db.properties hoặc biến môi trường.
 * Không làm gián đoạn ứng dụng web nếu CSDL tạm thời chưa bật.
 */
public class DBContext {
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static String dbUrl = "jdbc:mysql://localhost:3306/carcare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8";
    private static String dbUsername = "root";
    private static String dbPassword = "";

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                if (props.getProperty("db.driver") != null) dbDriver = props.getProperty("db.driver").trim();
                if (props.getProperty("db.url") != null) dbUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.username") != null) dbUsername = props.getProperty("db.username").trim();
                if (props.getProperty("db.password") != null) dbPassword = props.getProperty("db.password").trim();
            }
        } catch (Exception e) {
            System.err.println("[DBContext] Khong the tai file db.properties: " + e.getMessage());
        }

        // Ưu tiên biến môi trường nếu có
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.isBlank()) dbUrl = envUrl;
        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.isBlank()) dbUsername = envUser;
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) dbPassword = envPass;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(dbDriver);
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Khong tim thay JDBC Driver: " + dbDriver, e);
        }
    }

    public static boolean isDatabaseConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
