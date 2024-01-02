package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DoctorPanelController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label dateLabel, firstNameLabel, lastNameLabel, professionLabel;

    public void messageButtonClicked(ActionEvent actionEvent) {
    }

    public void settingsButtonClicked(ActionEvent actionEvent) {
    }

    public void logOutButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    public void patientButtonClicked(ActionEvent actionEvent) {
    }

    public void testScheduleButtonClicked(ActionEvent actionEvent) {
    }

    public void eContactButtonClicked(ActionEvent actionEvent) {
    }

    public void listOfClinicsButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorListOfClinicsScene.fxml");
    }

    void getDoctorDataFromDB() throws IOException {
        message.sendGetDoctorDataMessage(SendToServer,Client.clientId  + "," + Client.doctor_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorPanelController.ReadFromServer = Client.ReadFromServer;
        DoctorPanelController.SendToServer = Client.SendToServer;

        try {
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
