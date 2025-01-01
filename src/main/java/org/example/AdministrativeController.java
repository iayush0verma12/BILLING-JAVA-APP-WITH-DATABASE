package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AdministrativeController {

    @FXML
    private void resetDashboardData() {
        if (confirmReset()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM orders";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                    showAlert("Success", "Dashboard data has been reset.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset dashboard data: " + e.getMessage());
            }
        }
    }

    // Reset Sales Data
    @FXML
    private void resetSalesData() {
        if (confirmReset()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM orders";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                    showAlert("Success", "Sales data has been reset.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset sales data: " + e.getMessage());
            }
        }
    }

    // Reset Items Data
    @FXML
    private void resetItemsData() {
        if (confirmReset()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM items";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                    showAlert("Success", "Items data has been reset.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset items data: " + e.getMessage());
            }
        }
    }

    // Reset Customer Data
    @FXML
    private void resetCustomerData() {
        if (confirmReset()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM customers";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                    showAlert("Success", "Customer data has been reset.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset customer data: " + e.getMessage());
            }
        }
    }

    // Confirmation Dialog for Reset
    private boolean confirmReset() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmation");
        dialog.setHeaderText("Reset Confirmation");
        dialog.setContentText("Enter the confirmation code to proceed:");

        Optional<String> result = dialog.showAndWait();
        return result.isPresent() && result.get().equals("0600");
    }

    private void executeReset(String sql, String successMessage) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            showAlert("Success", successMessage);
        } catch (SQLException e) {
            showAlert("Error", "Failed to reset data: " + e.getMessage());
        }
    }


    @FXML
    private TextField userIdField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private TextArea logsArea;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        usersTable.setItems(usersList);

        // Load users into the table
        loadUsers();

        // Toggle password visibility
        showPasswordCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordField.setPromptText(passwordField.getText());
                passwordField.clear();
                passwordField.setDisable(true);
            } else {
                passwordField.setText(passwordField.getPromptText());
                passwordField.setPromptText("Password");
                passwordField.setDisable(false);
            }
        });
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

    // Add User
    @FXML
    public void addUser() {
        String userId = userIdField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "All fields are required.");
            return;
        }

        String query = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Integer.parseInt(userId));
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();

            logsArea.appendText("User added: " + username + "\n");
            loadUsers();
        } catch (SQLException e) {
            logsArea.appendText("Error adding user: " + e.getMessage() + "\n");
        }
    }

    // Update User
    @FXML
    public void updateUser() {
        String userId = userIdField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "All fields are required.");
            return;
        }

        String query = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, Integer.parseInt(userId));
            preparedStatement.executeUpdate();

            logsArea.appendText("User updated: " + username + "\n");
            loadUsers();
        } catch (SQLException e) {
            logsArea.appendText("Error updating user: " + e.getMessage() + "\n");
        }
    }

    // Remove User
    @FXML
    public void removeUser() {
        String userId = userIdField.getText().trim();

        if (userId.isEmpty()) {
            showAlert("Input Error", "User ID is required.");
            return;
        }

        String query = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, Integer.parseInt(userId));
            preparedStatement.executeUpdate();

            logsArea.appendText("User removed: " + userId + "\n");
            loadUsers();
        } catch (SQLException e) {
            logsArea.appendText("Error removing user: " + e.getMessage() + "\n");
        }
    }

    // Load Users into TableView
    private void loadUsers() {
        usersList.clear();

        String query = "SELECT * FROM users";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                usersList.add(new User(
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                ));
            }
        } catch (SQLException e) {
            logsArea.appendText("Error loading users: " + e.getMessage() + "\n");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class User {
        private final SimpleStringProperty id;
        private final SimpleStringProperty username;
        private final SimpleStringProperty password;

        public User(String id, String username, String password) {
            this.id = new SimpleStringProperty(id);
            this.username = new SimpleStringProperty(username);
            this.password = new SimpleStringProperty(password);
        }

        public String getId() {
            return id.get();
        }

        public String getUsername() {
            return username.get();
        }

        public String getPassword() {
            return password.get();
        }
    }


}