package ScenesControllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLOutput;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientPanelController implements Initializable {

    //private volatile boolean stop = false;

    @FXML
    private Label dateLabel;
    private static Message message;
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    public void initialize(URL location, ResourceBundle resources) {
        ClientPanelController.message = LogInController.getMessageResources();
        ClientPanelController.ReadFromServer = LogInController.getReadFromServerResources();
        ClientPanelController.SendToServer = LogInController.getSendToServerResources();

        // Utwórz Timeline do cyklicznego odświeżania daty co sekundę
        //Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDateTime()));
        //timeline.setCycleCount(Timeline.INDEFINITE);
        //timeline.play();
    }

    private void updateDateTime() {
        // Pobierz aktualną datę i godzinę
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Utwórz formatter daty i godziny
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Sformatuj datę i godzinę jako tekst
        String formattedDateTime = currentDateTime.format(formatter);

        // Ustaw sformatowaną datę i godzinę jako tekst etykiety
        dateLabel.setText(formattedDateTime);
        System.out.println(formattedDateTime);
    }

}
