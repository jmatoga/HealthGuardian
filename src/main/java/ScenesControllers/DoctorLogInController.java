package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Message;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorLogInController implements Initializable {
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

    public void unHighlightButtonsOnImageView(MouseEvent mouseEvent) {
    }

    public void logInButtonClicked(ActionEvent actionEvent) {
    }

    public void enterPressedInField(KeyEvent keyEvent) {
    }

    public void userLogInButtonClicked(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorLogInController.ReadFromServer = Client.ReadFromServer;
        DoctorLogInController.SendToServer = Client.SendToServer;
    }
}
