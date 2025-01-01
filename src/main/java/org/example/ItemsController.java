package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsController {

    @FXML
    private TextField idField, nameField, barcodeField, priceField, quantityField, imagePathField, discountField, searchBar;

    @FXML
    private ListView<String> itemsList;

    @FXML
    private TextArea itemDescriptionField, billingAddressField, shippingAddressField;

    @FXML
    private ImageView itemImage;

    private final List<Item> itemData = new ArrayList<>();

    @FXML
    public void initialize() {
        billingAddressField.setText("Default Billing Address");
        shippingAddressField.setText("Default Shipping Address");
        loadItemsFromDatabase();

        itemsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayItemDetails();
            }
        });
    }

    @FXML
    public void addItem() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String price = priceField.getText().trim();
        String quantity = quantityField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        String discountInput = discountField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || barcode.isEmpty() || price.isEmpty() || quantity.isEmpty() || discountInput.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            double priceValue = Double.parseDouble(price);
            int quantityValue = Integer.parseInt(quantity);
            double discountValue = Double.parseDouble(discountInput);

            saveItemToDatabase(id, name, barcode, priceValue, quantityValue, imagePath, discountValue);

            itemData.add(new Item(id, name, barcode, price, quantity, imagePath, discountValue));
            itemsList.getItems().add(name);
            clearInputFields();
            showAlert("Success", "Item added successfully!");
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Price, Quantity, and Discount must be valid numbers.");
        } catch (Exception e) {
            showAlert("Database Error", "Failed to save the item: " + e.getMessage());
        }
    }

    private void saveItemToDatabase(String id, String name, String barcode, double price, int quantity, String imagePath, double discount) throws SQLException {
        String sql = "INSERT INTO items (name, barcode, price, quantity, image_path, discount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, barcode);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setString(5, imagePath);
            stmt.setDouble(6, discount);
            stmt.executeUpdate();
        }
    }

    private void loadItemsFromDatabase() {
        String sql = "SELECT id, name, barcode, price, quantity, image_path, discount FROM items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String barcode = rs.getString("barcode");
                String price = rs.getString("price");
                String quantity = rs.getString("quantity");
                String imagePath = rs.getString("image_path");
                double discount = rs.getDouble("discount");

                itemData.add(new Item(id, name, barcode, price, quantity, imagePath, discount));
                itemsList.getItems().add(name);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load items from the database: " + e.getMessage());
        }
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    @FXML
    public void printBill() {
        if (itemData.isEmpty()) {
            showAlert("Error", "No items available to generate the bill.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bill");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String billingAddress = billingAddressField.getText().trim();
            String shippingAddress = shippingAddressField.getText().trim();
            double subtotal = 0.0;
            String[][] items = new String[itemData.size()][5];

            for (int i = 0; i < itemData.size(); i++) {
                Item item = itemData.get(i);
                items[i][0] = item.getId();
                items[i][1] = item.getName();
                items[i][2] = item.getQuantity();
                items[i][3] = item.getPrice();
                double total = Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity());
                items[i][4] = String.format("%.2f", total);
                subtotal += total;
            }

            double cgst = subtotal * 0.025;
            double sgst = subtotal * 0.025;
            double totalAmount = subtotal + cgst + sgst;

            new BillGenerator().generateBill(file.getAbsolutePath(), billingAddress, shippingAddress, items, subtotal, cgst, sgst, 0, totalAmount, totalAmount, 0);
            showAlert("Success", "Bill saved successfully at: " + file.getAbsolutePath());
        }
    }

    @FXML
    public void searchItems() {
        String query = searchBar.getText().toLowerCase();
        itemsList.getItems().clear();

        for (Item item : itemData) {
            if (item.getName().toLowerCase().contains(query) || item.getBarcode().toLowerCase().contains(query)) {
                itemsList.getItems().add(item.getName());
            }
        }

        if (itemsList.getItems().isEmpty()) {
            showAlert("Search Result", "No items found matching the query.");
        }
    }

    @FXML
    public void displayItemDetails() {
        String selectedItem = itemsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            for (Item item : itemData) {
                if (item.getName().equals(selectedItem)) {
                    if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                        itemImage.setImage(new Image("file:" + item.getImagePath()));
                    } else {
                        itemImage.setImage(null);
                    }

                    String description = "Name: " + item.getName() +
                            "\nBarcode: " + item.getBarcode() +
                            "\nPrice: ₹" + item.getPrice() +
                            "\nQuantity: " + item.getQuantity() +
                            "\nDiscount: ₹" + item.getDiscount();
                    itemDescriptionField.setText(description);
                    break;
                }
            }
        }
    }

    private void clearInputFields() {
        idField.clear();
        nameField.clear();
        barcodeField.clear();
        priceField.clear();
        quantityField.clear();
        imagePathField.clear();
        discountField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Item {
        private final String id;
        private final String name;
        private final String barcode;
        private final String price;
        private final String quantity;
        private final String imagePath;
        private final double discount;

        public Item(String id, String name, String barcode, String price, String quantity, String imagePath, double discount) {
            this.id = id;
            this.name = name;
            this.barcode = barcode;
            this.price = price;
            this.quantity = quantity;
            this.imagePath = imagePath;
            this.discount = discount;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getPrice() {
            return price;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getImagePath() {
            return imagePath;
        }

        public double getDiscount() {
            return discount;
        }
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