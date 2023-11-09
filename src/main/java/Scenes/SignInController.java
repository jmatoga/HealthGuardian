package Scenes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxId.getItems().addAll("+48","+69");
        comboBoxId.getSelectionModel().selectFirst();
    }

    @FXML
    private void enterPressedInField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }

    private void checkWrittenText() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || phoneNumber.getText().isEmpty() || pesel.getText().isEmpty() || username.getText().isEmpty() || password.getText().isEmpty())
            signInStatus.setText("You have to fill all gaps!");
        else if (firstName.getText().contains(" "))
            signInStatus.setText("Wrong first name, space inside!");
        else if (lastName.getText().contains(" "))
            signInStatus.setText("Wrong last name, space inside!");
        else if (!email.getText().contains("@") || email.getText().contains(" ") || email.getText().lastIndexOf('@') != email.getText().indexOf('@')) // if contains 2 @
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
//            if (Server.chceckUsernameAndPasswordInDataBase(username.getText(), password.getText())) {
//                signInStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
//                signInStatus.setText("Signed in succesfully!");
//
//
//                Platform.runLater(() -> {
//                    try {
//                        Thread.sleep(1500);
//                        username.setText("");
//                        password.setText("");
//                        new SceneSwitch(signInScene, "ClientPanelScene.fxml");
////                      Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ClientPanelScene.fxml")));
////                      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
////                      scene = new Scene(root);
////                      stage.setScene(scene);
////                      stage.show();
//                    } catch (InterruptedException e) {
//                        // Obsłuż ewentualny wyjątek spowodowany przerwaniem uśpienia
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//            } else
//                signInStatus.setText("Wrong username or password!");
        }
    }

    @FXML
    private void unHighlightButtonsOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }
}
