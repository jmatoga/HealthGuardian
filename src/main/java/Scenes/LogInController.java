package Scenes;

import Server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class LogInController {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button logInButton;

    @FXML
    private Button signInButton;

    @FXML
    private Label loggingStatus;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane helloScene;

    @FXML
    private void logInButtonClicked(ActionEvent event) throws IOException {
        checkWrittenText();
    }

    @FXML
    private void enterPressedInField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }

    @FXML
    private void unHighlightButtonsOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }

    @FXML
    private void SignInButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch(helloScene, "SignInScene.fxml");
    }

//    private Stage stage;
//    private Scene scene;
//    private Parent root;

    private int user_id;
    private void checkWrittenText() throws IOException {
        if (username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Username and Password can't be empty!");
        else if (!username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Password can't be empty!");
        else if (username.getText().isEmpty() && !password.getText().isEmpty())
            loggingStatus.setText("Username can't be empty!");
        else {
            if (Server.chceckUsernameAndPasswordInDataBase(username.getText(), password.getText())) {
                loggingStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                loggingStatus.setText("Logged succesfully!");


                Platform.runLater(() -> {
                    try {
                        Thread.sleep(1500);
                        username.setText("");
                        password.setText("");
                        new SceneSwitch(helloScene, "ClientPanelScene.fxml");
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
                loggingStatus.setText("Wrong username or password!");
        }
    }
}



