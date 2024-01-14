package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLogInController implements Initializable {

    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private final int screenWidth = toolkit.getScreenSize().width;
    private final int screenHeight = toolkit.getScreenSize().height;
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private TextField adminId;

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
    private AnchorPane adminHelloScene;

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
        if (adminId.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("ID and Password can't be empty!");
        else if (!adminId.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("ID can't be empty!");
        else if (adminId.getText().isEmpty() && !password.getText().isEmpty())
            loggingStatus.setText("ID can't be empty!");
        else if(!adminId.getText().matches("\\d+"))
            loggingStatus.setText("ID must be a number!");
        else {
            message.sendAdminLoginMessage(SendToServer, Client.clientId + "#/#" + adminId.getText() + "#/#" + password.getText());
            String serverAnswer = Client.getServerResponse(ReadFromServer);

            if (serverAnswer.startsWith("Admin logged successfully. Your admin_id: ") && Integer.parseInt(serverAnswer.substring(42)) > 0) {
                int admin_id = Integer.parseInt(serverAnswer.substring(42));
                loggingStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                loggingStatus.setText("Logged succesfully!");
                adminId.setText("");
                password.setText("");
                logInButton.requestFocus();

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(500), event -> {
                            try {
                                new SceneSwitch("/ScenesLayout/AdminPanelScene.fxml", 815, 520, 815, 520, false, false,"HealthGuardian - ADMIN: " + admin_id + ", ClientID: " + Client.clientId);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                );
                timeline.setCycleCount(1);
                timeline.play();

            } else {
                loggingStatus.setText("Wrong admin ID or password!");
                System.out.println(serverAnswer);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AdminLogInController.ReadFromServer = Client.ReadFromServer;
        AdminLogInController.SendToServer = Client.SendToServer;
    }
}
