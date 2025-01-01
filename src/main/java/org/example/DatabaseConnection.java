package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:business_app.db"; // Adjust path if necessary

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found.");
            throw new SQLException("Driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createItemsTable = "CREATE TABLE IF NOT EXISTS items (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "barcode TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "image_path TEXT, " +
                "discount REAL DEFAULT 0" +
                ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "id TEXT PRIMARY KEY, " +
                "details TEXT NOT NULL, " +
                "date_created TEXT NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Create items table
            stmt.execute(createItemsTable);
            System.out.println("Items table initialized successfully.");

            // Create orders table
            stmt.execute(createOrdersTable);
            System.out.println("Orders table initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
}