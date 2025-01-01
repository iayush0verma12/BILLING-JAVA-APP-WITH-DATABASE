package org.example;

public class Order {
    private String id;
    private String details;

    public Order(String id, String details) {
        this.id = id;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public String getDetails() {
        return details;
    }
}