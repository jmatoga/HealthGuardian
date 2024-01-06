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
import javafx.scene.layout.ColumnConstraints;
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
import java.util.List;
import java.util.ResourceBundle;

public class DoctorUserController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private ImageView imageView;

    @FXML
    private Label dateLabel, peselStatusLabel, firstNameLabel, lastNameLabel, birthDateLabel;

    @FXML
    private TextField peselTextField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane addMedicalHistoryPane;

    @FXML
    public void doctorPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    private Alert createMedicalHistoryAlert(GridPane grid, TextField inputFieldMedicalCase, ButtonType okButtonType, String labelName, TextField inputFieldICD10Code,TextField inputFieldICD10FirstLetter) {
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

    private void getMedicalHistoryFromDB() throws IOException {
        message.sendGetDoctorMedicalHistoryMessage(SendToServer, Client.clientId + "," + peselTextField.getText());
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No medical history in database]]")) {
            Pane newMedicalHistory = new Pane();
            Label newMedicalHistoryTitle = new Label("There is no medical history.");
            newMedicalHistoryTitle.setPrefHeight(120);
            newMedicalHistoryTitle.setPrefWidth(1334);
            newMedicalHistoryTitle.setLayoutX(14);
            newMedicalHistoryTitle.setAlignment(Pos.CENTER);
            newMedicalHistoryTitle.setFont(new Font("Consolas Bold", 50.0));
            newMedicalHistory.getChildren().add(newMedicalHistoryTitle);
            gridPane.add(newMedicalHistory, 0, 0);
        } else {
            String[] medicalHistoriesData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < medicalHistoriesData.length; i++) {
                String[] medicalHistoryData = medicalHistoriesData[i].split(", ");

                // be ready to generate pdf without checking database again
                //content.add(List.of(medicalHistoryData[0] + medicalHistoryData[1], medicalHistoryData[2]));

                Pane newMedicalHistory = new Pane();
                Label newMedicalHistoryTitle = new Label(medicalHistoryData[0] + medicalHistoryData[1]);
                Label newMedicalHistoryContent = new Label(medicalHistoryData[2]);

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
                //newDeleteButton.getStyleClass().add("darkblue-button");
                //newDeleteButtonPane.setMinWidth(119);
                newDeleteButtonPane.setPrefWidth(104);
                //newDeleteButtonPane.setMaxWidth(120);
                newDeleteButton.setPrefHeight(40);
                newDeleteButton.setPrefWidth(80);
                //newDeleteButton.setMaxWidth(80);
                //newDeleteButton.setMinWidth(80);
                newDeleteButton.setLayoutY(40);
                newDeleteButton.setLayoutX(12);
               // newDeleteButton.setPadding(new Insets(40, 10, 40, 10));
                //newDeleteButton.setAlignment(Pos.CENTER);
                newDeleteButton.setFocusTraversable(false);
                //newDeleteButton.setTextAlignment(TextAlignment.CENTER);

                //newDeleteButton.setLayoutX(newDeleteButton.getMinWidth() - newDeleteButton.getMinWidth() / 2);
                //newDeleteButton.setLayoutY(newDeleteButton.getMinHeight() - newDeleteButton.getMinHeight() / 2);
                Platform.runLater(() -> {
                    System.out.println(newDeleteButton.getWidth() + " " + newDeleteButton.getHeight());
                    System.out.println(newDeleteButtonPane.getWidth() + "! !" + newDeleteButtonPane.getHeight());
                });

                newDeleteButton.setOnMouseClicked(MouseEvent -> {
                    System.out.println("Delete button clicked");
                });

                newDeleteButtonPane.getChildren().add(newDeleteButton);

                // Add new notification to the GridPane on the appropriate row
                gridPane.add(newMedicalHistory, 0, i);
                gridPane.add(newDeleteButtonPane, 1, i);
            }

            addMedicalHistoryPane.setVisible(true);
            Button newAddButton = new Button("Add new");
            //newDeleteButton.getStyleClass().add("darkblue-button");
            //newDeleteButtonPane.setMinWidth(119);
            //addMedicalHistoryPane.setPrefWidth(104);
            //newDeleteButtonPane.setMaxWidth(120);
            newAddButton.setPrefHeight(40);
            newAddButton.setPrefWidth(80);
            addMedicalHistoryPane.setStyle("-fx-border-color: grey; -fx-border-width: 2px; -fx-background-color: transparent; -fx-background-radius: 2;");
            //addMedicalHistoryPane.setPrefHeight(24);
            addMedicalHistoryPane.setPrefWidth(106);
            //addMedicalHistoryPane.setMinHeight(50);
            addMedicalHistoryPane.setMinHeight(62);
            //newDeleteButton.setMaxWidth(80);
            //newDeleteButton.setMinWidth(80);
