package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

public class DoctorUserController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private ImageView imageView;

    @FXML
    private Label dateLabel;

    public void doctorPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    public void changeMedicalHistoryButtonClicked(ActionEvent actionEvent) {
    }

    public void prescribeEPrescriptionButtonClicked(ActionEvent actionEvent) {
    }

    public void prescribeEReferralButtonClicked(ActionEvent actionEvent) {
    }

    public void checkFindingsButtonClicked(ActionEvent actionEvent) {
    }

    public void addRecommendationsButtonClicked(ActionEvent actionEvent) {
    }

    public void checkDocumentationButtonClicked(ActionEvent actionEvent) {
    }

    @FXML
    private void unHighlightOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }

    private void getSettingsFromDB() throws IOException {
        message.sendGetDoctorSettingsMessage(SendToServer,Client.clientId  + "," + Client.doctor_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        String[] settingsData = serverAnswer.substring(1,serverAnswer.length()-1).split(", ");

        dateLabel.setVisible(settingsData[2].equals("false"));
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
        DoctorUserController.ReadFromServer = Client.ReadFromServer;
        DoctorUserController.SendToServer = Client.SendToServer;

        try {
            getSettingsFromDB();
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
