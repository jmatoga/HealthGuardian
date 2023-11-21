package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Message;

import java.awt.*;
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

import static javafx.scene.paint.Color.GREEN;

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

    @FXML
    private void initializeHoverEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(GREEN);  // Change color as per your preference

        ePrescriptionButton.setOnMouseEntered(e -> {
            ePrescriptionButton.setEffect(shadow);
        });

        ePrescriptionButton.setOnMouseExited(e -> {
            ePrescriptionButton.setEffect(null);
        });
    }

    private void getUserDataFromDB() throws IOException {
        int user_id = Client.user_id;
        String user_id_str = Integer.toString(user_id);
        message.sendGetNameMessage(SendToServer,Client.clientId  + "," + user_id_str);
        String serverAnswer = Client.rreader(ReadFromServer);
        System.out.println(serverAnswer);
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

    public void initialize(URL location, ResourceBundle resources) {
        initializeHoverEffect();
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
