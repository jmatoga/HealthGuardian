package Scenes;

import Server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class HelloController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button buttonLogIn;

    @FXML
    private Label loginStatusLabel;
    private Label loginStatusLabel1;

    @FXML
    private AnchorPane hellowScene;

    @FXML
    private void buttonLogIn(ActionEvent event) throws IOException {
        checkWrittenText();
    }

    @FXML
    private void enterPressedInUsernameField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }
    @FXML
    private void enterPressedInPasswordField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }

//    private Stage stage;
//    private Scene scene;
//    private Parent root;

    private void checkWrittenText() throws IOException {
        if (usernameField.getText().isEmpty() && passwordField.getText().isEmpty())
            loginStatusLabel.setText("Username and Password can't be empty!");
        else if (!usernameField.getText().isEmpty() && passwordField.getText().isEmpty())
            loginStatusLabel.setText("Password can't be empty!");
        else if (usernameField.getText().isEmpty() && !passwordField.getText().isEmpty())
            loginStatusLabel.setText("Username can't be empty!");
        else {
            if (Server.chceckUsernameAndPasswordInDataBase(usernameField.getText(), passwordField.getText())) {
                loginStatusLabel.setTextFill(Paint.valueOf("0x2aff00"));
                loginStatusLabel.setText("Logged succesfully!");


                Platform.runLater(() -> {
                    try {
                        Thread.sleep(1500);
                        usernameField.setText("");
                        passwordField.setText("");
                        new SceneSwitch(hellowScene, "ClientPanelScene.fxml");
//                      Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ClientPanelScene.fxml")));
//                      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//                      scene = new Scene(root);
//                      stage.setScene(scene);
//                      stage.show();
                    } catch (InterruptedException e) {
                        // Obsłuż ewentualny wyjątek spowodowany przerwaniem uśpienia
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else
                loginStatusLabel.setText("Wrong username or password!");
        }
    }
}