//            newAddButton.setLayoutY(10);
//            newAddButton.setLayoutX(32);
            newAddButton.setLayoutY(10);
            newAddButton.setLayoutX(13);
            // newDeleteButton.setPadding(new Insets(40, 10, 40, 10));
            //newDeleteButton.setAlignment(Pos.CENTER);
            newAddButton.setFocusTraversable(false);
            //newDeleteButton.setTextAlignment(TextAlignment.CENTER);

            //newDeleteButton.setLayoutX(newDeleteButton.getMinWidth() - newDeleteButton.getMinWidth() / 2);
            //newDeleteButton.setLayoutY(newDeleteButton.getMinHeight() - newDeleteButton.getMinHeight() / 2);
            Platform.runLater(() -> {
                System.out.println(newAddButton.getWidth() + " " + newAddButton.getHeight());
                System.out.println(addMedicalHistoryPane.getWidth() + "! !" + addMedicalHistoryPane.getHeight());
            });

            newAddButton.setOnMouseClicked(MouseEvent -> {
                if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
                    GridPane grid = new GridPane();
                    TextField inputFieldMedicalCase = new TextField();
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
                            if(!inputFieldMedicalCase.getText().isEmpty() && !inputFieldICD10FirstLetter.getText().isEmpty() && !inputFieldICD10Code.getText().isEmpty()) {
                                message.addMedicalHistory(SendToServer, Client.clientId + "," + inputFieldMedicalCase.getText() + "," + inputFieldICD10FirstLetter.getText() + "," + inputFieldICD10Code.getText() + "," + peselTextField.getText());

                                try {
                                    String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                                    if (serverAnswer1.equals("Medical history added correctly.")) {
                                        peselStatusLabel.setTextFill(Color.greenGradient());
                                        peselStatusLabel.setText("Medical history added correctly!");
                                        scrollPane.setVisible(false);
                                        addMedicalHistoryPane.setVisible(false);
                                        addMedicalHistoryPane.getChildren().remove(newAddButton);
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

            addMedicalHistoryPane.getChildren().add(newAddButton);
        }
    }

    @FXML
    public void changeMedicalHistoryButtonClicked(ActionEvent actionEvent) throws IOException {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            scrollPane.setVisible(true);
            getMedicalHistoryFromDB();
        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void prescribeEPrescriptionButtonClicked(ActionEvent actionEvent) {
        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextField inputFieldMedicines = new TextField();
            inputFieldMedicines.setPromptText("Eg. 2x Metoprolol; 1x Warfarin");
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);;
            Alert alert = createEditDataAlert(grid, inputFieldMedicines, okButtonType, "Medicines:");
            alert.setTitle("Prescribe e-prescription:");
            alert.setHeaderText("Write below medicines to e-prescription.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !inputFieldMedicines.getText().isEmpty() && !peselTextField.getText().isEmpty()) {
                    message.prescribeEPrescription(SendToServer, Client.clientId + "," + inputFieldMedicines.getText() + "," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," + Client.doctor_id + "," + peselTextField.getText());

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

    private Alert createEditDataAlert(GridPane grid, TextField inputField, ButtonType okButtonType, String labelName) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 10, 0));
        grid.setMinWidth(420);
        grid.setMaxWidth(420);

        inputField.setMinHeight(100);
        inputField.setMinWidth(400);
        inputField.setAlignment(Pos.TOP_LEFT);

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
        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextField inputFieldEReferralName = new TextField();
            inputFieldEReferralName.setPromptText("Eg. Dermatology Consultation");
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);;
            Alert alert = createEditDataAlert(grid, inputFieldEReferralName, okButtonType, "E-referral name:");
            alert.setTitle("Prescribe e-referral:");
            alert.setHeaderText("Write below name of e-referral.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !inputFieldEReferralName.getText().isEmpty() && !peselTextField.getText().isEmpty()) {
                    message.prescribeEReferral(SendToServer, Client.clientId + "," + inputFieldEReferralName.getText() + "," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," + Client.doctor_id + "," + peselTextField.getText());

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
    public void checkFindingsButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
            peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    private Alert createRecommendationAlert(GridPane grid, TextField inputFieldMedicines, ButtonType okButtonType, String labelName, DatePicker nextCheckUpDate, TextField inputFieldNextCheckUpName, TextField inputFieldAdditionalInformation, TextField inputFieldDiet) {
        grid.add(new Label("Medicines:"), 0, 3);
        inputFieldMedicines.setMinHeight(100);
        inputFieldMedicines.setMinWidth(400);
        inputFieldMedicines.setAlignment(Pos.TOP_LEFT);
        inputFieldMedicines.setPromptText("Eg. 2x Metoprolol; 1x Warfarin");
        grid.add(inputFieldMedicines, 0, 4);

        grid.add(new Label("Additional information:"), 0, 5);
        inputFieldAdditionalInformation.setMinHeight(150);
        inputFieldAdditionalInformation.setMinWidth(400);
        inputFieldAdditionalInformation.setAlignment(Pos.TOP_LEFT);
        inputFieldAdditionalInformation.setPromptText("Eg. Patient should avoid alcohol.");
        grid.add(inputFieldAdditionalInformation, 0, 6);

        grid.add(new Label("Next check up name:"), 0, 7);
        inputFieldNextCheckUpName.setPromptText("Eg. Dermatology Consultation");
        grid.add(inputFieldNextCheckUpName, 0, 8);

        grid.add(new Label("Next check up date:"), 0, 9);
        nextCheckUpDate.setMinWidth(100);
        nextCheckUpDate.setMaxWidth(100);
        nextCheckUpDate.setPromptText(LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonthValue() + "." + LocalDate.now().getYear());
        nextCheckUpDate.setTranslateX(300);
        grid.add(nextCheckUpDate, 0, 9);
        grid.add(new Pane(), 0, 11);

        return createEditDataAlert(grid, inputFieldDiet, okButtonType, labelName);
    }

    @FXML
    public void addRecommendationsButtonClicked(ActionEvent actionEvent) {
        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextField inputFieldDiet = new TextField();
            inputFieldDiet.setPromptText("Eg. Low-carb diet with increased protein intake.");
            TextField inputFieldMedicines = new TextField();
            TextField inputFieldNextCheckUpName = new TextField();
            TextField inputFieldAdditionalInformation = new TextField();
            DatePicker nextCheckUpDate = new DatePicker();

            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Alert alert = createRecommendationAlert(grid, inputFieldMedicines, okButtonType, "Diet:", nextCheckUpDate, inputFieldNextCheckUpName, inputFieldAdditionalInformation, inputFieldDiet);
            alert.setTitle("Add recommendation:");
            alert.setHeaderText("Write below recommendation.");

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !peselTextField.getText().isEmpty()) {
                    message.addRecommendation(SendToServer, Client.clientId + "," + inputFieldDiet.getText() + "," + inputFieldMedicines.getText() + "," + nextCheckUpDate.getValue() + "," + inputFieldNextCheckUpName.getText() + "," + inputFieldAdditionalInformation.getText() + "," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," + Client.doctor_id + "," + peselTextField.getText());

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

    @FXML
    public void checkDocumentationButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

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
            message.sendGetPatientMessage(SendToServer, Client.clientId + "," + peselTextField.getText());
            String serverAnswer = Client.getServerResponse(ReadFromServer);

            String[] settingsData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

            if (serverAnswer.equals("[Patient not found, Patient not found, Patient not found]")) {
                peselStatusLabel.setTextFill(Color.redGradient());
                peselStatusLabel.setText("Patient not found!");
            } else {
                peselStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
                firstNameLabel.setText(settingsData[0]);
                lastNameLabel.setText(settingsData[1]);
                birthDateLabel.setText(settingsData[2]);
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
        message.sendGetDoctorSettingsMessage(SendToServer, Client.clientId + "," + Client.doctor_id);
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
        scrollPane.setVisible(false);

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
