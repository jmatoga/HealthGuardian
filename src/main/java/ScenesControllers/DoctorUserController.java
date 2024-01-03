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
import javafx.scene.layout.VBox;
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
    private ImageView imageView;

    @FXML
    private Label dateLabel, peselStatusLabel, firstNameLabel, lastNameLabel, birthDateLabel;

    @FXML
    private TextField peselTextField;

    @FXML
    public void doctorPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    @FXML
    public void changeMedicalHistoryButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void prescribeEPrescriptionButtonClicked(ActionEvent actionEvent) {
        if (checkPesel() && !peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {
            GridPane grid = new GridPane();
            TextField inputFieldMedicines = new TextField();
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);;
            Alert alert = createEditDataAlert(grid, inputFieldMedicines, okButtonType);

            alert.setOnCloseRequest(alertEvent -> {
                if (alert.getResult() == okButtonType && !inputFieldMedicines.getText().isEmpty() && !peselTextField.getText().isEmpty()) {
                    message.prescribeEPrescription(SendToServer, Client.clientId + "," + inputFieldMedicines.getText() + "," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," + Client.doctor_id + "," + peselTextField.getText());

                    try {
                        String serverAnswer = ReadFromServer.readLine();
                        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

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
        } else
            peselStatusLabel.setText("First enter pesel!");
    }

    private Alert createEditDataAlert(GridPane grid, TextField inputFieldMedicines, ButtonType okButtonType) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 10, 0));
        grid.setMinWidth(420);
        grid.setMaxWidth(420);

        inputFieldMedicines.setMinHeight(200);
        inputFieldMedicines.setMinWidth(400);
        inputFieldMedicines.setAlignment(Pos.TOP_LEFT);

        Label dateAlertLabel = new Label(LocalDate.now().toString());
        dateAlertLabel.setTranslateX(285);
        dateAlertLabel.setFont(new Font("Consolas", 20.0));
        grid.add(dateAlertLabel, 0, 0);

        grid.add(new Label("Medicines:"), 0, 1);
        inputFieldMedicines.setPromptText("Eg. 2x Metoprolol; 1x Warfarin");
        grid.add(inputFieldMedicines, 0, 2);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Prescribe e-prescription:");
        alert.setHeaderText("Write below medicines to e-prescription.");
        alert.getButtonTypes().setAll(okButtonType);

        alert.getDialogPane().setContent(grid);
        return alert;
    }

    @FXML
    public void prescribeEReferralButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void checkFindingsButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void addRecommendationsButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
            peselStatusLabel.setText("First enter pesel!");
        }
    }

    @FXML
    public void checkDocumentationButtonClicked(ActionEvent actionEvent) {
        if (!peselTextField.getText().isEmpty() && !firstNameLabel.getText().isEmpty()) {

        } else {
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
            String serverAnswer = ReadFromServer.readLine();
            System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
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
        firstNameLabel.setText("");
        lastNameLabel.setText("");
        birthDateLabel.setText("");

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
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
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
