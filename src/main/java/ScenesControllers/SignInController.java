package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;

import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    AnchorPane signInScene;

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
        comboBoxId.getItems().addAll("+48");
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
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 800, 500, false, false);
    }

    @FXML
    private void enterPressedInField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            signInUser();
    }

    void signInUser() throws IOException {
        if(checkWrittenText()) {
            message.checkIfUserExists(SendToServer, Client.clientId + "," + username.getText() + "," + email.getText());

            String serverAnswer = Client.getServerResponse(ReadFromServer);

            if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("Free to use")) {
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
                            serverAnswer1 = ReadFromServer.readLine();
                            System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer1);

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
                                                new SceneSwitch("/ScenesLayout/LogInScene.fxml", 800, 500, false, false);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }));
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
            } else if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("User exists")) {
                signInStatus.setText("Username is used, change it!");
            } else if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("Email exists")) {
                signInStatus.setText("Email is used, change it!");
            } else {
                signInStatus.setText("Error in database! Try again later.");
                System.out.println(Color.ColorString("Error in database while signing in! Try again later.", Color.ANSI_RED));
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

    boolean checkWrittenText() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || phoneNumber.getText().isEmpty() || pesel.getText().isEmpty() || username.getText().isEmpty() || password.getText().isEmpty())
            signInStatus.setText("You have to fill all gaps!");
        else if (firstName.getText().contains(" ") || !firstName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong first name!");
        else if (lastName.getText().contains(" ") || !lastName.getText().matches("^[a-zA-Z]+$"))
            signInStatus.setText("Wrong last name!");
        else if (!email.getText().contains("@") || !email.getText().contains(".") || email.getText().contains(" ") || email.getText().lastIndexOf('.') != email.getText().indexOf('.') || email.getText().lastIndexOf('@') != email.getText().indexOf('@') || email.getText().length() < 6 || email.getText().indexOf(".", email.getText().indexOf("@")) != email.getText().lastIndexOf(".") || email.getText().substring(email.getText().lastIndexOf(".")).length() <= 2) // if contains 2 @ or 2 . , . should be after @ , after . should be at least 2 chars
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

    public void setFirstName(String s) {
        firstName.setText(s);
    }

    public void setLastName(String s) {
        lastName.setText(s);
    }

    public void setEmail(String s) {
        email.setText(s);
    }

    public void setPhoneNumber(String s) {
        phoneNumber.setText(s);
    }

    public void setPesel(String s) {
        pesel.setText(s);
    }

    public void setUsername(String s) {
        username.setText(s);
    }

    public void setPassword(String s) {
        password.setText(s);
    }

    public Label getSignInStatus() {
        return signInStatus;
    }

    public void setFirstName(TextField firstName) {
        this.firstName = firstName;
    }

    public void setLastName(TextField lastName) {
        this.lastName = lastName;
    }

    public void setEmail(TextField email) {
        this.email = email;
    }

    public void setPhoneNumber(TextField phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPesel(TextField pesel) {
        this.pesel = pesel;
    }

    public void setUsername(TextField username) {
        this.username = username;
    }

    public void setPassword(TextField password) {
        this.password = password;
    }

    public void setSignInStatus(Label signInStatus) {
        this.signInStatus = signInStatus;
    }

    public void setSignInButton(Button signInButton) {
        this.signInButton = signInButton;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public TextField getFirstName() {
        return firstName;
    }
}
