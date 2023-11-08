package com.healthguardian;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PasswordFieldController implements Initializable {
    @FXML
    private PasswordField passwordField;

    @FXML
    void passwordFieldKeyTyped(KeyEvent event) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
