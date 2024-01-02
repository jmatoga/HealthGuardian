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

public class DoctorUserController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    public void doctorPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/DoctorPanelScene.fxml");
    }

    public void changeMedicalHistoryButtonClicked(ActionEvent actionEvent) {
    }

    public void prescribeEPrescriptionButtonClicked(ActionEvent actionEvent) {
    }

    public void prescribeEReferralButtonClicked(ActionEvent actionEvent) {
    }

    public void checkFindingsButtonClicked(ActionEvent actionEvent) {
    }

    public void addRecommendationsButtonClicked(ActionEvent actionEvent) {
    }

    public void checkDocumentationButtonClicked(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DoctorUserController.ReadFromServer = Client.ReadFromServer;
        DoctorUserController.SendToServer = Client.SendToServer;
    }
}
