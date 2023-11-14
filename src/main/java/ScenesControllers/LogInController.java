package ScenesControllers;

import Client.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private final int screenWidth = toolkit.getScreenSize().width;
    private final int screenHeight = toolkit.getScreenSize().height;
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

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
    private void logInButtonClicked(ActionEvent event) throws IOException{
        checkWrittenText();
    }

    @FXML
    private void enterPressedInField(KeyEvent event) throws IOException{
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
        new SceneSwitch("SignInScene.fxml", 800, 500, false, false);
    }

    private void checkWrittenText() throws IOException{
        if (username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Username and Password can't be empty!");
        else if (!username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Password can't be empty!");
        else if (username.getText().isEmpty() && !password.getText().isEmpty())
            loggingStatus.setText("Username can't be empty!");
        else {
            message.sendLoginMessage(SendToServer, Client.clientId + "," + username.getText() + "," + password.getText());
            String serverAnswer = Client.rreader(ReadFromServer);

            if (serverAnswer.startsWith("Logged successfully. Your user_id: ") && Integer.parseInt(serverAnswer.substring(35)) > 0) {
                Client.user_id = Integer.parseInt(serverAnswer.substring(35));

                loggingStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                loggingStatus.setText("Logged succesfully!");

                Platform.runLater(() -> {
                    try {
                        System.out.println(serverAnswer);

                        Thread.sleep(1000);
                        username.setText("");
                        password.setText("");
                        new SceneSwitch("ClientPanelScene.fxml", 1920, 1080, screenWidth, screenHeight, true, true,"HealthGuardian - clientID: " + Client.clientId + " ,user_id: " + Client.user_id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Platform.runLater(() -> System.out.println(serverAnswer));
                loggingStatus.setText("Wrong username or password!");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LogInController.ReadFromServer = Client.ReadFromServer;
        LogInController.SendToServer = Client.SendToServer;
    }
}
