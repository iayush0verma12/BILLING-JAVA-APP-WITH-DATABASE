package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        DatabaseConnection.initializeDatabase();

        // Load Login Page initially
        switchToLogin();
    }

    public static void switchToLogin() {
        try {
            Parent loginRoot = FXMLLoader.load(Main.class.getResource("/login.fxml"));
            primaryStage.setTitle("Login Page");
            primaryStage.setScene(new Scene(loginRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading login.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToDashboard() {
        try {
            Parent dashboardRoot = FXMLLoader.load(Main.class.getResource("/dashboard.fxml"));
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(new Scene(dashboardRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading dashboard.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToItemsPage() {
        try {
            Parent itemsRoot = FXMLLoader.load(Main.class.getResource("/items.fxml"));
            primaryStage.setTitle("Items Page");
            primaryStage.setScene(new Scene(itemsRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading items.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToSales() {
        try {
            Parent salesRoot = FXMLLoader.load(Main.class.getResource("/sales.fxml"));
            primaryStage.setTitle("Sales Page");
            primaryStage.setScene(new Scene(salesRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading sales.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToAdministrative() {
        try {
            Parent administrativeRoot = FXMLLoader.load(Main.class.getResource("/administrative.fxml"));
            primaryStage.setTitle("Administrative Page");
            primaryStage.setScene(new Scene(administrativeRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading administrative.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToCustomer() {
        try {
            Parent customerRoot = FXMLLoader.load(Main.class.getResource("/customer.fxml"));
            primaryStage.setTitle("Customer Page");
            primaryStage.setScene(new Scene(customerRoot));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading customer.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}