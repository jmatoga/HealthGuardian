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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;

import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;

public class ClientPanelController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label dataUpdatedStatusLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label weightLabel;

    @FXML
    private Label heightLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label presureLabel;

    @FXML
    private Label dateOfLastUpdateLabel;

    @FXML
    Label bmiLabel;

    @FXML
    Label bmiStatusLabel;

    @FXML
    private Button ePrescriptionButton;

    @FXML
    private Button eReferralButton;

    @FXML
    private Button testScheduleButton;

    @FXML
    private Button findingsButton;

    @FXML
    private Button eContactWithADoctorButton;

    @FXML
    private Button listOfClinicsButton;

    @FXML
    private Button MessageButton;

    @FXML
    private Button presurePanelButton;

    @FXML
    private Button medicalHistoryButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button editDataButton;

    @FXML
    private Button SMIButton;

    @FXML
    private Button logOutButton;

    @FXML
    AnchorPane clientPanelScene;

    public AnchorPane getPane() {
        return clientPanelScene;
    }

    @FXML
    private void ePrescriptionButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("EPrescriptionScene.fxml");
    }

    @FXML
    private void eReferralButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("EReferralScene.fxml");
    }

    @FXML
    private void testScheduleButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("ExaminationScheduleScene.fxml");
    }

    @FXML
    private void findingsButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("FindingsScene.fxml");
    }

    @FXML
    private void eContactWithADoctorButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("EContactScene.fxml");
    }

    @FXML
    private void listOfClinicsButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("listOfClinicsScene.fxml");
    }

    @FXML
    private void messageButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("MessagesScene.fxml");
    }

    @FXML
    private void presurePanelButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("PresurePanelScene.fxml");
    }

    @FXML
    private void medicalHistoryButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("MedicalHistoryScene.fxml");
    }

    @FXML
    private void settingsButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("SettingsScene.fxml");
    }

    @FXML
    private void editDataButtonClicked(ActionEvent event) throws  IOException{
        GridPane grid = new GridPane();
        TextField inputFieldWeight = new TextField();
        TextField inputFieldHeight = new TextField();
        TextField inputFieldTemperature = new TextField();
        TextField inputFieldPressure1 = new TextField();
        TextField inputFieldPressure2 = new TextField();
        Label label = new Label("");
        label.setTextFill(Paint.valueOf("#ff0000"));
        Alert alert = createEditDataAlert(grid, inputFieldWeight, inputFieldHeight, inputFieldTemperature, inputFieldPressure1, inputFieldPressure2, label);

        alert.setOnCloseRequest(alertEvent -> {
            if(checkEditData(alertEvent, inputFieldWeight, inputFieldHeight, inputFieldTemperature, inputFieldPressure1, inputFieldPressure2, label)) {
                message.updateUserBasicData(SendToServer, Client.clientId + "," + "withoutBirthdayDate" + "," + inputFieldWeight.getText() + "," + inputFieldHeight.getText() + "," + inputFieldTemperature.getText() + "," + inputFieldPressure1.getText() + "," + inputFieldPressure2.getText() + "," + LocalDate.now() + "," + Client.user_id);
                try {
                    String serverAnswer = Client.rreader(ReadFromServer);
                    System.out.println(serverAnswer);
                    if (serverAnswer.equals("Updated user basic data correctly.")) {
                        getUserDataFromDB();
                        dataUpdatedStatusLabel.setText("Data updated correctly!");
                    } else
                        dataUpdatedStatusLabel.setText("Error while updating!");

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                dataUpdatedStatusLabel.setText("");
                            }));
                    timeline.setCycleCount(1);
                    timeline.play();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        alert.showAndWait();
    }

    private boolean checkEditData(DialogEvent alertEvent, TextField inputFieldWeight, TextField inputFieldHeight, TextField inputFieldTemperature, TextField inputFieldPressure1, TextField inputFieldPressure2, Label label) {
        if (inputFieldWeight.getText().isEmpty() || inputFieldHeight.getText().isEmpty() || inputFieldTemperature.getText().isEmpty() || inputFieldPressure1.getText().isEmpty() || inputFieldPressure2.getText().isEmpty()) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Fill all labels!");
        } else if (!inputFieldWeight.getText().matches("\\d*\\.?\\d+") || Float.parseFloat(inputFieldWeight.getText()) >= 250.0 || Float.parseFloat(inputFieldWeight.getText()) <= 30.0) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Wrong Weight!");
        } else if (!inputFieldHeight.getText().matches("\\d*\\.?\\d+") || Float.parseFloat(inputFieldHeight.getText()) >= 250.0 || Float.parseFloat(inputFieldHeight.getText()) <= 100.0) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Wrong Height!");
        } else if (!inputFieldTemperature.getText().matches("\\d*\\.?\\d+") || Float.parseFloat(inputFieldTemperature.getText()) >= 50.0 || Float.parseFloat(inputFieldTemperature.getText()) <= 20.0) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Wrong Temperature!");
        } else if (!inputFieldPressure1.getText().matches("\\d+") || Float.parseFloat(inputFieldPressure1.getText()) >= 250.0 || Float.parseFloat(inputFieldPressure1.getText()) <= 50.0) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Wrong systolic pressure parametr!");
        } else if (!inputFieldPressure2.getText().matches("\\d+") || Float.parseFloat(inputFieldPressure2.getText()) >= 150.0 || Float.parseFloat(inputFieldPressure2.getText()) <= 30.0) {
            alertEvent.consume(); // cancel closing alert on ok button pressed
            label.setText("Wrong diastolic pressure parametr!");
        } else
            return true;

        return false;
    }

    @FXML
    private void SMIButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("ShortMedicalInterviewScene.fxml");
    }

    @FXML
    private void LogOutButtonClicked(ActionEvent event) throws  IOException{
        new SceneSwitch("LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    @FXML
    void initializeHoverEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(GREEN);  // Change color as per your preference

        ePrescriptionButton.setOnMouseEntered(e -> {
            ePrescriptionButton.setEffect(shadow);
        });

        ePrescriptionButton.setOnMouseExited(e -> {
            ePrescriptionButton.setEffect(null);
        });
    }

    private void getUserDataFromDB() throws IOException {
        int user_id = Client.user_id;
        String user_id_str = Integer.toString(user_id);
        message.sendGetNameMessage(SendToServer,Client.clientId  + "," + user_id_str);
        String serverAnswer = Client.rreader(ReadFromServer);

        if(serverAnswer != null)
            System.out.println(serverAnswer);

        assert serverAnswer != null;
        String[] userData = serverAnswer.substring(1,serverAnswer.length()-1).split(", ");

        if(userData[2].equals("No data")) {
            TextField inputFieldWeight = new TextField();
            TextField inputFieldHeight = new TextField();
            TextField inputFieldTemperature = new TextField();
            TextField inputFieldPressure1 = new TextField();
            TextField inputFieldPressure2 = new TextField();
            DatePicker datePicker = new DatePicker();
            Label label = new Label("");
            label.setTextFill(Paint.valueOf("#ff0000"));
            Alert alert = createDataAlert(inputFieldWeight, inputFieldHeight, inputFieldTemperature, inputFieldPressure1, inputFieldPressure2, datePicker, label);

            // Blocked "x" button in alert
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setOnCloseRequest(event ->  {
                label.setText("You have to insert data!");
                event.consume();
            });

            alert.setOnCloseRequest(alertEvent -> {
                if (datePicker.getValue().toString().isEmpty() || !datePicker.getValue().toString().matches("\\d{4}-\\d+-\\d+") || datePicker.getValue().isAfter(LocalDate.now())) {
                    alertEvent.consume(); // cancel closing alert on ok button pressed
                    label.setText("Wrong Date!");
                } else if(checkEditData(alertEvent, inputFieldWeight, inputFieldHeight, inputFieldTemperature, inputFieldPressure1, inputFieldPressure2, label)) {
                    message.updateUserBasicData(SendToServer, Client.clientId + "," + datePicker.getValue() + "," + inputFieldWeight.getText() + "," + inputFieldHeight.getText() + "," + inputFieldTemperature.getText() + "," + inputFieldPressure1.getText() + "," + inputFieldPressure2.getText() + "," + LocalDate.now() + "," + Client.user_id);

                    if(userData[2].equals("No data")) {
                        try {
                            String serverAnswer1 = Client.rreader(ReadFromServer);
                            System.out.println(serverAnswer1);
                            getUserDataFromDB();

                            if (!userData[2].equals("No data"))
                                dataUpdatedStatusLabel.setText("Data updated correctly!");
                             else
                                dataUpdatedStatusLabel.setText("Error while updating!");

                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                        dataUpdatedStatusLabel.setText("");
                                    }));
                            timeline.setCycleCount(1);
                            timeline.play();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            Platform.runLater(alert::showAndWait);
            ageLabel.setText("age: " + userData[2]);
            presureLabel.setText("last pressure: " + userData[6]);
        } else {
            ageLabel.setText("age: " + LocalDate.parse(userData[2]).until(LocalDate.now()).getYears());
            presureLabel.setText("last pressure: " + userData[6] + "/" + userData[7]);
        }

        firstNameLabel.setText(userData[0]);
        lastNameLabel.setText(userData[1]);
        birthDateLabel.setText(userData[2]);
        weightLabel.setText("weight: " + userData[3]);
        heightLabel.setText("height: " + userData[4]);
        temperatureLabel.setText("temperature: " + userData[5]);
        dateOfLastUpdateLabel.setText("date of last update:\n" + userData[8]);
        calculateBMI(Double.parseDouble(userData[3]), Double.parseDouble(userData[4]));
    }

    private Alert createDataAlert(TextField inputFieldWeight, TextField inputFieldHeight, TextField inputFieldTemperature, TextField inputFieldPressure1, TextField inputFieldPressure2, DatePicker datePicker, Label label) {
        GridPane grid = new GridPane();

        grid.add(new Label("Birth date:"), 0, 0);
        datePicker.setMinWidth(100);
        datePicker.setMaxWidth(100);
        datePicker.setPromptText(LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonthValue() + "." + LocalDate.now().getYear());
        grid.add(datePicker, 1, 0);

        return createEditDataAlert(grid, inputFieldWeight, inputFieldHeight, inputFieldTemperature, inputFieldPressure1, inputFieldPressure2, label);
    }

    private Alert createEditDataAlert(GridPane grid, TextField inputFieldWeight, TextField inputFieldHeight, TextField inputFieldTemperature, TextField inputFieldPressure1, TextField inputFieldPressure2, Label label) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 10, 0));
        grid.setMinWidth(350);
        grid.setMaxWidth(200);

        ColumnConstraints spacerColumn1 = new ColumnConstraints();
        spacerColumn1.setMaxWidth(75);

        ColumnConstraints spacerColumn2 = new ColumnConstraints();
        spacerColumn2.setMaxWidth(53);

        ColumnConstraints spacerColumn3 = new ColumnConstraints();
        spacerColumn3.setPrefWidth(18);

        ColumnConstraints spacerColumn4 = new ColumnConstraints();
        spacerColumn4.setPrefWidth(30);

        grid.getColumnConstraints().addAll(spacerColumn1, spacerColumn2, spacerColumn3, spacerColumn4);

        label.setMinWidth(100);
        label.setMinHeight(30);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        grid.add(new Label("Weight:"), 0, 1);
        inputFieldWeight.setPromptText("75.23");
        grid.add(inputFieldWeight, 1, 1);
        grid.add(new Label("kg"), 2, 1);

        grid.add(new Label("Height:"), 0, 2);
        inputFieldHeight.setPromptText("183.23");
        grid.add(inputFieldHeight, 1, 2);
        grid.add(new Label("cm"), 2, 2);

        grid.add(new Label("Temperature:"), 0, 3);
        inputFieldTemperature.setPromptText("36.6");
        grid.add(inputFieldTemperature, 1, 3);
        grid.add(new Label("°C"), 2, 3);

        grid.add(new Label("Pressure:"), 0, 4);
        inputFieldPressure1.setPromptText("120");
        grid.add(inputFieldPressure1, 1, 4);
        grid.add(new Label("/"), 2, 4);
        inputFieldPressure2.setPromptText("80");
        inputFieldPressure2.setTranslateX(-14);
        grid.add(inputFieldPressure2, 3, 4);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Insert data:");
        alert.setHeaderText("Write below your data.");
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButtonType);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, grid);
        vBox.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(vBox);

        return alert;
    }

    public void initialize(URL location, ResourceBundle resources) {
        initializeHoverEffect();
        ClientPanelController.ReadFromServer = Client.ReadFromServer;
        ClientPanelController.SendToServer = Client.SendToServer;

        try {
            getUserDataFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

    public double calculateBMI(double weight, double height) {
        // TODO
        boolean settingsBMI = true;
        if(settingsBMI) {
            height = height / 100; // convert height from cm to m
            double bmi = weight / (height * height);

            bmiStatusLabel.setText(String.format("%.2f", bmi));


            //bmiLabel.setVisible(true);
            if (bmi < 18.5) {
                bmiStatusLabel.setTextFill(Paint.valueOf("#f54040"));
            } else if (bmi >= 18.5 && bmi < 25.0) {
                bmiStatusLabel.setTextFill(Paint.valueOf("#40f546"));
            } else if (bmi >= 25.0 && bmi < 30.0) {
                bmiStatusLabel.setTextFill(Paint.valueOf("#f5bb40"));
            } else {
                bmiStatusLabel.setTextFill(Paint.valueOf("#f54040"));
            }

            return bmi;
        }
       // return 0;
        return 0;
    }
}
