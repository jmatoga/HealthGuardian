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
    private Label settingsSavedStatusLabel;

    @FXML
    private Label settingsDescription1Label;

    @FXML
    private Label settingsDescription2Label;

    @FXML
    private Label settingsDescription3Label;

    @FXML
    private Label settingsDescription4Label;

    @FXML
    private Label settingsDescription5Label;

    @FXML
    private Label settingsDescription6Label;

    @FXML
    private Label settingsDescription7Label;

    @FXML
    private Label settingsDescription8Label;

    @FXML
    private Label settingsDescription9Label;

    @FXML
    private Label settingsDescription10Label;

    @FXML
    private CheckBox bmiSettingsCheckBox;

    @FXML
    private CheckBox ageSettingsCheckBox;

    @FXML
    private CheckBox dateSettingsCheckBox;

    @FXML
    private CheckBox settingsCheckBox4;

    @FXML
    private CheckBox settingsCheckBox5;

    @FXML
    private CheckBox settingsCheckBox6;

    @FXML
    private CheckBox settingsCheckBox7;

    @FXML
    private CheckBox settingsCheckBox8;

    @FXML
    private CheckBox settingsCheckBox9;

    @FXML
    private CheckBox settingsCheckBox10;

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
        message.sendSetSettingsMessage(SendToServer, Client.clientId + "," + Client.user_id + "," + bmiSettingsCheckBox.isSelected() + "," + ageSettingsCheckBox.isSelected() + "," + dateSettingsCheckBox.isSelected() + "," + settingsCheckBox4.isSelected() + "," + settingsCheckBox5.isSelected());
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
        settingsCheckBox4.setSelected(settingsData[3].equals("true"));
        settingsCheckBox5.setSelected(settingsData[4].equals("true"));
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
