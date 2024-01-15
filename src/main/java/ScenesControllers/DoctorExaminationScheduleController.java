package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorExaminationScheduleController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label descriptionShowLabel;

    private Pane selectedExamination = null;

    @FXML
    private void docotrPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorExaminationScheduleController.ReadFromServer = Client.ReadFromServer;
        DoctorExaminationScheduleController.SendToServer = Client.SendToServer;

        try {
            getExaminationsDoctorFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getExaminationsDoctorFromDB() throws IOException {
        message.sendGetExaminationsDoctorMessage(SendToServer, Client.clientId  + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if(serverAnswer.equals("[[No examinations in database]]")) {
            Pane newExamination = new Pane();
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
                Label newExaminationDescription = new Label("Examination number: " + examinationData[0] + ", with " + examinationData[4] + " " + examinationData[5] + ". Phone: " + examinationData[6]);
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

                newExamination.setOnMouseClicked(mouseEvent -> {
                    if(examinationData[3].equals("null"))
                        descriptionShowLabel.setText("No description in this examination.");
                    else
                        descriptionShowLabel.setText(examinationData[3]);
                });

                setColors(newExamination);

                newExaminationName.setLayoutX(14);
                newExaminationName.setFont(new Font("Consolas Bold", 36.0));
                newExaminationDescription.setPrefHeight(120);
                newExaminationDescription.setLayoutX(14);
                newExaminationDescription.setPadding(new Insets(40, 0, 0, 0));
                newExaminationDescription.setWrapText(true); // Text wrapping
                newExaminationDescription.setFont(new Font("Consolas", 28.0));
                newExaminationDate.setPrefHeight(50);
                newExaminationDate.setPrefWidth(200);
                newExaminationDate.setStyle("-fx-wrap-text: true;");
                newExaminationDate.setLayoutX(590);
                newExaminationDate.setLayoutY(0);
                newExaminationDate.setFont(new Font("Consolas Bold", 20.0));
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
                // Przywróć domyślny styl dla poprzednio zaznaczonego panelu
                selectedExamination.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }

            selectedExamination = newExamination;

            newExamination.setStyle("-fx-background-color: #f2f2f2; -fx-border-radius: 10; -fx-border-color: #00FF00; -fx-border-width: 4");
        });
    }
}
