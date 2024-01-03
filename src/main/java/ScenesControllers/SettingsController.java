package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label settingsSavedStatusLabel, settingsDescription1Label, settingsDescription2Label, settingsDescription3Label, settingsDescription4Label, settingsDescription5Label, settingsDescription6Label, settingsDescription7Label, settingsDescription8Label, settingsDescription9Label, settingsDescription10Label;

    @FXML
    private CheckBox bmiSettingsCheckBox, ageSettingsCheckBox, dateSettingsCheckBox, weightInChartSettingsCheckBox, temperatureInChartSettingsCheckBox, settingsCheckBox6, settingsCheckBox7, settingsCheckBox8, settingsCheckBox9, settingsCheckBox10;

    @FXML
    private Button userPanelButton;

    @FXML
    private Button saveSettingsButton;

    @FXML
    private AnchorPane settingsScene;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @FXML
    private void saveSettingsButtonClicked(ActionEvent event) throws IOException {
        message.sendSetSettingsMessage(SendToServer, Client.clientId + "," + Client.user_id + "," + bmiSettingsCheckBox.isSelected() + "," + ageSettingsCheckBox.isSelected() + "," + dateSettingsCheckBox.isSelected() + "," + weightInChartSettingsCheckBox.isSelected() + "," + temperatureInChartSettingsCheckBox.isSelected());
        String serverAnswer = ReadFromServer.readLine();

        if (serverAnswer.equals("Settings changed correctly.")) {
            settingsSavedStatusLabel.setText("Settings changed correctly!");

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(1300), TimeEvent -> {
                        try {
                            settingsSavedStatusLabel.setText("");
                            new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            settingsSavedStatusLabel.setTextFill(Paint.valueOf("RED"));
            settingsSavedStatusLabel.setText("Something went wrong!");
        }
    }

    private void getSettingsFromDB() throws IOException {
        message.sendGetSettingsMessage(SendToServer, Client.clientId + "," + Client.user_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        String[] settingsData = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");

        bmiSettingsCheckBox.setSelected(settingsData[0].equals("true"));
        ageSettingsCheckBox.setSelected(settingsData[1].equals("true"));
        dateSettingsCheckBox.setSelected(settingsData[2].equals("true"));
        weightInChartSettingsCheckBox.setSelected(settingsData[3].equals("true"));
        temperatureInChartSettingsCheckBox.setSelected(settingsData[4].equals("true"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SettingsController.ReadFromServer = Client.ReadFromServer;
        SettingsController.SendToServer = Client.SendToServer;

        try {
            getSettingsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
