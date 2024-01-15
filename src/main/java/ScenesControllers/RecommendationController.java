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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class RecommendationController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label doctorNameRecommendationLabel, dateRecommendationLabel, dietLabel, medicinesLabel, additionalInformationLabel, nextMedicalExaminationLabel, nextMedicalExaminationDateLabel, recommendationIdLabel, nextMedicalExaminationDateTextLabel;

    private Pane selectedRecommendation = null;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RecommendationController.ReadFromServer = Client.ReadFromServer;
        RecommendationController.SendToServer = Client.SendToServer;

        try {
            getRecommendationsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void setColors(Pane newRecommendation) {
        newRecommendation.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");

        newRecommendation.setOnMouseEntered(mouseEvent -> {
            if (newRecommendation != selectedRecommendation) {
                newRecommendation.setStyle("-fx-background-color: #e6e6e6; -fx-border-radius: 10; -fx-border-color: #edae55; -fx-border-width: 4");
            }
        });

        newRecommendation.setOnMouseExited(mouseEvent -> {
            if (newRecommendation != selectedRecommendation) {
                newRecommendation.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }
        });

        newRecommendation.setOnMousePressed(mouseEvent -> {
            if (selectedRecommendation != null) {
                // Przywróć domyślny styl dla poprzednio zaznaczonego panelu
                selectedRecommendation.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }

            selectedRecommendation = newRecommendation;

            newRecommendation.setStyle("-fx-background-color: #f2f2f2; -fx-border-radius: 10; -fx-border-color: #00FF00; -fx-border-width: 4");
        });
    }

    private void getRecommendationsFromDB() throws IOException {
        message.sendGetRecommendationMessage(SendToServer, Client.clientId + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No recommendations in database]]")) {
            Pane newRecommendation = new Pane();
            Label newRecommendationTitle = new Label("There is no recommendations.");
            newRecommendationTitle.setPrefHeight(200);
            newRecommendationTitle.setPrefWidth(816);
            newRecommendation.setMinWidth(816);
            newRecommendationTitle.setAlignment(Pos.CENTER);
            newRecommendationTitle.setFont(new Font("Consolas Bold", 35.0));
            newRecommendation.getChildren().add(newRecommendationTitle);
            gridPane.add(newRecommendation, 0, 0);
        } else {
            String[] recommendationsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");
            for (int i = 0; i < recommendationsData.length; i++) {
                String[] recommendationData = recommendationsData[i].split(", ");

                VBox newRecommendation = new VBox();
                newRecommendation.setPrefHeight(200);
                newRecommendation.setId("recommendation" + (i+1));
                newRecommendation.setOnMouseClicked(mouseEvent -> {
                    doctorNameRecommendationLabel.setText("From dr. " + recommendationData[9] + " " + recommendationData[10]);
                    dateRecommendationLabel.setText(recommendationData[6]);

                    if(recommendationData[1].equals("null")) {
                        dietLabel.setFont(new Font("Consolas Bold", 20.0));
                        dietLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        dietLabel.setAlignment(Pos.CENTER);
                        dietLabel.setText("No diet recommendation.");
                    } else {
                        dietLabel.setFont(new Font("Consolas", 20.0));
                        dietLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
                        dietLabel.setAlignment(Pos.TOP_LEFT);
                        dietLabel.setText(recommendationData[1]);
                    }

                    if(recommendationData[2].equals("null")) {
                        medicinesLabel.setFont(new Font("Consolas Bold", 20.0));
                        medicinesLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        medicinesLabel.setAlignment(Pos.CENTER);
                        medicinesLabel.setText("No medicines recommendation.");
                    } else {
                        medicinesLabel.setFont(new Font("Consolas", 20.0));
                        medicinesLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
                        medicinesLabel.setAlignment(Pos.TOP_LEFT);
                        medicinesLabel.setText(recommendationData[2]);
                    }

                    if(recommendationData[5].equals("null")) {
                        additionalInformationLabel.setFont(new Font("Consolas Bold", 30.0));
                        additionalInformationLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        additionalInformationLabel.setAlignment(Pos.CENTER);
                        additionalInformationLabel.setText("No additional information.");
                    } else {
                        additionalInformationLabel.setFont(new Font("Consolas", 20.0));
                        additionalInformationLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
                        additionalInformationLabel.setAlignment(Pos.TOP_LEFT);
                        additionalInformationLabel.setText(recommendationData[5]);
                    }

                    if(recommendationData[4].equals("null")) {
                        nextMedicalExaminationLabel.setFont(new Font("Consolas Bold", 20.0));
                        nextMedicalExaminationLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        nextMedicalExaminationLabel.setAlignment(Pos.CENTER);
                        nextMedicalExaminationLabel.setWrapText(true);
                        nextMedicalExaminationLabel.setText("No next medical examination recommended.");
                        nextMedicalExaminationDateLabel.setVisible(false);
                        nextMedicalExaminationDateTextLabel.setVisible(false);
                    } else {
                        nextMedicalExaminationDateLabel.setVisible(true);
                        nextMedicalExaminationDateTextLabel.setVisible(true);
                        nextMedicalExaminationDateLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        nextMedicalExaminationDateLabel.setAlignment(Pos.CENTER);
                        nextMedicalExaminationLabel.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                        nextMedicalExaminationLabel.setAlignment(Pos.CENTER);
                        nextMedicalExaminationLabel.setText(recommendationData[4]);
                        nextMedicalExaminationDateLabel.setText(recommendationData[3]);
                    }

                    recommendationIdLabel.setText("Recommendation ID: " + recommendationData[0]);
                });

                // Set fitting to scroll bar
                if(recommendationsData.length > 9) {
                    newRecommendation.setPrefWidth(267);
                    newRecommendation.setMinWidth(267);
                    newRecommendation.setMaxWidth(267);
                } else {
                    newRecommendation.setPrefWidth(272);
                    newRecommendation.setMinWidth(272);
                    newRecommendation.setMaxWidth(272);
                }

                setColors(newRecommendation);

                Label newRecommendationTitle = new Label("Recommendation " + (i+1));
                Label newRecommendationNextMedicalExam = new Label("Next medical examination:\n" + recommendationData[4]);
                Label newRecommendationNextMedicalExamDate = new Label("on: " + recommendationData[3]);
                Label newRecommendationDate = new Label("Date: " + recommendationData[6]);
                Label newRecommendationDoctor = new Label("dr. " + recommendationData[9] + " " + recommendationData[10]);

                newRecommendationTitle.setAlignment(Pos.CENTER);
                newRecommendationTitle.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                newRecommendationTitle.setPrefHeight(50);
                newRecommendationTitle.setPrefWidth(268);
                newRecommendationTitle.setFont(new Font("Consolas Bold", 20.0));
                newRecommendationTitle.setWrapText(true); // Text wrapping

                newRecommendationNextMedicalExam.setPrefHeight(44);
                newRecommendationNextMedicalExam.setWrapText(true);
                newRecommendationNextMedicalExam.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                newRecommendationNextMedicalExam.setPadding(new Insets(0, 0, 0, 10));
                newRecommendationNextMedicalExam.setFont(new Font("Consolas", 16.0));

                newRecommendationNextMedicalExamDate.setMinHeight(24);
                newRecommendationNextMedicalExamDate.setPadding(new Insets(0, 0, 0, 10));
                newRecommendationNextMedicalExamDate.setFont(new Font("Consolas Bold", 13.0));

                newRecommendationDate.setPrefWidth(268);
                newRecommendationDate.setAlignment(Pos.CENTER);
                newRecommendationDate.setPrefHeight(20);
                newRecommendationDate.setPadding(new Insets(10, 0, 0, 0));
                newRecommendationDate.setFont(new Font("Consolas", 18.0));

                newRecommendationDoctor.setPrefWidth(268);
                newRecommendationDoctor.setAlignment(Pos.CENTER);
                newRecommendationDoctor.setPrefHeight(26);
                newRecommendationDoctor.setPadding(new Insets(10, 0, 0, 0));
                newRecommendationDoctor.setFont(new Font("Consolas Italic", 18.0));

                if(recommendationData[4].equals("null"))
                    newRecommendation.getChildren().addAll(newRecommendationTitle, newRecommendationDate, newRecommendationDoctor);
                else
                    newRecommendation.getChildren().addAll(newRecommendationTitle, newRecommendationNextMedicalExam, newRecommendationNextMedicalExamDate, newRecommendationDate, newRecommendationDoctor);

                // Add new Recommendation to the GridPane on the appropriate row and column
                gridPane.add(newRecommendation, i % 3, i / 3);
            }
        }
    }
}
