package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLOutput;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientPanelController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label dateLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label weightLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label presureLabel;

    @FXML
    private Label heightLabel;

    @FXML
    private Label dateOfLastUpdateLabel;

    @FXML
    private Button ePrescriptionButton;

    @FXML
    private Button eReferralButton;

    @FXML
    private Button testScheduleButton;


    @FXML
    private Button findingsButton;

    @FXML
    private Button eContactWithADoctorButton;

    @FXML
    private Button listOfClinicsButton;

    @FXML
    private Button MessageButton;

    @FXML
    private Button presurePanelButton;

    @FXML
    private Button medicalHistoryButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button editDataButton;

    @FXML
    private Button SMIButton;

    @FXML
    private Button logOutButton;

    @FXML
    private AnchorPane clientPanelScene;

    @FXML
    private void ePrescriptionButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("EPrescriptionScene.fxml");
    }

    @FXML
    private void eReferralButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("EReferralScene.fxml");
    }

    @FXML
    private void testScheduleButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("TestScheduleScene.fxml");
    }

    @FXML
    private void findingsButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("FindingsScene.fxml");
    }

    @FXML
    private void eContactWithADoctorButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("EContactScene.fxml");
    }

    @FXML
    private void listOfClinicsButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("listOfClinicsScene.fxml");
    }

    @FXML
    private void messageButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("MessagesScene.fxml");
    }

    @FXML
    private void presurePanelButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("PresurePanelScene.fxml");
    }

    @FXML
    private void medicalHistoryButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("MedicalHistoryScene.fxml");
    }

    @FXML
    private void settingsButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("SettingsScene.fxml");
    }

    @FXML
    private void editDataButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("EditDataScene.fxml");
    }

    @FXML
    private void SMIButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("ShortMedicalInterviewScene.fxml");
    }

    @FXML
    private void LogOutButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    private void getUserDataFromDB() throws IOException {
        int user_id = Client.user_id;
        String user_id_str = Integer.toString(user_id);
        message.sendGetNameMessage(SendToServer,Client.clientId  + "," + user_id_str);
        String serverAnswer = Client.rreader(ReadFromServer);
        System.out.println(serverAnswer);
        if(serverAnswer.startsWith("[firstLogin-insertData")) {
            TextField inputField = new TextField();
            TextField inputField1 = new TextField();
            DatePicker datePicker = new DatePicker();
            Label label = new Label("");
            Alert alert = createDataAlert(inputField, inputField1, datePicker, label);

            alert.setOnCloseRequest(alertEvent -> {
                if (inputField.getText().isEmpty()) {
                    alertEvent.consume(); // cancel closing
                    label.setTextFill(Paint.valueOf("#ff0000"));
                    label.setText("Empty code!");
                } else {
                    //message.checkOneTimeCode(SendToServer, Client.clientId + "," + inputField.getText() + "," + firstName.getText() + "," + lastName.getText() + "," + email.getText() + "," + phoneNumber.getText() + "," + pesel.getText() + "," + username.getText() + "," + password.getText());
                    String serverAnswer1="";
                    System.out.println("aaaaa");

//                    try {
//                        //serverAnswer1 = Client.rreader(ReadFromServer);
//                        //System.out.println(serverAnswer1);
//
//                        if (serverAnswer1.startsWith("CODE RESULT:") && serverAnswer1.substring(12).equals("true")) {
//
//
//                        } else if (serverAnswer1.startsWith("CODE RESULT:") && serverAnswer1.substring(12).equals("false")){
//                            alertEvent.consume(); // cancel closing
//                            label.setTextFill(Paint.valueOf("#ff0000"));
//                            label.setText("Wrong code!");
//                        } else {
//                            alertEvent.consume(); // cancel closing
//                            label.setTextFill(Paint.valueOf("#ff0000"));
//                            label.setText("Error in database!");
//                        }
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
                }
            });
            Platform.runLater(alert::showAndWait);
        }

        String[] userData = serverAnswer.substring(1,serverAnswer.length()-1).split(", ");

        firstNameLabel.setText(userData[0]);
        lastNameLabel.setText(userData[1]);
        //birthDateLabel.setText(serverAnswer);
        weightLabel.setText("weight: " + userData[3]);
        heightLabel.setText("height: " + userData[4]);
        presureLabel.setText("last pressure: " + userData[5] + "/" + userData[6]);
        ageLabel.setText("age: " + userData[2]);
        //dateOfLastUpdateLabel.setText(serverAnswer);
    }

    private Alert createDataAlert(TextField inputField, TextField inputField1, DatePicker datePicker, Label label) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        label.setMinWidth(100);
        label.setMinHeight(30);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        grid.add(new Label("Birth date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Birth date:"), 0, 1);
        grid.add(inputField, 1, 1);
        grid.add(new Label("Birth date:"), 0, 2);
        grid.add(inputField1, 1, 2);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Insert data:");
        alert.setHeaderText("Write below your data.");
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButtonType);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, grid);
        vBox.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(vBox);

        return alert;
    }

    public void initialize(URL location, ResourceBundle resources) {
        ClientPanelController.ReadFromServer = Client.ReadFromServer;
        ClientPanelController.SendToServer = Client.SendToServer;

        try {
            getUserDataFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Utwórz Timeline do cyklicznego odświeżania daty co sekundę
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateDateTime() {
        // Pobierz aktualną datę i godzinę
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Utwórz formatter daty i godziny
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Sformatuj datę i godzinę jako tekst
        String formattedDateTime = currentDateTime.format(formatter);

        // Ustaw sformatowaną datę i godzinę jako tekst etykiety
        this.dateLabel.setText(formattedDateTime);

        if (this.dateLabel != null) {
            this.dateLabel.setText(formattedDateTime);
        } else {
            System.err.println("dateLabel is null");
        }
    }
}
