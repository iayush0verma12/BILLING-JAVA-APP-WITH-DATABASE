package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> customersList;

    @FXML
    private ImageView customerImage;

    @FXML
    private Label customerName, customerContact, customerAddress;

    public void initialize() {
        loadCustomersFromDatabase();

        customersList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadCustomerDetails(newVal);
            }
        });
    }

    private void loadCustomersFromDatabase() {
        String sql = "SELECT phone FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customersList.getItems().add(rs.getString("phone"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load customers: " + e.getMessage());
        }
    }

    @FXML
    public void searchCustomers() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            customersList.getItems().clear();
            loadCustomersFromDatabase();
            return;
        }

        String sql = "SELECT phone FROM customers WHERE phone LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            customersList.getItems().clear();
            while (rs.next()) {
                customersList.getItems().add(rs.getString("phone"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to search customers: " + e.getMessage());
        }
    }

    private void loadCustomerDetails(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customerName.setText(rs.getString("name"));
                customerContact.setText(rs.getString("phone"));
                customerAddress.setText("Address not available"); // Update if address is added
                customerImage.setImage(new Image("/path/to/default/image.png"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load customer details: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void switchToDashboard() {
        Main.switchToDashboard();
    }

    @FXML
    public void switchToItems() {
        Main.switchToItemsPage();
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