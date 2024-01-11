package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DoctorPanelController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label dateLabel, firstNameLabel, lastNameLabel, professionLabel;

    public void messageButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorNotificationScene.fxml");
    }

    public void settingsButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorSettingsScene.fxml");
    }

    public void logOutButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    public void patientButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorUserScene.fxml");
    }

    public void examinationScheduleButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorExaminationScheduleScene.fxml");
    }

    public void eContactButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorEContactScene.fxml");
    }

    public void listOfClinicsButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorListOfClinicsScene.fxml");
    }

    void getDoctorDataFromDB() throws IOException {
        message.sendGetDoctorDataMessage(SendToServer,Client.clientId  + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] doctorData = serverAnswer.substring(1,serverAnswer.length()-1).split(", ");

        firstNameLabel.setText("dr. " + doctorData[0]);
        lastNameLabel.setText(doctorData[1]);
        professionLabel.setText(doctorData[2]);
    }

    private void updateDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Set as text
        String formattedDateTime = currentDateTime.format(formatter);
        this.dateLabel.setText(formattedDateTime);

        if (this.dateLabel != null) {
            this.dateLabel.setText(formattedDateTime);
        } else {
            System.err.println("dateLabel is null");
        }
    }

    private void getSettingsFromDB() throws IOException {
        message.sendGetDoctorSettingsMessage(SendToServer,Client.clientId  + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] settingsData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

        dateLabel.setVisible(settingsData[2].equals("false"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorPanelController.ReadFromServer = Client.ReadFromServer;
        DoctorPanelController.SendToServer = Client.SendToServer;

        try {
            getSettingsFromDB();
            getDoctorDataFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(dateLabel.isVisible()) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateDateTime()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }
}
