package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class PressurePanelController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private LineChart<?, ?> pressureChart;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException, IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PressurePanelController.ReadFromServer = Client.ReadFromServer;
        PressurePanelController.SendToServer = Client.SendToServer;

        pressureChart.getXAxis().setLabel("Timeline");
        pressureChart.getYAxis().setLabel("Pressure");

        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data<>("1", 23));
        series.getData().add(new XYChart.Data<>("2", 123));
        series.getData().add(new XYChart.Data<>("3", 53));

        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>("1", 5));
        series1.getData().add(new XYChart.Data<>("5", 23));
        series1.getData().add(new XYChart.Data<>("8", 70));
        pressureChart.getData().addAll(series, series1);
    }
}
