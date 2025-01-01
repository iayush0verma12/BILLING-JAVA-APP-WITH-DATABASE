package org.example;

public class Item {

    private String id;
    private String name;
    private String barcode;
    private String price; // Stored as String to avoid type mismatch
    private String quantity; // Stored as String to match database usage
    private String imagePath;

    public Item(String id, String name, String barcode, String price, String quantity, String imagePath) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
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
        return quantity; // Stock quantity
    }

    public String getImagePath() {
        return imagePath;
    }
    public String getStock() {
        return quantity;
    }
}