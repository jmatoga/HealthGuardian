package ScenesControllers;

import Client.Client;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PressurePanelController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private LineChart<?, ?> pressureChart;

    @FXML
    private VBox vbox;

    @FXML
    private HBox legendBox;

    @FXML
    private FlowPane alertPane;

    @FXML
    private Label weightLabel, temperatureLabel;

    private boolean ifShowWeight = true;
    private boolean ifShowTemperature = true;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    private void checkPressure(int systolicPressure, int diastolicPressure) {
        if(systolicPressure < 100 || systolicPressure >= 140 || diastolicPressure < 60 || diastolicPressure >= 90) {
            alertPane.setVisible(true);

            // Animate the appearance of alertPane
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), alertPane);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Animate the disappearance of alertPane
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), alertPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            // Create an animation sequence (500ms fade in, 500ms pause, 500ms fade out)
            SequentialTransition sequence = new SequentialTransition(fadeIn, new PauseTransition(Duration.millis(500)), fadeOut);
            sequence.setCycleCount(Timeline.INDEFINITE); // Set cycle count to indefinite
            sequence.play();
        }
    }

    private void getSettingsFromDB() throws IOException {
        message.sendGetSettingsMessage(SendToServer,Client.clientId  + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);
        String[] settingsData = serverAnswer.substring(1,serverAnswer.length()-1).split(", ");

        ifShowWeight = settingsData[3].equals("false");
        ifShowTemperature = settingsData[4].equals("false");
    }

    private void getPressureFromDB() throws IOException {
        message.sendGetPressureMessage(SendToServer, Client.clientId  + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        String[] pressuresData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");
        XYChart.Series systolicPressure = new XYChart.Series();
        XYChart.Series diastolicPressure = new XYChart.Series();
        XYChart.Series weight = new XYChart.Series();
        XYChart.Series temperature = new XYChart.Series();

        for(int i=0; i< pressuresData.length; i++) {
            String[] pressureData = pressuresData[i].split(", ");
            checkPressure(Integer.parseInt(pressureData[2]), Integer.parseInt(pressureData[3]));

            Label pressureLabel = new Label(pressureData[2] + " / " + pressureData[3]);
            pressureLabel.setPrefHeight(80);
            pressureLabel.setPrefWidth(300);
            pressureLabel.setAlignment(Pos.CENTER);
            pressureLabel.setTextFill(Color.WHITE);
            pressureLabel.setFont(new Font("Consolas Bold", 50.0));

            Label dateLabel = new Label(pressureData[5]);
            dateLabel.setPrefHeight(80);
            dateLabel.setPrefWidth(250);
            dateLabel.setAlignment(Pos.CENTER);
            dateLabel.setTextFill(Color.WHITE);
            dateLabel.setFont(new Font("Consolas Italic", 20.0));

            systolicPressure.getData().add(new XYChart.Data<>(pressureData[5].replace(" ", "\n"), Integer.parseInt(pressureData[2])));
            diastolicPressure.getData().add(new XYChart.Data<>(pressureData[5].replace(" ", "\n"), Integer.parseInt(pressureData[3])));
            weight.getData().add(new XYChart.Data<>(pressureData[5].replace(" ", "\n"), Double.parseDouble(pressureData[0])));
            temperature.getData().add(new XYChart.Data<>(pressureData[5].replace(" ", "\n"), Double.parseDouble(pressureData[4])));

            HBox hbox = new HBox(); // Horizontal Box for pressure and date labels
            hbox.getChildren().addAll(pressureLabel, dateLabel);

            vbox.getChildren().add(hbox);
        }

        pressureChart.getData().addAll(systolicPressure, diastolicPressure, weight, temperature);

        if(!ifShowWeight) {
            pressureChart.getData().remove(weight);
            legendBox.getChildren().remove(weightLabel);
        }

        if(!ifShowTemperature) {
            pressureChart.getData().remove(temperature);
            legendBox.getChildren().remove(temperatureLabel);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PressurePanelController.ReadFromServer = Client.ReadFromServer;
        PressurePanelController.SendToServer = Client.SendToServer;
        alertPane.setVisible(false);

        try {
            getSettingsFromDB();
            getPressureFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
