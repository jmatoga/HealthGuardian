package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorPanelController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    public void messageButtonClicked(ActionEvent actionEvent) {
    }

    public void settingsButtonClicked(ActionEvent actionEvent) {
    }

    public void logOutButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    public void patientButtonClicked(ActionEvent actionEvent) {
    }

    public void testScheduleButtonClicked(ActionEvent actionEvent) {
    }

    public void eContactButtonClicked(ActionEvent actionEvent) {
    }

    public void listOfClinicsButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/listOfClinicsScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorPanelController.ReadFromServer = Client.ReadFromServer;
        DoctorPanelController.SendToServer = Client.SendToServer;
    }
}
