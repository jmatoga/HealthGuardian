package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
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
    private Label dateLabel;

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
        new SceneSwitch("/ScenesLayout/listOfClinicsScene.fxml");
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

        if(dateLabel.isVisible()) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateDateTime()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }
}
