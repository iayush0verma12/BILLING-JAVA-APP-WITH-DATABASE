package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML
    private VBox todaySalesCard, last7DaysSalesCard, last30DaysSalesCard, thisYearSalesCard;

    @FXML
    private Label todaySalesValue, todaySalesCount, last7DaysSalesValue, last7DaysSalesCount;
    @FXML
    private Label last30DaysSalesValue, last30DaysSalesCount, thisYearSalesValue, thisYearSalesCount;

    public void initialize() {
        refreshDashboardData();
    }

    private void refreshDashboardData() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Today's Sales
            calculateSales(conn, today.format(formatter), today.format(formatter), todaySalesValue, todaySalesCount);

            // Last 7 Days Sales
            calculateSales(conn, today.minusDays(7).format(formatter), today.format(formatter), last7DaysSalesValue, last7DaysSalesCount);

            // Last 30 Days Sales
            calculateSales(conn, today.minusDays(30).format(formatter), today.format(formatter), last30DaysSalesValue, last30DaysSalesCount);

            // This Year Sales
            calculateSales(conn, today.withDayOfYear(1).format(formatter), today.format(formatter), thisYearSalesValue, thisYearSalesCount);
        } catch (SQLException e) {
            System.err.println("Failed to fetch sales data: " + e.getMessage());
        }
    }

    private void calculateSales(Connection conn, String startDate, String endDate, Label salesValue, Label salesCount) throws SQLException {
        String sql = "SELECT SUM(total_amount) AS total_sales, COUNT(*) AS total_orders FROM orders WHERE date_created BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double totalSales = rs.getDouble("total_sales");
                int totalOrders = rs.getInt("total_orders");

                salesValue.setText("₹" + String.format("%.2f", totalSales));
                salesCount.setText("No. of Sales: " + totalOrders);
            }
        }
    }

    // Navigation methods
    @FXML
    public void switchToItemsPage() {
        Main.switchToItemsPage();
    }

    @FXML
    public void switchToDashboard() {
        Main.switchToDashboard();
    }

    @FXML
    public void switchToSales() {
        Main.switchToSales();
    }

    @FXML
    public void switchToCustomer() {
        Main.switchToCustomer();
    }

    @FXML
    public void switchToAdministrative() {
        Main.switchToAdministrative();
    }
}