package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    private Pane ClinicPane1, ClinicPane2, ClinicPane3, ClinicPane4, ClinicPane5, ClinicPane6, ClinicPane7, ClinicPane8, ClinicPane9, ClinicPane10;

    @FXML
    private Label clinicNameLabel1, clinicNameLabel2, clinicNameLabel3, clinicNameLabel4, clinicNameLabel5, clinicNameLabel6, clinicNameLabel7, clinicNameLabel8, clinicNameLabel9, clinicNameLabel10;

    @FXML
    private Label descriptionClinicLabel1, descriptionClinicLabel2, descriptionClinicLabel3, descriptionClinicLabel4, descriptionClinicLabel5, descriptionClinicLabel6, descriptionClinicLabel7, descriptionClinicLabel8, descriptionClinicLabel9, descriptionClinicLabel10;

    @FXML
    private WebView mapWebView;

    private WebEngine webEngine;

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

        webEngine = mapWebView.getEngine();
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
                    descriptionClinicLabel1.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane1.setOnMouseClicked(mouseEvent -> {
                        webEngine.load("https://www.google.com/maps/d/u/7/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&ll=50.04633907911461%2C19.94378549021035&z=20");
                    });
                    break;
                case 1:
                    clinicNameLabel2.setText(clinicData[2]);
                    descriptionClinicLabel2.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane2.setOnMouseClicked(mouseEvent -> {
                        webEngine.load("https://www.google.com/maps/d/u/7/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&ll=50.0723909741866%2C19.98003652244043&z=20");
                    });
                    break;
                case 2:
                    clinicNameLabel3.setText(clinicData[2]);
                    descriptionClinicLabel3.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane3.setOnMouseClicked(mouseEvent -> {
                        webEngine.load("https://www.google.com/maps/d/u/7/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&ll=50.08881526021777%2C19.941395459489815&z=20");
                    });
                    break;
                case 3:
                    clinicNameLabel4.setText(clinicData[2]);
                    descriptionClinicLabel4.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane4.setOnMouseClicked(mouseEvent -> {
                        webEngine.load("https://www.google.com/maps/d/u/7/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&ll=50.040960984375445%2C19.99704020859832&z=20");
                    });
                    break;
                case 4:
                    clinicNameLabel5.setText(clinicData[2]);
                    descriptionClinicLabel5.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane5.setOnMouseClicked(mouseEvent -> {
                        webEngine.load("https://www.google.com/maps/d/u/7/edit?mid=1ylOsb6r5D450-AW_eOlIq_Rfl3p9fA4&ll=50.07061504567112%2C19.93404685029718&z=20");
                    });
                    break;
                case 5:
                    clinicNameLabel6.setText(clinicData[2]);
                    descriptionClinicLabel6.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane6.setOnMouseClicked(mouseEvent -> {
                        //webEngine.load("");
                    });
                    break;
                case 6:
                    clinicNameLabel7.setText(clinicData[2]);
                    descriptionClinicLabel7.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane7.setOnMouseClicked(mouseEvent -> {
                        //webEngine.load("");
                    });
                    break;
                case 7:
                    clinicNameLabel8.setText(clinicData[2]);
                    descriptionClinicLabel8.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane8.setOnMouseClicked(mouseEvent -> {
                        //webEngine.load("");
                    });
                    break;
                case 8:
                    clinicNameLabel9.setText(clinicData[2]);
                    descriptionClinicLabel9.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane9.setOnMouseClicked(mouseEvent -> {
                        //webEngine.load("");
                    });
                    break;
                case 9:
                    clinicNameLabel10.setText(clinicData[2]);
                    descriptionClinicLabel10.setText(clinicData[2] + " is located at " + clinicData[3] + "\nin " + clinicData[4] + " city. Phone number: " + clinicData[5] + "\nClinic Number: " + clinicData[1]);
                    ClinicPane10.setOnMouseClicked(mouseEvent -> {
                       // webEngine.load("");
                    });
                    break;
                default:
                    System.out.println(Color.ColorString("Error while showing clinics.", Color.ANSI_RED));
                    break;
            }
        }
    }
}
