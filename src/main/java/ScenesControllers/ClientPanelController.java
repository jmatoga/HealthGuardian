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
import javafx.scene.layout.AnchorPane;
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

    //private volatile boolean stop = false;

    private static Message message;
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

    private void getUserDataFromDB() throws IOException {
        int user_id = Client.user_id;
        String user_id_str = Integer.toString(user_id);
        message.sendGetNameMessage(SendToServer, user_id_str);
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
        ClientPanelController.message = LogInController.getMessageResources();
        ClientPanelController.ReadFromServer = LogInController.getReadFromServerResources();
        ClientPanelController.SendToServer = LogInController.getSendToServerResources();

        try {
            getUserDataFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Utwórz Timeline do cyklicznego odświeżania daty co sekundę
        //Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDateTime()));
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
    }

    private void updateDateTime() {
        // Pobierz aktualną datę i godzinę
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Utwórz formatter daty i godziny
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Sformatuj datę i godzinę jako tekst
        String formattedDateTime = currentDateTime.format(formatter);

        // Ustaw sformatowaną datę i godzinę jako tekst etykiety
        dateLabel.setText(formattedDateTime);
        System.out.println(formattedDateTime);
    }

}
