package ScenesControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Label settingsDescription1Label;

    @FXML
    private Label settingsDescription2Label;

    @FXML
    private Label settingsDescription3Label;

    @FXML
    private Label settingsDescription4Label;

    @FXML
    private Label settingsDescription5Label;

    @FXML
    private Label settingsDescription6Label;

    @FXML
    private Label settingsDescription7Label;

    @FXML
    private Label settingsDescription8Label;

    @FXML
    private Label settingsDescription9Label;

    @FXML
    private Label settingsDescription10Label;

    @FXML
    private CheckBox settingsCheckBox1;

    @FXML
    private CheckBox settingsCheckBox2;

    @FXML
    private CheckBox settingsCheckBox3;

    @FXML
    private CheckBox settingsCheckBox4;

    @FXML
    private CheckBox settingsCheckBox5;

    @FXML
    private CheckBox settingsCheckBox6;

    @FXML
    private CheckBox settingsCheckBox7;

    @FXML
    private CheckBox settingsCheckBox8;

    @FXML
    private CheckBox settingsCheckBox9;

    @FXML
    private CheckBox settingsCheckBox10;

    @FXML
    private Button userPanelButton;

    @FXML
    private AnchorPane settingsScene;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("ClientPanelScene.fxml");
    }


}
