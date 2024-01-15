package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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

public class DoctorUserController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    public Pane smiPanel;

    @FXML
    public TextField smiCodeTextField;

    @FXML
    public Label smiStatusLabel, smiMainFieldOfCaseLabel, smiMedicinesLabel, smiWhenThePainSatrtLabel, smiAdditionalInformationLabel, smiDateLabel, smiRecommendationIdLabel, smiExtentOfThePainLabel, smiTemperatureLabel, dateLabel, peselStatusLabel, firstNameLabel, lastNameLabel, birthDateLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField peselTextField;

    @FXML
    private ScrollPane scrollPane, scrollPane1, scrollPane2;

    @FXML
    private GridPane gridPane, gridPane1, gridPane2;

    @FXML
    private Pane addPane, smiCodePane, smiPanelPane;

    @FXML
    public void doctorPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    @FXML
    public void smiCodeEntered(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER && checkPesel()) {
            if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                if (smiCodeTextField.getText().length() == 4 && smiCodeTextField.getText().matches("\\d+")) {
                    smiStatusLabel.setText("");
                    getSmiFromDB();
                } else {
                    smiPanelPane.setVisible(false);
                    smiStatusLabel.setText("Wrong smi code, should contains 4 digits!");
                }
            } else {
                smiPanelPane.setVisible(false);
                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                peselStatusLabel.setText("First enter pesel!");
            }
        }
    }

    private void getSmiFromDB() throws IOException {
        message.sendGetSMIMessage(SendToServer, Client.clientId + "#/#" + smiCodeTextField.getText() + "#/#" + peselTextField.getText());
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[SMI with this code doesn't exist in this user!]")) {
            smiPanelPane.setVisible(false);
            smiStatusLabel.setText("SMI with this code doesn't exist in this user!");
        } else {
            smiStatusLabel.setText("");
            setAllVisibleFalse();
            smiPanelPane.setVisible(true);

            String[] smiData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

            smiMainFieldOfCaseLabel.setFont(new Font("Consolas", 20.0));
            smiMainFieldOfCaseLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
            smiMainFieldOfCaseLabel.setAlignment(Pos.TOP_LEFT);
            smiMainFieldOfCaseLabel.setText(smiData[1]);
            if (!smiData[2].equals("null"))
                smiMainFieldOfCaseLabel.setText(smiMainFieldOfCaseLabel.getText() + "\n- " + smiData[2]);

            if (!smiData[3].equals("null"))
                smiMainFieldOfCaseLabel.setText(smiMainFieldOfCaseLabel.getText() + "\n- " + smiData[3]);

            if (!smiData[4].equals("null"))
                smiMainFieldOfCaseLabel.setText(smiMainFieldOfCaseLabel.getText() + "\n- " + smiData[4]);

            smiMedicinesLabel.setFont(new Font("Consolas", 20.0));
            smiMedicinesLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
            smiMedicinesLabel.setAlignment(Pos.TOP_LEFT);
            smiMedicinesLabel.setText(smiData[5]);

            smiExtentOfThePainLabel.setFont(new Font("Consolas", 20.0));
            smiExtentOfThePainLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
            smiExtentOfThePainLabel.setAlignment(Pos.TOP_LEFT);
            smiExtentOfThePainLabel.setText(smiData[6]);

            smiWhenThePainSatrtLabel.setFont(new Font("Consolas", 20.0));
            smiWhenThePainSatrtLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
            smiWhenThePainSatrtLabel.setAlignment(Pos.TOP_LEFT);
            smiWhenThePainSatrtLabel.setText(smiData[7]);

            smiTemperatureLabel.setFont(new Font("Consolas", 20.0));
            smiTemperatureLabel.setTextAlignment(TextAlignment.CENTER);
            smiTemperatureLabel.setAlignment(Pos.CENTER);
            smiTemperatureLabel.setText(smiData[8] + "°C");

            smiAdditionalInformationLabel.setFont(new Font("Consolas", 20.0));
            smiAdditionalInformationLabel.setTextAlignment(TextAlignment.LEFT); // to set text to center after text wrapping
            smiAdditionalInformationLabel.setAlignment(Pos.TOP_LEFT);
            smiAdditionalInformationLabel.setText(smiData[9]);

            smiDateLabel.setFont(new Font("Consolas", 20.0));
            smiDateLabel.setTextAlignment(TextAlignment.LEFT);
            smiDateLabel.setAlignment(Pos.CENTER_LEFT);
            smiDateLabel.setText(smiData[11]);
        }
    }

    private Alert createMedicalHistoryAlert(GridPane grid, TextArea inputFieldMedicalCase, ButtonType
                                                                                                   okButtonType, String labelName, TextField inputFieldICD10Code, TextField inputFieldICD10FirstLetter) {
        grid.add(new Label("ICD10:"), 0, 3);
        inputFieldICD10FirstLetter.setMaxWidth(45);
        inputFieldICD10FirstLetter.setAlignment(Pos.TOP_LEFT);
        inputFieldICD10FirstLetter.setPromptText("Eg. A");
        inputFieldICD10FirstLetter.setTranslateX(50);
        grid.add(inputFieldICD10FirstLetter, 0, 3);

        inputFieldICD10Code.setMaxWidth(50);
        inputFieldICD10Code.setAlignment(Pos.TOP_LEFT);
        inputFieldICD10Code.setPromptText("Eg. 10");
        inputFieldICD10Code.setTranslateX(105);
        grid.add(inputFieldICD10Code, 0, 3);

        return createEditDataAlert(grid, inputFieldMedicalCase, okButtonType, labelName);
    }

    private Alert createDocumentationAlert(GridPane grid, TextArea inputFieldInterview, ButtonType okButtonType, String labelName, TextArea inputFieldPhysicalExamination, TextField inputFieldRecommendationId, TextArea inputFieldICD10) {
        grid.add(new Label("Physical examination description:"), 0, 3);
        inputFieldPhysicalExamination.setMinHeight(100);
        inputFieldPhysicalExamination.setMinWidth(400);
        inputFieldPhysicalExamination.setMaxHeight(100);
        inputFieldPhysicalExamination.setMaxWidth(400);
        //inputFieldPhysicalExamination.setAlignment(Pos.TOP_LEFT);
        inputFieldPhysicalExamination.setWrapText(true);
        inputFieldPhysicalExamination.setPromptText("Information about physical examination.");
        grid.add(inputFieldPhysicalExamination, 0, 4);

        grid.add(new Label("ICD10 codes:"), 0, 5);
        inputFieldICD10.setMinHeight(45);
        inputFieldICD10.setMinWidth(400);
        inputFieldICD10.setMaxHeight(45);
        inputFieldICD10.setMaxWidth(400);
        //inputFieldICD10.setAlignment(Pos.TOP_LEFT);
        inputFieldICD10.setWrapText(true);
        inputFieldICD10.setPromptText("Eg. M54.4 Cervicalgia");
        //inputFieldICD10.setTranslateX(50);
        grid.add(inputFieldICD10, 0, 6);


        grid.add(new Label("Recommendation id:"), 0, 7);
        inputFieldRecommendationId.setMaxWidth(50);
        inputFieldRecommendationId.setAlignment(Pos.TOP_LEFT);
        inputFieldRecommendationId.setPromptText("Eg. 1");
        inputFieldRecommendationId.setTranslateX(120);
        grid.add(inputFieldRecommendationId, 0, 7);

        return createEditDataAlert(grid, inputFieldInterview, okButtonType, labelName);
    }

    private void getMedicalHistoryFromDB() throws IOException {
        message.sendGetDoctorMedicalHistoryMessage(SendToServer, Client.clientId + "#/#" + peselTextField.getText());
        String serverAnswer = Client.getServerResponse(ReadFromServer);
        Button newAddButton = new Button("Add new");

        if (serverAnswer.equals("[[No medical history in database]]")) {
            Pane newMedicalHistory = new Pane();
            Label newMedicalHistoryTitle = new Label("There is no medical history.");
            newMedicalHistoryTitle.setPrefHeight(120);
            newMedicalHistoryTitle.setPrefWidth(934);
            newMedicalHistoryTitle.setLayoutX(14);
            newMedicalHistoryTitle.setAlignment(Pos.CENTER);
            newMedicalHistoryTitle.setFont(new Font("Consolas Bold", 50.0));
            newMedicalHistory.getChildren().add(newMedicalHistoryTitle);
            gridPane.add(newMedicalHistory, 0, 0);
        } else {
            String[] medicalHistoriesData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < medicalHistoriesData.length; i++) {
                String[] medicalHistoryData = medicalHistoriesData[i].split(", ");

                Pane newMedicalHistory = new Pane();
                Label newMedicalHistoryTitle = new Label(medicalHistoryData[1] + medicalHistoryData[2]);
                Label newMedicalHistoryContent = new Label(medicalHistoryData[3]);

                newMedicalHistoryTitle.setPrefHeight(40);
                newMedicalHistoryTitle.setPrefWidth(830);
                newMedicalHistoryContent.setPrefWidth(830);

                // Set fitting to scroll bar
                if (medicalHistoriesData.length > 6) {
                    newMedicalHistoryTitle.setPrefWidth(1321); //1334 1321
                    newMedicalHistoryContent.setPrefWidth(1321);
                } else {

                }

                newMedicalHistoryTitle.setLayoutX(14);
                newMedicalHistoryTitle.setFont(new Font("Consolas Bold", 36.0));
                newMedicalHistoryContent.setPrefHeight(120);
                newMedicalHistoryContent.setLayoutX(14);
                newMedicalHistoryContent.setPadding(new Insets(40, 0, 0, 0));
                newMedicalHistoryContent.setWrapText(true); // Text wrapping
                newMedicalHistoryContent.setFont(new Font("Consolas", 28.0));

                newMedicalHistory.getChildren().addAll(newMedicalHistoryTitle, newMedicalHistoryContent);

                Pane newDeleteButtonPane = new Pane();
                Button newDeleteButton = new Button("Delete");
                newDeleteButtonPane.setPrefWidth(104);
                newDeleteButton.setPrefHeight(40);
                newDeleteButton.setPrefWidth(80);
                newDeleteButton.setLayoutY(40);
                newDeleteButton.setLayoutX(12);
                newDeleteButton.setFocusTraversable(false);

                newDeleteButton.setOnMouseClicked(MouseEvent -> {
                    if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                        try {
                            message.sendDeleteMedicialHistory(SendToServer, Client.clientId + "#/#" + medicalHistoryData[0]);
                            String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                            if (serverAnswer1.equals("Medical history deleted correctly.")) {
                                peselStatusLabel.setTextFill(Color.greenGradient());
                                peselStatusLabel.setText("Medical history deleted correctly!");
                                setAllVisibleFalse();
                                addPane.getChildren().remove(newAddButton);
                                gridPane.getChildren().remove(newMedicalHistory);
                                gridPane.getChildren().remove(newDeleteButtonPane);
                                gridPane.requestLayout(); // call layoutChildren() on the GridPane
                            } else {
                                peselStatusLabel.setTextFill(Color.redGradient());
                                peselStatusLabel.setText("Error while deleting medical history!");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                    peselStatusLabel.setText("");
                                    peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                                }));
                        timeline.setCycleCount(1);
                        timeline.play();
                    } else {
                        peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                        peselStatusLabel.setText("First enter pesel!");
                    }
                });

                newDeleteButtonPane.getChildren().add(newDeleteButton);

                // Add new notification to the GridPane on the appropriate row
                gridPane.add(newMedicalHistory, 0, i);
                gridPane.add(newDeleteButtonPane, 1, i);
            }
            gridPane.requestLayout();
        }

        addPane.setVisible(true);
        newAddButton.setPrefHeight(40);
        newAddButton.setPrefWidth(80);
        addPane.setStyle("-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: transparent; -fx-background-radius: 2;");
        addPane.setPrefWidth(106);
        addPane.setMinHeight(62);
        newAddButton.setLayoutY(10);
        newAddButton.setLayoutX(13);
        newAddButton.setFocusTraversable(false);

        newAddButton.setOnMouseClicked(MouseEvent -> {
            if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                GridPane grid = new GridPane();
                TextArea inputFieldMedicalCase = new TextArea();
                inputFieldMedicalCase.setPromptText("Eg. Asthma");
                TextField inputFieldICD10FirstLetter = new TextField();
                TextField inputFieldICD10Code = new TextField();
                Label label = new Label("");
                label.setTextFill(Paint.valueOf("#ff0000"));

                ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                Alert alert = createMedicalHistoryAlert(grid, inputFieldMedicalCase, okButtonType, "Medical case:", inputFieldICD10Code, inputFieldICD10FirstLetter);
                alert.setTitle("Add new medical history:");
                alert.setHeaderText("Write below properties of medical history.");
                label.setMinWidth(100);
                label.setMinHeight(30);
                label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

                VBox vBox = new VBox();
                vBox.getChildren().addAll(label, grid);
                vBox.setAlignment(Pos.CENTER);
                alert.getDialogPane().setContent(vBox);

                alert.setOnCloseRequest(alertEvent -> {
                    if (alert.getResult() == okButtonType && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                        if (!inputFieldMedicalCase.getText().isEmpty() && !inputFieldICD10FirstLetter.getText().isEmpty() && !inputFieldICD10Code.getText().isEmpty()) {
                            message.addMedicalHistory(SendToServer, Client.clientId + "#/#" + inputFieldMedicalCase.getText() + "#/#" + inputFieldICD10FirstLetter.getText() + "#/#" + inputFieldICD10Code.getText() + "#/#" + peselTextField.getText());

                            try {
                                String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                                if (serverAnswer1.equals("Medical history added correctly.")) {
                                    peselStatusLabel.setTextFill(Color.greenGradient());
                                    peselStatusLabel.setText("Medical history added correctly!");
                                    setAllVisibleFalse();
                                    addPane.getChildren().remove(newAddButton);
                                } else {
                                    peselStatusLabel.setTextFill(Color.redGradient());
                                    peselStatusLabel.setText("Error while adding medical history!");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                        peselStatusLabel.setText("");
                                        peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                                    }));
                            timeline.setCycleCount(1);
                            timeline.play();
                        } else if (inputFieldICD10FirstLetter.getText().length() != 1 || inputFieldICD10FirstLetter.getText().contains(" ") || !inputFieldICD10FirstLetter.getText().matches("^[A-Z]+$")) {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Wrong ICD10 letter!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        } else if ((inputFieldICD10Code.getText().length() != 2 || !inputFieldICD10Code.getText().matches("\\d+"))) {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Wrong ICD10 code!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        } else {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Fill all gaps!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        }
                    }
                });
                Platform.runLater(alert::showAndWait);
            } else {
                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                peselStatusLabel.setText("First enter pesel!");
            }
        });

        addPane.getChildren().add(newAddButton);
    }

    private void getFindingsFromDB() throws IOException {
        message.sendGetDoctorFindingsMessage(SendToServer, Client.clientId + "#/#" + peselTextField.getText());
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No findings in database]]")) {
            Pane newFinding = new Pane();
            Label newFindingTitle = new Label("There is no findings.");
            newFindingTitle.setPrefHeight(120);
            newFindingTitle.setPrefWidth(948);
            newFindingTitle.setAlignment(Pos.CENTER);
            newFindingTitle.setFont(new Font("Consolas Bold", 50.0));
            newFinding.getChildren().add(newFindingTitle);
            gridPane2.add(newFinding, 0, 0);
        } else {
            String[] findingsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < findingsData.length; i++) {
                String[] findingData = findingsData[i].split(", ");

                Pane newFinding = new Pane();
                Label newFindingTitle = new Label(findingData[2]);
                Label newFindingContent = new Label(findingData[4]);

                newFindingTitle.setPrefHeight(40);
                newFindingTitle.setPrefWidth(830);
                newFindingContent.setPrefWidth(830);

                // Set fitting to scroll bar
                if (findingsData.length > 6) {
                    newFindingTitle.setPrefWidth(1321); //1334 1321
                    newFindingContent.setPrefWidth(1321);
                } else {

                }

                newFindingTitle.setLayoutX(14);
                newFindingTitle.setFont(new Font("Consolas Bold", 36.0));
                newFindingContent.setPrefHeight(120);
                newFindingContent.setLayoutX(14);
                newFindingContent.setPadding(new Insets(40, 0, 0, 0));
                newFindingContent.setWrapText(true); // Text wrapping
                newFindingContent.setFont(new Font("Consolas", 28.0));

                newFinding.getChildren().addAll(newFindingTitle, newFindingContent);
                gridPane2.add(newFinding, 0, i);
            }
            gridPane2.requestLayout();
        }
    }

    @FXML
    public void changeMedicalHistoryButtonClicked(ActionEvent actionEvent) throws IOException {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            scrollPane.setVisible(true);
            getMedicalHistoryFromDB();
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void prescribeEPrescriptionButtonClicked(ActionEvent actionEvent) {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextArea inputFieldMedicines = new TextArea();
            inputFieldMedicines.setPromptText("Eg. 2x Metoprolol; 1x Warfarin");
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ;
            Alert alert = createEditDataAlert(grid, inputFieldMedicines, okButtonType, "Medicines:");
            alert.setTitle("Prescribe e-prescription:");
            alert.setHeaderText("Write below medicines to e-prescription.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !inputFieldMedicines.getText().isEmpty() && !peselTextField.getText().isEmpty()) {
                    message.prescribeEPrescription(SendToServer, Client.clientId + "#/#" + inputFieldMedicines.getText() + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.doctor_id + "#/#" + peselTextField.getText());

                    try {
                        String serverAnswer = Client.getServerResponse(ReadFromServer);

                        if (serverAnswer.equals("E-prescription prescribed correctly.")) {
                            peselStatusLabel.setTextFill(Color.greenGradient());
                            peselStatusLabel.setText("E-prescription prescribed!");
                        } else {
                            peselStatusLabel.setTextFill(Color.redGradient());
                            peselStatusLabel.setText("Error while prescribing!");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                peselStatusLabel.setText("");
                                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                            }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            });
            Platform.runLater(alert::showAndWait);
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    private Alert createEditDataAlert(GridPane grid, TextArea inputField, ButtonType okButtonType, String labelName) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 10, 0));
        grid.setMinWidth(420);
        grid.setMaxWidth(420);

        inputField.setMinHeight(100);
        inputField.setMinWidth(400);
        inputField.setMaxHeight(100);
        inputField.setMaxWidth(400);
        //inputField.setAlignment(Pos.TOP_LEFT);
        inputField.setWrapText(true);

        Label dateAlertLabel = new Label(LocalDate.now().toString());
        dateAlertLabel.setTranslateX(285);
        dateAlertLabel.setFont(new Font("Consolas", 20.0));
        grid.add(dateAlertLabel, 0, 0);

        grid.add(new Label(labelName), 0, 1);
        grid.add(inputField, 0, 2);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(okButtonType);

        alert.getDialogPane().setContent(grid);
        return alert;
    }

    @FXML
    public void prescribeEReferralButtonClicked(ActionEvent actionEvent) {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextArea inputFieldEReferralName = new TextArea();
            inputFieldEReferralName.setPromptText("Eg. Dermatology Consultation");
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ;
            Alert alert = createEditDataAlert(grid, inputFieldEReferralName, okButtonType, "E-referral name:");
            alert.setTitle("Prescribe e-referral:");
            alert.setHeaderText("Write below name of e-referral.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !inputFieldEReferralName.getText().isEmpty() && !peselTextField.getText().isEmpty()) {
                    message.prescribeEReferral(SendToServer, Client.clientId + "#/#" + inputFieldEReferralName.getText() + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.doctor_id + "#/#" + peselTextField.getText());

                    try {
                        String serverAnswer = Client.getServerResponse(ReadFromServer);

                        if (serverAnswer.equals("E-referral prescribed correctly.")) {
                            peselStatusLabel.setTextFill(Color.greenGradient());
                            peselStatusLabel.setText("E-referral prescribed!");
                        } else {
                            peselStatusLabel.setTextFill(Color.redGradient());
                            peselStatusLabel.setText("Error while prescribing!");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                peselStatusLabel.setText("");
                                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                            }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            });
            Platform.runLater(alert::showAndWait);
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void checkFindingsButtonClicked(ActionEvent actionEvent) throws IOException {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            scrollPane2.setVisible(true);
            getFindingsFromDB();
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    private Alert createRecommendationAlert(GridPane grid, TextArea inputFieldMedicines, ButtonType
                                                                                                 okButtonType, String labelName, DatePicker nextCheckUpDate, TextField inputFieldNextCheckUpName, TextArea
                                                                                                                                                                                                          inputFieldAdditionalInformation, TextArea inputFieldDiet) {
        grid.add(new Label("Medicines:"), 0, 3);
        inputFieldMedicines.setMinHeight(100);
        inputFieldMedicines.setMinWidth(400);
        inputFieldMedicines.setMaxHeight(100);
        inputFieldMedicines.setMaxWidth(400);
        //inputFieldMedicines.setAlignment(Pos.TOP_LEFT);
        inputFieldMedicines.setWrapText(true);
        inputFieldMedicines.setPromptText("Eg. 2x Metoprolol; 1x Warfarin");
        grid.add(inputFieldMedicines, 0, 4);

        grid.add(new Label("Additional information:"), 0, 5);
        inputFieldAdditionalInformation.setMinHeight(150);
        inputFieldAdditionalInformation.setMinWidth(400);
        inputFieldAdditionalInformation.setMaxHeight(150);
        inputFieldAdditionalInformation.setMaxWidth(400);
        //inputFieldAdditionalInformation.setAlignment(Pos.TOP_LEFT);
        inputFieldAdditionalInformation.setWrapText(true);
        inputFieldAdditionalInformation.setPromptText("Eg. Patient should avoid alcohol.");
        grid.add(inputFieldAdditionalInformation, 0, 6);

        grid.add(new Label("Next check up name:"), 0, 7);
        inputFieldNextCheckUpName.setPromptText("Eg. Dermatology Consultation");
        grid.add(inputFieldNextCheckUpName, 0, 8);

        grid.add(new Label("Next check up date:"), 0, 9);
        nextCheckUpDate.setMinWidth(100);
        nextCheckUpDate.setMaxWidth(100);
        nextCheckUpDate.setPromptText(LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonthValue() + "." + LocalDate.now().getYear());
        nextCheckUpDate.setTranslateX(110);
        grid.add(nextCheckUpDate, 0, 9);
        grid.add(new Pane(), 0, 11);

        return createEditDataAlert(grid, inputFieldDiet, okButtonType, labelName);
    }

    @FXML
    public void addRecommendationsButtonClicked(ActionEvent actionEvent) {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextArea inputFieldDiet = new TextArea();
            inputFieldDiet.setPromptText("Eg. Low-carb diet with increased protein intake.");
            TextArea inputFieldMedicines = new TextArea();
            TextField inputFieldNextCheckUpName = new TextField();
            TextArea inputFieldAdditionalInformation = new TextArea();
            DatePicker nextCheckUpDate = new DatePicker();

            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Alert alert = createRecommendationAlert(grid, inputFieldMedicines, okButtonType, "Diet:", nextCheckUpDate, inputFieldNextCheckUpName, inputFieldAdditionalInformation, inputFieldDiet);
            alert.setTitle("Add recommendation:");
            alert.setHeaderText("Write below recommendation.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !peselTextField.getText().isEmpty()) {
                    message.addRecommendation(SendToServer, Client.clientId + "#/#" + inputFieldDiet.getText() + "#/#" + inputFieldMedicines.getText() + "#/#" + nextCheckUpDate.getValue() + "#/#" + inputFieldNextCheckUpName.getText() + "#/#" + inputFieldAdditionalInformation.getText() + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.doctor_id + "#/#" + peselTextField.getText());

                    try {
                        String serverAnswer = Client.getServerResponse(ReadFromServer);

                        if (serverAnswer.equals("Recommendation added correctly.")) {
                            peselStatusLabel.setTextFill(Color.greenGradient());
                            peselStatusLabel.setText("Recommendation added correctly!");
                        } else {
                            peselStatusLabel.setTextFill(Color.redGradient());
                            peselStatusLabel.setText("Error while adding recommendation!");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                peselStatusLabel.setText("");
                                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                            }));
                    timeline.setCycleCount(1);
                    timeline.play();
                }
            });
            Platform.runLater(alert::showAndWait);
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    private void getDocumentationsFromDB() throws IOException {
        message.sendGetDoctorDocumentationsMessage(SendToServer, Client.clientId + "#/#" + peselTextField.getText());
        String serverAnswer = Client.getServerResponse(ReadFromServer);
        Button newAddButton = new Button("Add new");

        if (serverAnswer.equals("[[No documentations in database]]")) {
            Pane newDocumentation = new Pane();
            Label newDocumentationTitle = new Label("There is no documentations.");
            newDocumentationTitle.setPrefHeight(120);
            newDocumentationTitle.setPrefWidth(934);
            newDocumentationTitle.setLayoutX(14);
            newDocumentationTitle.setAlignment(Pos.CENTER);
            newDocumentationTitle.setFont(new Font("Consolas Bold", 50.0));
            newDocumentation.getChildren().add(newDocumentationTitle);
            gridPane1.add(newDocumentation, 0, 0);
        } else {
            String[] documentationsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < documentationsData.length; i++) {
                String[] documentationData = documentationsData[i].split(", ");

                Pane newDocumentation = new Pane();
                newDocumentation.setMaxHeight(600);
                newDocumentation.setMinHeight(320);
                Label newDocumentationTitle = new Label("Documentation id: " + documentationData[0]);
                if (documentationData[7].equals("null"))
                    newDocumentationTitle.setText("Documentation id: " + documentationData[0] + " (no recommendation)");
                else
                    newDocumentationTitle.setText("Documentation id: " + documentationData[0] + " (recommendation id: " + documentationData[7] + ")");

                Label newDocumentationContent = new Label();

                if (documentationData[4].equals("null"))
                    newDocumentationContent.setText("Interview description: \n" + documentationData[2] + "\nExamination description: \n" + documentationData[3]);
                else
                    newDocumentationContent.setText("ICD10: " + documentationData[4] + "\nInterview description: \n" + documentationData[2] + "\nExamination description: \n" + documentationData[3]);


                Label newDocumentationDate = new Label(documentationData[1]);

                newDocumentationTitle.setPrefHeight(40);
                newDocumentationTitle.setPrefWidth(830);
                newDocumentationContent.setPrefWidth(830);
                newDocumentationContent.setMinHeight(320);


                // Set fitting to scroll bar
//                if (documentationsData.length > 6) {
//                    newDocumentationTitle.setPrefWidth(1321); //1334 1321
//                    newDocumentationContent.setPrefWidth(1321);
//                } else {
//                    scrollPane.prefWidth(1000);
//                    scrollPane.maxWidth(1000);
//                    gridPane.setMaxWidth(950);
//                }

                newDocumentationTitle.setLayoutX(14);
                newDocumentationTitle.setFont(new Font("Consolas Bold", 26.0));
                newDocumentationDate.setPrefHeight(50);
                newDocumentationDate.setPrefWidth(130);
                newDocumentationDate.setLayoutX(710);
                newDocumentationDate.setLayoutY(0);
                newDocumentationDate.setFont(new Font("Consolas Bold", 20.0));
                //newDocumentationContent.setMaxHeight(560);
                newDocumentationContent.setLayoutX(14);
                newDocumentationContent.setPadding(new Insets(40, 0, 0, 0));
                newDocumentationContent.setWrapText(true); // Text wrapping
                newDocumentationContent.setFont(new Font("Consolas", 14.0));

                newDocumentation.getChildren().addAll(newDocumentationTitle, newDocumentationDate, newDocumentationContent);

                Pane newDeleteButtonPane = new Pane();
                Button newDeleteButton = new Button("Delete");
                newDeleteButtonPane.setPrefWidth(104);
                newDeleteButton.setPrefHeight(40);
                newDeleteButton.setPrefWidth(80);
                newDeleteButton.setLayoutY(140);
                newDeleteButton.setLayoutX(12);
                newDeleteButton.setFocusTraversable(false);

                newDeleteButton.setOnMouseClicked(MouseEvent -> {
                    if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                        try {
                            message.sendDeleteDocumentation(SendToServer, Client.clientId + "#/#" + documentationData[0]);
                            String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                            if (serverAnswer1.equals("Documentation deleted correctly.")) {
                                peselStatusLabel.setTextFill(Color.greenGradient());
                                peselStatusLabel.setText("Documentation deleted correctly!");
                                setAllVisibleFalse();
                                addPane.getChildren().remove(newAddButton);
                                gridPane1.getChildren().remove(newDocumentation);
                                gridPane1.getChildren().remove(newDeleteButtonPane);
                                gridPane1.requestLayout(); // call layoutChildren() on the GridPane
                            } else {
                                peselStatusLabel.setTextFill(Color.redGradient());
                                peselStatusLabel.setText("Error while deleting documentation!");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                    peselStatusLabel.setText("");
                                    peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                                }));
                        timeline.setCycleCount(1);
                        timeline.play();
                    } else {
                        peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                        peselStatusLabel.setText("First enter pesel!");
                    }
                });

                newDeleteButtonPane.getChildren().add(newDeleteButton);

                // Add new notification to the GridPane on the appropriate row
                gridPane1.add(newDocumentation, 0, i);
                gridPane1.add(newDeleteButtonPane, 1, i);
            }
            gridPane1.requestLayout();
        }

        addPane.setVisible(true);
        newAddButton.setPrefHeight(40);
        newAddButton.setPrefWidth(80);
        addPane.setStyle("-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: transparent; -fx-background-radius: 2;");
        addPane.setPrefWidth(106);
        addPane.setMinHeight(62);
        newAddButton.setLayoutY(10);
        newAddButton.setLayoutX(13);
        newAddButton.setFocusTraversable(false);

        newAddButton.setOnMouseClicked(MouseEvent -> {
            if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                GridPane grid = new GridPane();
                TextArea inputFieldInterview = new TextArea();
                inputFieldInterview.setPromptText("Information about interview.");
                TextArea inputFieldPhysicalExamination = new TextArea();
                TextArea inputFieldICD10 = new TextArea();
                TextField inputFieldRecommendationId = new TextField();
                Label label = new Label("");
                label.setTextFill(Paint.valueOf("#ff0000"));

                ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                Alert alert = createDocumentationAlert(grid, inputFieldInterview, okButtonType, "Interview description:", inputFieldPhysicalExamination, inputFieldRecommendationId, inputFieldICD10);
                alert.setTitle("Add new documentation:");
                alert.setHeaderText("Write below properties of documentation.");
                label.setMinWidth(100);
                label.setMinHeight(30);
                label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

                VBox vBox = new VBox();
                vBox.getChildren().addAll(label, grid);
                vBox.setAlignment(Pos.CENTER);
                alert.getDialogPane().setContent(vBox);

                alert.setOnCloseRequest(alertEvent -> {
                    if (alert.getResult() == okButtonType && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                        if (!inputFieldInterview.getText().isEmpty() && !inputFieldICD10.getText().isEmpty() && !inputFieldRecommendationId.getText().isEmpty()) {
                            message.addDocumentation(SendToServer, Client.clientId + "#/#" + inputFieldInterview.getText() + "#/#" + inputFieldPhysicalExamination.getText() + "#/#" + inputFieldICD10.getText() + "#/#" + inputFieldRecommendationId.getText() + "#/#" + peselTextField.getText() + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.doctor_id);

                            try {
                                String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                                if (serverAnswer1.equals("Documentation added correctly.")) {
                                    peselStatusLabel.setTextFill(Color.greenGradient());
                                    peselStatusLabel.setText("Documentation added correctly!");
                                    setAllVisibleFalse();
                                    addPane.getChildren().remove(newAddButton);
                                } else {
                                    peselStatusLabel.setTextFill(Color.redGradient());
                                    peselStatusLabel.setText("Error while adding documentation!");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                        peselStatusLabel.setText("");
                                        peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                                    }));
                            timeline.setCycleCount(1);
                            timeline.play();
                        } else if (!inputFieldRecommendationId.getText().matches("\\d+")) {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Wrong Recommendation ID!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        } else if (inputFieldInterview.getText().isEmpty()) {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Empty interview description!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        } else if (inputFieldPhysicalExamination.getText().isEmpty()) {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Empty physical examination!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        } else {
                            alertEvent.consume(); // cancel closing alert on ok button pressed
                            label.setText("Fill all gaps!");
                            label.setTextFill(Paint.valueOf("#ff0000"));
                        }
                    }
                });
                Platform.runLater(alert::showAndWait);
            } else {
                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                peselStatusLabel.setText("First enter pesel!");
            }
        });

        addPane.getChildren().add(newAddButton);

    }

    private void setAllVisibleFalse() {
        scrollPane.setVisible(false);
        scrollPane1.setVisible(false);
        scrollPane2.setVisible(false);
        addPane.setVisible(false);
        smiPanelPane.setVisible(false);
        gridPane.getChildren().clear();
        //gridPane.requestLayout();
        gridPane1.getChildren().clear();
        //gridPane1.requestLayout();
        gridPane2.getChildren().clear();
       // gridPane2.requestLayout();
        smiStatusLabel.setText("");
    }

    @FXML
    public void checkDocumentationButtonClicked(ActionEvent actionEvent) throws IOException {
        setAllVisibleFalse();

        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            scrollPane1.setVisible(true);
            getDocumentationsFromDB();
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void enterPressedInPeselField(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER)
            getPatient();
    }

    private void getPatient() throws IOException {
        if (checkPesel()) {
            message.sendGetPatientMessage(SendToServer, Client.clientId + "#/#" + peselTextField.getText());
            String serverAnswer = Client.getServerResponse(ReadFromServer);

            String[] settingsData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

            if (serverAnswer.equals("[Patient not found, Patient not found, Patient not found]")) {
                peselStatusLabel.setTextFill(Color.redGradient());
                peselStatusLabel.setText("Patient not found!");
                firstNameLabel.setText("");
                lastNameLabel.setText("");
                birthDateLabel.setText("");
                smiCodeTextField.setText("");
                smiCodePane.setVisible(false);
            } else {
                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                firstNameLabel.setText(settingsData[0]);
                lastNameLabel.setText(settingsData[1]);
                birthDateLabel.setText(settingsData[2]);
                smiCodeTextField.setText("");
                setAllVisibleFalse();
                smiCodePane.setVisible(true);
            }
        }
    }

    private boolean checkPesel() {
        if (peselTextField.getText().length() > 11)
            peselStatusLabel.setText("Pesel too long!");
        else if (peselTextField.getText().length() < 11)
            peselStatusLabel.setText("Pesel too short!");
        else if (peselTextField.getText().contains(" "))
            peselStatusLabel.setText("Pesel contains space!");
        else if (!peselTextField.getText().matches("\\d+"))
            peselStatusLabel.setText("Wrong pesel!");
        else {
            peselStatusLabel.setText("");
            return true;
        }

        firstNameLabel.setText("");
        lastNameLabel.setText("");
        birthDateLabel.setText("");
        smiCodePane.setVisible(false);

        return false;
    }

    @FXML
    private void unHighlightOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            imageView.requestFocus();
            if (peselTextField.getText().isEmpty()) {
                firstNameLabel.setText("");
                lastNameLabel.setText("");
                birthDateLabel.setText("");
            }
        }
    }

    private void getSettingsFromDB() throws IOException {
        message.sendGetDoctorSettingsMessage(SendToServer, Client.clientId + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] settingsData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

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
        setAllVisibleFalse();
        smiCodePane.setVisible(false);

        try {
            getSettingsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (dateLabel.isVisible()) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateDateTime()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }


}
