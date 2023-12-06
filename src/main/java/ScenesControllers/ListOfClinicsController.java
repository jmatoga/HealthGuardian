package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.layout.AnchorPane;
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
    private WebView mapWebView;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("ClientPanelScene.fxml");
    }

    @FXML
    public void testScheduleButtonClicked(ActionEvent actionEvent) throws IOException{
         new SceneSwitch("ExaminationScheduleScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListOfClinicsController.ReadFromServer = Client.ReadFromServer;
        ListOfClinicsController.SendToServer = Client.SendToServer;
        WebEngine webEngine = mapWebView.getEngine();
        webEngine.load("https://www.google.com/maps/d/embed?mid=1HLLCItcY4KPaE8gYwBNYODyiSOAXCX8&hl=pl&ehbc=2E312F");
    }

}
