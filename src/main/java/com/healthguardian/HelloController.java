package com.healthguardian;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void onLogInButtonClick(ActionEvent actionEvent) {
        welcomeText.setText("Logged in!");
    }

    public void handleButton1Click(ActionEvent actionEvent) {
    }
}