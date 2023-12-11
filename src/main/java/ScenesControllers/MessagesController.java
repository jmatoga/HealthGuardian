package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    private Pane message1;
    @FXML
    private Pane message2;
    @FXML
    private Pane message3;
    @FXML
    private Pane message4;
    @FXML
    private Pane message5;
    @FXML
    private Pane message6;
    @FXML
    private Pane message7;
    @FXML
    private Pane message8;
    @FXML
    private Pane message9;
    @FXML
    private Pane message10;
    @FXML
    private Pane message11;
    @FXML
    private Pane message12;
    @FXML
    private Pane message13;
    @FXML
    private Pane message14;
    @FXML
    private Pane message15;
    @FXML
    private Pane message16;
    @FXML
    private Pane message17;
    @FXML
    private Pane message18;
    @FXML
    private Pane message19;
    @FXML
    private Pane message20;
    @FXML
    private Pane message21;
    @FXML
    private Pane message22;
    @FXML
    private Pane message23;
    @FXML
    private Pane message24;
    @FXML
    private Pane message25;
    @FXML
    private Pane message26;
    @FXML
    private Pane message27;
    @FXML
    private Pane message28;
    @FXML
    private Pane message29;
    @FXML
    private Pane message30;

    @FXML
    private Button userPanelButton;

    @FXML
    private AnchorPane listOfClinicsScene;

    @FXML
    private Label messageNameLabel1;

    @FXML
    private Label messageLabel1;

    @FXML
    private Label messageNameLabel2;

    @FXML
    private Label messageLabel2;

    @FXML
    private Label messageNameLabel3;

    @FXML
    private Label messageLabel3;

    @FXML
    private Label messageNameLabel4;

    @FXML
    private Label messageLabel4;

    @FXML
    private Label messageNameLabel5;

    @FXML
    private Label messageLabel5;

    @FXML
    private Label messageNameLabel6;

    @FXML
    private Label messageLabel6;

    @FXML
    private Label messageNameLabel7;

    @FXML
    private Label messageLabel7;

    @FXML
    private Label messageNameLabel8;

    @FXML
    private Label messageLabel8;

    @FXML
    private Label messageNameLabel9;

    @FXML
    private Label messageLabel9;

    @FXML
    private Label messageNameLabel10;

    @FXML
    private Label messageLabel10;

    @FXML
    private Label messageNameLabel11;

    @FXML
    private Label messageLabel11;

    @FXML
    private Label messageNameLabel12;

    @FXML
    private Label messageLabel12;

    @FXML
    private Label messageNameLabel13;

    @FXML
    private Label messageLabel13;

    @FXML
    private Label messageNameLabel14;

    @FXML
    private Label messageLabel14;

    @FXML
    private Label messageNameLabel15;

    @FXML
    private Label messageLabel15;

    @FXML
    private Label messageNameLabel16;

    @FXML
    private Label messageLabel16;

    @FXML
    private Label messageNameLabel17;

    @FXML
    private Label messageLabel17;

    @FXML
    private Label messageNameLabel18;

    @FXML
    private Label messageLabel18;

    @FXML
    private Label messageNameLabel19;

    @FXML
    private Label messageLabel19;

    @FXML
    private Label messageNameLabel20;

    @FXML
    private Label messageLabel20;

    @FXML
    private Label messageNameLabel21;

    @FXML
    private Label messageLabel21;

    @FXML
    private Label messageNameLabel22;

    @FXML
    private Label messageLabel22;

    @FXML
    private Label messageNameLabel23;

    @FXML
    private Label messageLabel23;

    @FXML
    private Label messageNameLabel24;

    @FXML
    private Label messageLabel24;

    @FXML
    private Label messageNameLabel25;

    @FXML
    private Label messageLabel25;

    @FXML
    private Label messageNameLabel26;

    @FXML
    private Label messageLabel26;

    @FXML
    private Label messageNameLabel27;

    @FXML
    private Label messageLabel27;

    @FXML
    private Label messageNameLabel28;

    @FXML
    private Label messageLabel28;

    @FXML
    private Label messageNameLabel29;

    @FXML
    private Label messageLabel29;

    @FXML
    private Label messageNameLabel30;

    @FXML
    private Label messageLabel30;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessagesController.ReadFromServer = Client.ReadFromServer;
        MessagesController.SendToServer = Client.SendToServer;
        //getMessagesFromDB();
        System.out.println("" + message1.isVisible() + message2.isVisible());
        message1.setVisible(false);
        message2.setVisible(false);
        message3.setVisible(false);
        message4.setVisible(false);
        message5.setVisible(false);
        message6.setVisible(false);
        message7.setVisible(false);
        message8.setVisible(false);
        message9.setVisible(false);
        message10.setVisible(false);
        message11.setVisible(false);
        message12.setVisible(false);
        message13.setVisible(false);
        message14.setVisible(false);
        message15.setVisible(false);
        message16.setVisible(false);
        message17.setVisible(false);
        message18.setVisible(false);
        message19.setVisible(false);
        message20.setVisible(false);
        message21.setVisible(false);
        message22.setVisible(false);
        message23.setVisible(false);
        message24.setVisible(false);
        message25.setVisible(false);
        message26.setVisible(false);
        message27.setVisible(false);
        message28.setVisible(false);
        messageNameLabel1.setText("xddd");
        messageLabel1.setText("xddddddd)");
    }

    private void getMessagesFromDB() throws IOException {
        message.sendGetMessagesMessage(SendToServer, Client.clientId  + "," + Client.user_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        String[] messagesData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

//        for (int i = 0; i < messagesData.length; i++) {
//            String[] messageData = messagesData[i].split(", ");
//
//            switch (i) {
//                case 0:
//                    clinicNameLabel1.setText(clinicData[2]);
//                    descriptionClinicLabel1.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
//                    break;
//                default:
//                    System.out.println(Color.ColorString("Error while showing messages.", Color.ANSI_RED));
//                    break;
//            }
//        }
    }
}
