package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorLogInController implements Initializable {

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
    private Button UserLogInButton;

    @FXML
    private Label loggingStatus;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane doctorHelloScene;

    @FXML
    public void userLogInButtonClicked(MouseEvent mouseEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 800, 500, false, false);
    }

    @FXML
    private void logInButtonClicked(ActionEvent event) throws IOException {
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

    private void checkWrittenText() throws IOException {
        if (username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Username and Password can't be empty!");
        else if (!username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Password can't be empty!");
        else if (username.getText().isEmpty() && !password.getText().isEmpty())
            loggingStatus.setText("Username can't be empty!");
        else {
            message.sendDoctorLoginMessage(SendToServer, Client.clientId + "," + username.getText() + "," + password.getText());
            String serverAnswer = Client.getServerResponse(ReadFromServer);

            if (serverAnswer.startsWith("Doctor logged successfully. Your doctor_id: ") && Integer.parseInt(serverAnswer.substring(44)) > 0) {
                Client.doctor_id = Integer.parseInt(serverAnswer.substring(44));
                loggingStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                loggingStatus.setText("Logged succesfully!");
                username.setText("");
                password.setText("");
                logInButton.requestFocus();

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(500), event -> {
                            try {
                                new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml", 1920, 1080, screenWidth, screenHeight, true, true,"HealthGuardian - doctorID: " + Client.clientId + ", doctor_id: " + Client.doctor_id);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                );
                timeline.setCycleCount(1);
                timeline.play();

            } else {
                loggingStatus.setText("Wrong doctor username or password!");
                System.out.println(serverAnswer);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorLogInController.ReadFromServer = Client.ReadFromServer;
        DoctorLogInController.SendToServer = Client.SendToServer;
    }
}
