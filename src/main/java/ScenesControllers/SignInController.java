package ScenesControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.lang.String;

import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    @FXML
    private AnchorPane signInScene;

    @FXML
    private ComboBox<String> comboBoxId;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField pesel;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Label signInStatus;

    @FXML
    private Button logInButton;

    @FXML
    private Button signInButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxId.getItems().addAll("+48","+69");
        comboBoxId.getSelectionModel().selectFirst();
    }

    @FXML
    private void SignInButtonClicked(ActionEvent event) {
        checkWrittenText();
    }

    @FXML
    private void LogInButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("LogInScene.fxml", 800, 500, false, false);
    }

    @FXML
    private void enterPressedInField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }

    private void checkWrittenText() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || phoneNumber.getText().isEmpty() || pesel.getText().isEmpty() || username.getText().isEmpty() || password.getText().isEmpty())
            signInStatus.setText("You have to fill all gaps!");
        else if (firstName.getText().contains(" ") || !firstName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong first name!");
        else if (lastName.getText().contains(" ") || !lastName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong last name!");
        else if (!email.getText().contains("@") || email.getText().contains(" ") || email.getText().lastIndexOf('@') != email.getText().indexOf('@') || email.getText().length() < 7 ) // if contains 2 @
            signInStatus.setText("Wrong email!");
        else if (phoneNumber.getText().length() != 9 || !phoneNumber.getText().matches("\\d+"))
            signInStatus.setText("Wrong phone number!");
        else if (pesel.getText().length() != 11 || pesel.getText().contains(" ") || !pesel.getText().matches("\\d+"))
            signInStatus.setText("Wrong pesel!");
        else if (username.getText().contains(" "))
            signInStatus.setText("Wrong username, space inside!");
        else if (password.getText().contains(" "))
            signInStatus.setText("Wrong password!");
        else {
            System.out.println("dsa");

        }
    }

    @FXML
    private void unHighlightButtonsOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }
}
