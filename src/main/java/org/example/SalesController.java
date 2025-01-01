package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesController {
    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerPhoneField;

    @FXML
    private TextField orderSearchBar, itemSearchBar, quantityField, discountField, amountReceivedField;
    @FXML
    private TextArea billingAddressField, shippingAddressField, itemDescriptionField;
    @FXML
    private ListView<String> ordersList, itemsList, addedItemsList;
    @FXML
    private Label selectedOrderLabel;

    private final List<Item> items = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Item> addedItems = new ArrayList<>();

    @FXML
    public void initialize() {
        loadItemsFromDatabase();
        loadOrdersFromDatabase();

        ordersList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayOrderDetails(newSelection);
            }
        });

        itemsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayItemDetails(newSelection);
            }
        });
    }

    @FXML
    public void searchOrders() {
        String query = orderSearchBar.getText().toLowerCase();
        ordersList.getItems().clear();

        for (Order order : orders) {
            if (order.getId().toLowerCase().contains(query)) {
                ordersList.getItems().add(order.getId());
            }
        }
    }

    @FXML
    private void addItemToBill() {
        String selectedItem = itemsList.getSelectionModel().getSelectedItem();
        String quantityText = quantityField.getText().trim();

        if (selectedItem == null || quantityText.isEmpty()) {
            showAlert("Input Error", "Please select an item and enter the quantity.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            for (Item item : items) {
                if (item.getName().equals(selectedItem)) {
                    if (quantity > Integer.parseInt(item.getQuantity())) {
                        showAlert("Stock Error", "Not enough stock for the selected item.");
                        return;
                    }

                    // Correctly pass arguments to the Item constructor
                    addedItems.add(new Item(item.getId(), item.getName(), item.getBarcode(),
                            item.getPrice(), String.valueOf(quantity), item.getImagePath()));
                    addedItemsList.getItems().add(item.getName() + " (Qty: " + quantity + ")");
                    quantityField.clear();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Quantity must be a valid number.");
        }
    }

    @FXML
    public void searchItems() {
        String query = itemSearchBar.getText().toLowerCase();
        itemsList.getItems().clear();

        for (Item item : items) {
            if (item.getName().toLowerCase().contains(query)) {
                itemsList.getItems().add(item.getName());
            }
        }
    }

    private void loadItemsFromDatabase() {
        String sql = "SELECT id, name, barcode, price, quantity, image_path FROM items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("barcode"),
                        String.valueOf(rs.getDouble("price")),
                        rs.getString("quantity"),
                        rs.getString("image_path")
                );
                items.add(item);
                itemsList.getItems().add(item.getName());
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load items from the database: " + e.getMessage());
        }
    }

    private void loadOrdersFromDatabase() {
        String sql = "SELECT id, details FROM orders";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(rs.getString("id"), rs.getString("details"));
                orders.add(order);
                ordersList.getItems().add(order.getId());
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load orders from the database: " + e.getMessage());
        }
    }

    @FXML
    private void downloadBill() {
        if (addedItems.isEmpty()) {
            showAlert("Error", "No items added to generate the bill.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bill");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // Ensure the file has a .pdf extension
            if (!file.getName().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            String billingAddress = billingAddressField.getText().trim();
            String shippingAddress = shippingAddressField.getText().trim();

            double subtotal = 0.0;
            String[][] itemsArray = new String[addedItems.size()][5];

            for (int i = 0; i < addedItems.size(); i++) {
                Item item = addedItems.get(i);
                itemsArray[i][0] = item.getId();
                itemsArray[i][1] = item.getName();
                itemsArray[i][2] = item.getQuantity();
                itemsArray[i][3] = item.getPrice();
                double total = Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity());
                itemsArray[i][4] = String.format("%.2f", total);
                subtotal += total;
            }

            double cgst = subtotal * 0.09; // 9% CGST
            double sgst = subtotal * 0.09; // 9% SGST

            // Parse Discount
            double discount = 0.0;
            if (!discountField.getText().trim().isEmpty()) {
                try {
                    discount = Double.parseDouble(discountField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Discount must be a valid number.");
                    return;
                }
            }

            // Parse Received Amount
            double receivedAmount = 0.0;
            if (!amountReceivedField.getText().trim().isEmpty()) {
                try {
                    receivedAmount = Double.parseDouble(amountReceivedField.getText().trim());
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Received Amount must be a valid number.");
                    return;
                }
            }

            double totalAmount = subtotal + cgst + sgst - discount;
            double dueAmount = totalAmount - receivedAmount;

            // Generate PDF
            new BillGenerator().generateBill(file.getAbsolutePath(), billingAddress, shippingAddress,
                    itemsArray, subtotal, cgst, sgst, discount, totalAmount, receivedAmount, dueAmount);
            showAlert("Success", "Bill saved successfully at: " + file.getAbsolutePath());

            // Add order to database
            saveOrderToDatabase("Order " + (ordersList.getItems().size() + 1), addedItems, totalAmount);

            // Update ordersList
            ordersList.getItems().add("Order " + (ordersList.getItems().size() + 1));
            addedItems.clear();
            addedItemsList.getItems().clear();
        }
        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();

        if (customerName.isEmpty() || customerPhone.isEmpty()) {
            showAlert("Input Error", "Please enter customer name and phone number.");
            return;
        }

        saveCustomerDetails(customerName, customerPhone);

        // Clear the fields after saving
        customerNameField.clear();
        customerPhoneField.clear();
    }

    private void saveCustomerDetails(String name, String phone) {
        String sql = "INSERT INTO customers (name, phone) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.executeUpdate();
            System.out.println("Customer saved: " + name + " (" + phone + ")");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save customer: " + e.getMessage());
        }
    }

    private void saveOrderToDatabase(String orderName, List<Item> items, double totalAmount) {
        String details = items.toString(); // Convert items to a string or JSON
        String dateCreated = java.time.LocalDate.now().toString();

        String sql = "INSERT INTO orders (id, details, total_amount, date_created) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderName);
            stmt.setString(2, details);
            stmt.setDouble(3, totalAmount);
            stmt.setString(4, dateCreated);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save order: " + e.getMessage());
        }
    }

    private void displayItemDetails(String itemName) {
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                itemDescriptionField.setText("Name: " + item.getName() +
                        "\nPrice: ₹" + item.getPrice() +
                        "\nStock: " + item.getStock());
                break;
            }
        }
    }

    private void displayOrderDetails(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                showAlert("Order Details", order.getDetails());
                break;
            }
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
    @FXML
    public void switchToItemsPage() {
        Main.switchToItemsPage(); // Call the method from Main to switch to the Items Page
    }
}