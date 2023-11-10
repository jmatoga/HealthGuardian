package Scenes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLOutput;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;

public class ClientPanelController {

    private volatile boolean stop = false;

    @FXML
    private Label timeLabel;

//    private void Timenow() {
//        Thread thread = new Thread( () ->{
//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
//            while (!stop) {
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    System.out.println("Data - Errror");
//                }
//                final String timenow = sdf.format(new SimpleDateFormat());
//                Platform.runLater(() -> {
//
//                    timeLabel.setText(timenow);
//                });
//            }
//        });
//        thread.start();
//    }

}
