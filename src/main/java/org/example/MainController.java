package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

    @FXML
    private Button helloWorldButton; // Link the button using fx:id

    public void initialize() {
        // Set an action for the button
        helloWorldButton.setOnAction(event -> {
            System.out.println("Hello World Button Clicked!");
        });
    }

}