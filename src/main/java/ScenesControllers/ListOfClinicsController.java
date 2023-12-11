package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.layout.AnchorPane;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class ListOfClinicsController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private AnchorPane listOfClinicsScene;

    @FXML
    private Label clinicNameLabel1;

    @FXML
    private Label descriptionClinicLabel1;

    @FXML
    private Label clinicNameLabel2;

    @FXML
    private Label descriptionClinicLabel2;

    @FXML
    private Label clinicNameLabel3;

    @FXML
    private Label descriptionClinicLabel3;

    @FXML
    private Label clinicNameLabel4;

    @FXML
    private Label descriptionClinicLabel4;

    @FXML
    private Label clinicNameLabel5;

    @FXML
    private Label descriptionClinicLabel5;

    @FXML
    private Label clinicNameLabel6;

    @FXML
    private Label descriptionClinicLabel6;

    @FXML
    private Label clinicNameLabel7;

    @FXML
    private Label descriptionClinicLabel7;

    @FXML
    private Label clinicNameLabel8;

    @FXML
    private Label descriptionClinicLabel8;

    @FXML
    private Label clinicNameLabel9;

    @FXML
    private Label descriptionClinicLabel9;

    @FXML
    private Label clinicNameLabel10;

    @FXML
    private Label descriptionClinicLabel10;

    @FXML
    private WebView mapWebView;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @FXML
    public void testScheduleButtonClicked(ActionEvent actionEvent) throws IOException{
         new SceneSwitch("/ScenesLayout/ExaminationScheduleScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListOfClinicsController.ReadFromServer = Client.ReadFromServer;
        ListOfClinicsController.SendToServer = Client.SendToServer;

        try {
            getClilincsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WebEngine webEngine = mapWebView.getEngine();
        webEngine.load("https://www.google.com/maps/d/u/2/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&usp=sharing");
    }

    private void getClilincsFromDB() throws IOException {
        message.sendGetClinicsMessage( SendToServer,"" + Client.clientId);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        String[] clinicsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

        for (int i = 0; i < clinicsData.length; i++) {
            String[] clinicData = clinicsData[i].split(", ");

            switch (i) {
                case 0:
                    clinicNameLabel1.setText(clinicData[2]);
                    descriptionClinicLabel1.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 1:
                    clinicNameLabel2.setText(clinicData[2]);
                    descriptionClinicLabel2.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 2:
                    clinicNameLabel3.setText(clinicData[2]);
                    descriptionClinicLabel3.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 3:
                    clinicNameLabel4.setText(clinicData[2]);
                    descriptionClinicLabel4.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 4:
                    clinicNameLabel5.setText(clinicData[2]);
                    descriptionClinicLabel5.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 5:
                    clinicNameLabel6.setText(clinicData[2]);
                    descriptionClinicLabel6.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 6:
                    clinicNameLabel7.setText(clinicData[2]);
                    descriptionClinicLabel7.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 7:
                    clinicNameLabel8.setText(clinicData[2]);
                    descriptionClinicLabel8.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 8:
                    clinicNameLabel9.setText(clinicData[2]);
                    descriptionClinicLabel9.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                case 9:
                    clinicNameLabel10.setText(clinicData[2]);
                    descriptionClinicLabel10.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinicID: " + clinicData[0]);
                    break;
                default:
                    System.out.println(Color.ColorString("Error while showing clinics.", Color.ANSI_RED));
                    break;
            }
        }
    }
}
