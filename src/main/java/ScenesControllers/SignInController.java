package ScenesControllers;

import Client.Client;
import com.healthguardian.WindowApplication;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class SignInController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

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
        SignInController.ReadFromServer = Client.ReadFromServer;
        SignInController.SendToServer = Client.SendToServer;
    }

    @FXML
    private void SignInButtonClicked(ActionEvent event) throws IOException {
        signInUser();
    }

    @FXML
    private void LogInButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("LogInScene.fxml", 800, 500, false, false);
    }

    @FXML
    private void enterPressedInField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            signInUser();
    }

    private void signInUser() throws IOException {
        if(checkWrittenText()) {
            message.checkIfUserExists(SendToServer, Client.clientId + "," + username.getText());

            String serverAnswer = Client.rreader(ReadFromServer);
            Platform.runLater(() -> {
                System.out.println(serverAnswer);
            });

            if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("false")) {
                TextField inputField = new TextField();
                Label label = new Label("");
                Alert alert = createCodeAlert(inputField, label);

                alert.setOnCloseRequest(alertEvent -> {
                    if (inputField.getText().isEmpty()) {
                        alertEvent.consume(); // cancel closing
                        label.setTextFill(Paint.valueOf("#ff0000"));
                        label.setText("Empty code!");
                    } else {
                        message.checkOneTimeCode(SendToServer, Client.clientId + "," + inputField.getText() + "," + firstName.getText() + "," + lastName.getText() + "," + email.getText() + "," + phoneNumber.getText() + "," + pesel.getText() + "," + username.getText() + "," + password.getText());
                        String serverAnswer1;

                        try {
                            serverAnswer1 = Client.rreader(ReadFromServer);
                            System.out.println(serverAnswer1);

                            if (serverAnswer1.startsWith("CODE RESULT:") && serverAnswer1.substring(12).equals("true")) {
                                signInStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                                signInStatus.setText("Signed in succesfully!");
                                firstName.setText("");
                                lastName.setText("");
                                email.setText("");
                                phoneNumber.setText("");
                                pesel.setText("");
                                username.setText("");
                                password.setText("");
                                Timeline timeline = new Timeline(
                                        new KeyFrame(Duration.millis(1300), TimeEvent -> {
                                            try {
                                                new SceneSwitch("LogInScene.fxml", 800, 500, false, false);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        ));
                                timeline.setCycleCount(1);
                                timeline.play();

                            } else if (serverAnswer1.startsWith("CODE RESULT:") && serverAnswer1.substring(12).equals("false")){
                                alertEvent.consume(); // cancel closing
                                label.setTextFill(Paint.valueOf("#ff0000"));
                                label.setText("Wrong code!");
                            } else {
                                alertEvent.consume(); // cancel closing
                                label.setTextFill(Paint.valueOf("#ff0000"));
                                label.setText("Error in database!");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                alert.showAndWait();
            } else {
                signInStatus.setText("Username is used, change it!");
            }
        }
    }

    private Alert createCodeAlert(TextField inputField, Label label) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        label.setMinWidth(100);
        label.setMinHeight(30);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        grid.add(new Label("Write code:"), 0, 0);
        grid.add(inputField, 1, 0);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("One time registration code");
        alert.setHeaderText("Write below one time code to sign in.");
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButtonType);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, grid);
        vBox.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(vBox);

        return alert;
    }

    private boolean checkWrittenText() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || phoneNumber.getText().isEmpty() || pesel.getText().isEmpty() || username.getText().isEmpty() || password.getText().isEmpty())
            signInStatus.setText("You have to fill all gaps!");
        else if (firstName.getText().contains(" ") || !firstName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong first name!");
        else if (lastName.getText().contains(" ") || !lastName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong last name!");
        else if (!email.getText().contains("@") || !email.getText().contains(".") || email.getText().contains(" ") || email.getText().lastIndexOf('.') != email.getText().indexOf('.') || email.getText().lastIndexOf('@') != email.getText().indexOf('@') || email.getText().length() < 6 ) // if contains 2 @ or 2 .
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
            signInStatus.setText("");
            return true;
        }

        return false;
    }

    @FXML
    private void unHighlightButtonsOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }
}
