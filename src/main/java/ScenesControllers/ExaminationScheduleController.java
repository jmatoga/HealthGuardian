package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ExaminationScheduleController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;
    public DatePicker appointmentDatePicker;
    public ChoiceBox appointmentDoctorChoiceBox;
    public ChoiceBox appointmentHourChoiceBox;
    public TextArea appointmentDescriptionTextField;
    public CheckBox appointmentEContactCheckBox;
    public Label appointmentSatusLabel;
    public Button appointmentConfirmButton;

    @FXML
    private Button userPanelButton;

    @FXML
    private GridPane gridPane;

    private String[] examDoctor = new String[3];

    private boolean flag = false;


    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExaminationScheduleController.ReadFromServer = Client.ReadFromServer;
        ExaminationScheduleController.SendToServer = Client.SendToServer;

        appointmentDoctorChoiceBox.setDisable(true);
        appointmentHourChoiceBox.setDisable(true);
        appointmentSatusLabel.setAlignment(Pos.CENTER);

        appointmentDescriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            appointmentConfirmButton.setDisable(newValue.length() > 100);
        });

        try {
            getExaminationsFromDB();
            getDoctorsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getExaminationsFromDB() throws IOException {
        message.sendGetExaminationsMessage(SendToServer, Client.clientId + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No examinations in database]]")) {
            Pane newExamination = new Pane();
            newExamination.setId("NoExam");
            Label newExaminationName = new Label("There is no examinations.");
            newExaminationName.setPrefHeight(120);
            newExaminationName.setPrefWidth(704);
            newExaminationName.setLayoutX(14);
            newExaminationName.setAlignment(Pos.CENTER);
            newExaminationName.setFont(new Font("Consolas Bold", 50.0));
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
                newExaminationName.setPrefHeight(40);
                // Set fitting to scroll bar

                if(examinationsData.length > 6) {
                    newExamination.setPrefWidth(705);
                    newExaminationName.setPrefWidth(705);
                    newExaminationDescription.setPrefWidth(705);
                } else {
                    newExamination.setPrefWidth(718);
                    newExaminationName.setPrefWidth(718);
                    newExaminationDescription.setPrefWidth(718);
                }
                newExaminationName.setLayoutX(14);
                newExaminationName.setFont(new Font("Consolas Bold", 36.0));
                newExaminationDescription.setPrefHeight(120);
                newExaminationDescription.setLayoutX(14);
                newExaminationDescription.setPadding(new Insets(40, 0, 0, 0));
                newExaminationDescription.setWrapText(true); // Text wrapping
                newExaminationDescription.setFont(new Font("Consolas", 28.0));
                newExaminationDate.setPrefHeight(50);
                newExaminationDate.setPrefWidth(200);
                //newExaminationDate.setTextAlignment(TextAlignment.CENTER);
                newExaminationDate.setWrapText(true);
                ;
                newExaminationDate.setLayoutX(590);
                newExaminationDate.setLayoutY(0);
                newExaminationDate.setFont(new Font("Consolas Bold", 20.0));
                newExamination.getChildren().addAll(newExaminationName, newExaminationDescription, newExaminationDate);

                // Add new examinations to the GridPane on the appropriate row
                gridPane.add(newExamination, 0, i);
            }
            gridPane.requestLayout();
        }
    }

    @FXML
    public void appointmentConfirmButtonClicked(ActionEvent actionEvent) throws IOException {
        if (appointmentDatePicker.getValue() == null || appointmentDoctorChoiceBox.getValue() == null || appointmentHourChoiceBox.getValue() == null) {
            appointmentSatusLabel.setTextFill(Color.redGradient());
            appointmentSatusLabel.setText("Fill out all required details!");
        } else {
            appointmentSatusLabel.setText("");

            ButtonType okButtonType = new ButtonType("YES", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", okButtonType, cancelButtonType);
            alert.setTitle("CONFIRMATION");
            alert.setHeaderText("Are you sure you want to make the following appointment?");
            String name = appointmentNameMaker();
            String date = appointmentDatePicker.getValue().toString() + " " + appointmentHourChoiceBox.getValue().toString() + ":00";

            alert.setContentText(name.toUpperCase() + "\nwith dr. " + examDoctor[0] + " at " + appointmentHourChoiceBox.getValue().toString() + " on " + appointmentDatePicker.getValue().toString() + ". \n\n" + "DECRIPTION FOR EXAMINATION: \n" + appointmentDescriptionTextField.getText());

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType) {
                    if (appointmentDatePicker.getValue() != null && appointmentDoctorChoiceBox.getValue() != null && appointmentHourChoiceBox.getValue() != null) {

                        message.sendMakeNewExamination(SendToServer, Client.clientId + "#/#" + name + "#/#" + date + "#/#" + appointmentDescriptionTextField.getText() + "#/#" + examDoctor[2] + "#/#" + Client.user_id);

                        try {
                            String serverAnswer = Client.getServerResponse(ReadFromServer);

                            if (serverAnswer.startsWith("Examination added correctly with nr: ")) {
                                appointmentSatusLabel.setTextFill(Color.greenGradient());
                                appointmentSatusLabel.setText("The date has been successfully booked.");
                            } else {
                                appointmentSatusLabel.setTextFill(Color.redGradient());
                                appointmentSatusLabel.setText(serverAnswer + " Try later.");
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

                alert.showAndWait();
                resetAllApointments();
                resetStatusLabel();
        }
    }

    private void resetStatusLabel() throws IOException {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(2500), TimeEvent -> {
                    appointmentSatusLabel.setText("");
                }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void resetAllApointments() throws IOException {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(0.5)
        );

        gridPane.getChildren().removeIf(node -> "NoExam".equals(node.getId()));

        visiblePause.setOnFinished(event -> {
            try {
                getExaminationsFromDB();
            } catch (IOException e) {
                e.printStackTrace(); // Możesz dostosować sposób obsługi błędów
            }

            appointmentDatePicker.setValue(null);
            appointmentDoctorChoiceBox.getSelectionModel().clearSelection();
            appointmentHourChoiceBox.getSelectionModel().clearSelection();
            appointmentDescriptionTextField.setText("");
            appointmentDescriptionTextField.setPromptText("Short description to examination (e.g., SMI CODE)");
            appointmentEContactCheckBox.setSelected(false);
            appointmentDoctorChoiceBox.setDisable(true);
            appointmentHourChoiceBox.setDisable(true);
            flag = false;
        });
        visiblePause.play();
    }

    @FXML
    public void appointmentDatePickerChosen(ActionEvent actionEvent) {

        if (appointmentDatePicker.getValue() != null && appointmentDatePicker.getValue().isBefore(LocalDate.now())) {
            appointmentSatusLabel.setTextFill(Color.redGradient());
            appointmentSatusLabel.setText("Choose right date!");
            appointmentDoctorChoiceBox.setDisable(true);
            appointmentHourChoiceBox.setDisable(true);
        } else {
            appointmentSatusLabel.setText("");
            appointmentDoctorChoiceBox.setDisable(false);
        }


    }

    public void getDoctorsFromDB() throws IOException {

        message.sendGetDoctorsToExams(SendToServer, Client.clientId + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] doctorsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");
        String[] doctors = new String[doctorsData.length];

        for (int i = 0; i < doctorsData.length; i++) {
            String[] doctorData = doctorsData[i].split(", ");

            doctors[i] = doctorData[2] + " " + doctorData[1] + " - " + doctorData[3].toUpperCase() + " - " + doctorData[0];

        }

        ObservableList<String> doctorsList = FXCollections.observableArrayList(doctors);

        appointmentDoctorChoiceBox.setItems(doctorsList);
    }

    public void getHoursFromDB() throws IOException {

        ArrayList<String> hourArr = new ArrayList<>(Arrays.asList(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00"
        ));

        String[] doctorId = appointmentDoctorChoiceBox.getValue().toString().split(" - ");
        examDoctor = appointmentDoctorChoiceBox.getValue().toString().split(" - ");

        message.sendGetHoursToExams(SendToServer, Client.clientId + "#/#" + doctorId[2] + "#/#" + appointmentDatePicker.getValue().toString());
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] busyHours = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

        hourArr.removeIf(busyHoursList -> Arrays.asList(busyHours).contains(busyHoursList));

        ObservableList<String> hourObservableList = FXCollections.observableArrayList(hourArr);
        appointmentHourChoiceBox.setItems(hourObservableList);

    }

    @FXML
    public void appointmentDoctorPickerChosen(ActionEvent actionEvent) throws IOException {

        if(!flag) {
            getHoursFromDB();
            appointmentHourChoiceBox.setDisable(false);
            flag = true;
        }
    }

    private String appointmentNameMaker() {

        String name = "";
        if (appointmentEContactCheckBox.isSelected()) {
            name = capitalizeFirstLetter(examDoctor[1]) + " E-Contact";
        } else {
            name = capitalizeFirstLetter(examDoctor[1]) + " Consultation";
        }

        return name;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String firstLetter = input.substring(0, 1).toUpperCase();

        String restOfText = input.substring(1).toLowerCase();

        return firstLetter + restOfText;
    }


}
