package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class EContactController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private Pane copyButton;

    @FXML
    private Label DoctorFirstNameLabel;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @FXML
    public void testScheduleButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/ExaminationScheduleScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EContactController.ReadFromServer = Client.ReadFromServer;
        EContactController.SendToServer = Client.SendToServer;

    }
}
