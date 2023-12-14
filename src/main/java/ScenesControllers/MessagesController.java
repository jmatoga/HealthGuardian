package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;


public class MessagesController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button userPanelButton;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessagesController.ReadFromServer = Client.ReadFromServer;
        MessagesController.SendToServer = Client.SendToServer;
        try {
            getMessagesFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMessagesFromDB() throws IOException {
        message.sendGetMessagesMessage(SendToServer, Client.clientId  + "," + Client.user_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

        if(serverAnswer.equals("[[No messages in database]]")) {
            Pane newMessage = new Pane();
            Label newMessageTitle = new Label("There is no messages from system.");
            newMessageTitle.setPrefHeight(120);
            newMessageTitle.setPrefWidth(1334);
            newMessageTitle.setLayoutX(14);
            newMessageTitle.setAlignment(Pos.CENTER);
            newMessageTitle.setFont(new Font("Consolas Bold", 50.0));
            newMessage.getChildren().add(newMessageTitle);
            gridPane.add(newMessage, 0, 0);
        } else {
            String[] messagesData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < messagesData.length; i++) {
                String[] messageData = messagesData[i].split(", ");
                System.out.println(messagesData.length);
                Pane newMessage = new Pane();
                Label newMessageTitle = new Label(messageData[1]);
                Label newMessageContent = new Label(messageData[2]);
                newMessageTitle.setPrefHeight(40);
                // Set fitting to scroll bar
                if(messagesData.length > 6) {
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
                newMessage.getChildren().addAll(newMessageTitle, newMessageContent);

                // Add new messages to the GridPane on the appropriate row
                gridPane.add(newMessage, 0, i);
            }
        }
    }
}
