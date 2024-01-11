package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EContactController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;
    @FXML
    public Label DoctorLastNameLabel;

    @FXML
    public Label DoctorProfessionLabel;

    @FXML
    public Label ContactDateLabel;

    @FXML
    public Label ContactHourLabel;

    @FXML
    public Button testScheduleButton;

    @FXML
    public Button RefreshButton;

    @FXML
    public Label examinationNameLabel;

    @FXML
    private Button userPanelButton;

    @FXML
    private Button copyButton;

    @FXML
    private Label DoctorFirstNameLabel;

    @FXML
    private Label correctLabel;

    @FXML
    private Label linkLabel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime dateTime;


    @FXML
    public void copyToText(ActionEvent actionEvent) {
        String textToCopy = linkLabel.getText();

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(textToCopy);

        Clipboard.getSystemClipboard().setContent(clipboardContent);


        correctLabel.setText("LINK COPY CORRECTLY!");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1300), TimeEvent -> {
                    correctLabel.setText("");
                }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void SendEContactInformation() throws IOException {
        message.sendGetEContact(SendToServer, Client.clientId + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No EContact information in database]]")) {
            DoctorFirstNameLabel.setStyle("-fx-text-fill:ORANGE");
            DoctorLastNameLabel.setStyle("-fx-text-fill:ORANGE");
            DoctorFirstNameLabel.setText("YOU DON'T HAVE ANY");
            DoctorLastNameLabel.setText("   E-CONTACT NOW");
            DoctorProfessionLabel.setText("");
            examinationNameLabel.setText("");
            ContactDateLabel.setText("");
            ContactHourLabel.setText("");
            linkLabel.setText("");
        } else {
            String[] EContactData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < EContactData.length; i++) {
                String[] eContactData = EContactData[i].split(", ");

                DoctorFirstNameLabel.setStyle("-fx-text-fill:WHITE");
                DoctorLastNameLabel.setStyle("-fx-text-fill:WHITE");
                DoctorFirstNameLabel.setText(eContactData[0]);
                DoctorLastNameLabel.setText(eContactData[1]);
                DoctorProfessionLabel.setText(eContactData[2]);
                examinationNameLabel.setText(eContactData[3]);

                String dateTimeString = eContactData[4];
                dateTime = LocalDateTime.parse(dateTimeString, formatter);
                String date = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String hour = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                ContactDateLabel.setText("E-Contact date: " + date);
                ContactHourLabel.setText("E-Contact hour: " + hour);

                if (eContactData[5].equals("null")) {
                    linkLabel.setText("DOCTOR DON'T SEND ANY LINK YET.");
                } else {
                    linkLabel.setText(eContactData[5]);
                }


            }
        }
    }

    @FXML
    public void RefreshButtonClicked(ActionEvent actionEvent) throws IOException {
        SendEContactInformation();
    }

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @FXML
    public void testScheduleButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/ExaminationScheduleScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EContactController.ReadFromServer = Client.ReadFromServer;
        EContactController.SendToServer = Client.SendToServer;

        try {
            SendEContactInformation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
