package ScenesControllers;

import Client.Client;
import com.healthguardian.WindowApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LogInController{

    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private int screenWidth = toolkit.getScreenSize().width;
    private int screenHeight = toolkit.getScreenSize().height;
    private static Message message;
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    public static void setLogInController(Message message, BufferedReader ReadFromServer, PrintWriter SendToServer) {
        LogInController.message = message;
        LogInController.ReadFromServer = ReadFromServer;
        LogInController.SendToServer = SendToServer;
    }

    public static Message getMessageResources() {
        return message;
    }

    public static BufferedReader getReadFromServerResources() {
        return ReadFromServer;
    }

    public static PrintWriter getSendToServerResources() {
        return SendToServer;
    }

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
}
