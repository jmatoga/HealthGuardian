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
import javafx.scene.text.Font;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorNotificationController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button doctorPanelButton;

    @FXML
    private void doctorPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorNotificationController.ReadFromServer = Client.ReadFromServer;
        DoctorNotificationController.SendToServer = Client.SendToServer;

        try {
            getDoctorNotificationsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDoctorNotificationsFromDB() throws IOException {
        message.sendGetDoctorNotificationsMessage(SendToServer, Client.clientId  + "#/#" + Client.doctor_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if(serverAnswer.equals("[[No notifications in database]]")) {
            Pane newNotification = new Pane();
            Label newNotificationTitle = new Label("There is no notifications from system.");
            newNotificationTitle.setPrefHeight(120);
            newNotificationTitle.setPrefWidth(1334);
            newNotificationTitle.setLayoutX(14);
            newNotificationTitle.setAlignment(Pos.CENTER);
            newNotificationTitle.setFont(new Font("Consolas Bold", 50.0));
            newNotification.getChildren().add(newNotificationTitle);
            gridPane.add(newNotification, 0, 0);
        } else {
            String[] notificationsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < notificationsData.length; i++) {
                String[] notificationData = notificationsData[i].split(", ");

                Pane newMessage = new Pane();
                Label newMessageTitle = new Label(notificationData[1]);
                Label newMessageContent = new Label(notificationData[2]);
                Label newMessageDate = new Label(notificationData[3]);
                newMessageTitle.setPrefHeight(40);
                // Set fitting to scroll bar
                if(notificationsData.length > 6) {
                    newMessageTitle.setPrefWidth(1321);
                    newMessageContent.setPrefWidth(1321);
                } else {
                    newMessageTitle.setPrefWidth(1334);
                    newMessageContent.setPrefWidth(1334);
                }
                newMessageTitle.setLayoutX(14);
                newMessageTitle.setFont(new Font("Consolas Bold", 36.0));
                newMessageContent.setPrefHeight(120);
                newMessageContent.setLayoutX(14);
                newMessageContent.setPadding(new Insets(40, 0, 0, 0));
                newMessageContent.setWrapText(true); // Text wrapping
                newMessageContent.setFont(new Font("Consolas", 28.0));
                newMessageDate.setPrefHeight(30);
                //newMessageDate.setPrefWidth(1334);
                newMessageDate.setLayoutX(1220);
                newMessageDate.setLayoutY(0);
                newMessageDate.setFont(new Font("Consolas Bold", 20.0));
                newMessage.getChildren().addAll(newMessageTitle, newMessageContent, newMessageDate);

                // Add new notification to the GridPane on the appropriate row
                gridPane.add(newMessage, 0, i);
            }
        }
    }
}
