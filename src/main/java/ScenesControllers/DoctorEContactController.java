package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
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

public class DoctorEContactController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    public GridPane gridPane;

    @FXML
    public Label PatientFirstNameLabel;

    @FXML
    public Label PatientLastNameLabel;

    @FXML
    public Label ExaminationNameLabel;

    @FXML
    public Label ContactDateLabel;

    @FXML
    public Label ContactHourLabel;

    @FXML
    public Button sendButton;

    @FXML
    public TextField linkTextField;

    @FXML
    private Button doctorPanelButton;

    @FXML
    private Pane copyButton;

    @FXML
    private Label correctLabel;

    private Pane selectedExamination = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime dateTime;

    private String Examination_nr = "";

    @FXML
    private void doctorPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    @FXML
    public void examinationScheduleButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorExaminationScheduleScene.fxml");
    }

    @FXML
    public void sendButtonClicked(ActionEvent actionEvent) throws IOException {
        message.addLinkToExamination(SendToServer, Client.clientId + "#/#" + Examination_nr + "#/#" + linkTextField.getText());
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

        if (serverAnswer.equals("Examination link added correctly.")) {
            correctLabel.setText("LINK SEND CORRECTLY!");

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(1500), TimeEvent -> {
                            correctLabel.setText("");
                    }));
            timeline.setCycleCount(1);
            timeline.play();
        }   else {
            correctLabel.setTextFill(Paint.valueOf("RED"));
            correctLabel.setText("Something went wrong!");
        }
    }

    private void getExaminationsForTodayFromDB() throws IOException {
        message.sendGetExaminationsForTodayMessage(SendToServer, Client.clientId  + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if(serverAnswer.equals("[[No examinations in database]]")) {
            Pane newExamination = new Pane();
            Label newExaminationName = new Label("There is no examinations for Today.");
            newExaminationName.setPrefHeight(30);
            newExaminationName.setPrefWidth(500);
            newExaminationName.setLayoutX(0);
            newExaminationName.setAlignment(Pos.CENTER);
            newExaminationName.setFont(new Font("Consolas Bold", 20.0));
            newExamination.getChildren().add(newExaminationName);
            gridPane.add(newExamination, 0, 0);
        } else {
            String[] examinationsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < examinationsData.length; i++) {
                String[] examinationData = examinationsData[i].split(", ");

                Pane newExamination = new Pane();
                Label newExaminationName = new Label(examinationData[1]);
                Label newExaminationDescription = new Label("Examination number: " + examinationData[0] + ", with " + examinationData[3] + " " + examinationData[4] + ". Phone: " + examinationData[5]);
                Label newExaminationDate = new Label(examinationData[2]);
                newExaminationName.setPrefHeight(30);
                // Set fitting to scroll bar
                if(examinationsData.length > 6) {
                    newExaminationName.setPrefWidth(450);
                    newExaminationDescription.setPrefWidth(450);
                } else {
                    newExaminationName.setPrefWidth(480);
                    newExaminationDescription.setPrefWidth(480);
                }

                setColors(newExamination);

                newExamination.setOnMouseClicked(event -> {
                    String text = newExaminationDescription.getText();

                    Examination_nr = text.split(":")[1].split("#/#")[0].trim();

                    String patientInfo = text.split("#/#")[1].trim();
                    String patientFirstName = patientInfo.split(" ")[1];
                    String patientLastName = patientInfo.split(" ")[2];

                    String dateTimeString = newExaminationDate.getText();
                    dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    String date = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String hour = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                    PatientFirstNameLabel.setText(patientFirstName);
                    PatientLastNameLabel.setText(patientLastName);
                    ExaminationNameLabel.setText(newExaminationName.getText());
                    ContactDateLabel.setText("E-Contact date: " + date);
                    ContactHourLabel.setText("E-Contact hour: " + hour);
                    sendButton.setDisable(false);

                });

                newExaminationName.setLayoutX(14);
                newExaminationName.setFont(new Font("Consolas Bold", 18.0));
                newExaminationDescription.setPrefHeight(60);
                newExaminationDescription.setLayoutX(14);
                newExaminationDescription.setPadding(new Insets(40, 0, 0, 0));
                newExaminationDescription.setWrapText(true); // Text wrapping
                newExaminationDescription.setFont(new Font("Consolas", 14.0));
                newExaminationDate.setPrefHeight(40);
                newExaminationDate.setPrefWidth(100);
                newExaminationDate.setStyle("-fx-wrap-text: true;");
                newExaminationDate.setLayoutX(420);
                newExaminationDate.setLayoutY(0);
                newExaminationDate.setFont(new Font("Consolas Bold", 12.0));
                newExamination.getChildren().addAll(newExaminationName, newExaminationDescription, newExaminationDate);

                // Add new examinations to the GridPane on the appropriate row
                gridPane.add(newExamination, 0, i);
            }
        }
    }

    void setColors(Pane newExamination) {
        newExamination.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");

        newExamination.setOnMouseEntered(mouseEvent -> {
            if (newExamination != selectedExamination) {
                newExamination.setStyle("-fx-background-color: #e6e6e6; -fx-border-radius: 10; -fx-border-color: #edae55; -fx-border-width: 4");
            }
        });

        newExamination.setOnMouseExited(mouseEvent -> {
            if (newExamination != selectedExamination) {
                newExamination.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }
        });

        newExamination.setOnMousePressed(mouseEvent -> {
            if (selectedExamination != null) {

                selectedExamination.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }


            selectedExamination = newExamination;


            newExamination.setStyle("-fx-background-color: #f2f2f2; -fx-border-radius: 10; -fx-border-color: #00FF00; -fx-border-width: 4");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorEContactController.ReadFromServer = Client.ReadFromServer;
        DoctorEContactController.SendToServer = Client.SendToServer;

        try {
            getExaminationsForTodayFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PatientFirstNameLabel.setText("NO EXAMINATION SELECTED");
        PatientLastNameLabel.setText("");
        ExaminationNameLabel.setText("");
        ContactDateLabel.setText("");
        ContactHourLabel.setText("");
        sendButton.setDisable(true);

    }


}
