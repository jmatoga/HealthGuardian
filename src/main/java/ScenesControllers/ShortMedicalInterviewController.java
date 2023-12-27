package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class ShortMedicalInterviewController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    public Button userPanelButton;
    public Pane startSmiPane;
    public Button startSmiButton;
    public Pane smiStage1;
    public Button stage1nextButton;
    public RadioButton throatChoice;
    public RadioButton headChoice;
    public RadioButton eyesChoice;
    public RadioButton earsChoice;
    public RadioButton teethChoice;
    public RadioButton neckChoice;
    public RadioButton chestChoice;
    public RadioButton stomachChoice;
    public RadioButton pelvisChoice;
    public RadioButton armChoice;
    public RadioButton legChoice;
    public RadioButton backChoice;
    public RadioButton skinChoice;
    public RadioButton lymphNodesChoice;
    public RadioButton otherAilmentsChoice;
    public Pane smiStage2;
    public Button stage2nextButton;
    public RadioButton answer21Choice;
    public RadioButton answer22Choice;
    public RadioButton answer23Choice;
    public RadioButton answer24Choice;
    public RadioButton answer25Choice;
    public RadioButton answer26Choice;
    public Pane smiStage3;
    public Button stage3nextButton;
    public RadioButton runnyNoseChoice;
    public RadioButton coughChoice;
    public RadioButton dyspnoeaChoice;
    public RadioButton swoonChoice;
    public RadioButton diarrheaChoice;
    public RadioButton nosebleedChoice;
    public RadioButton tickBiteChoice;
    public RadioButton feverChoice;
    public RadioButton psychoChoice;
    public RadioButton pregnancyChoice;
    public RadioButton otherAilments3StageChoice;
    public Pane smiStage4;
    public Button stage4nextButton;
    public RadioButton answer41Choice;
    public RadioButton answer42Choice;
    public RadioButton answer43Choice;
    public Pane smiStage5;
    public Button stage5nextButton;
    public TextArea medicinesTextFiled;
    public Pane smiStage6;
    public Button stage6nextButton;
    public TextArea painDurationTextField;
    public Pane smiStage7;
    public Button stage7nextButton;
    public TextArea extentOfThePainTextField;
    public Pane smiStage8;
    public Button stage8nextButton;
    public Pane smiStage9;
    public Button endtButton;
    public TextArea extentOfThePainTextField1;
    public Pane resultSmiPane;
    public Button askForAReferralButton;


    public void userPanelButtonClicked(ActionEvent actionEvent) {
    }

    public void stage1nextButtonClicked(ActionEvent actionEvent) {
    }

    public void headChoiceClicked(ActionEvent actionEvent) {
    }

    public void eyesChoiceClicked(ActionEvent actionEvent) {
    }

    public void earsChoiceClicked(ActionEvent actionEvent) {
    }

    public void teethChoiceClicked(ActionEvent actionEvent) {
    }

    public void neckChoiceClicked(ActionEvent actionEvent) {
    }

    public void throatChoiceClicked(ActionEvent actionEvent) {
    }

    public void chestChoiceClicked(ActionEvent actionEvent) {
    }

    public void stomachChoiceClicked(ActionEvent actionEvent) {
    }

    public void pelvisChoiceClicked(ActionEvent actionEvent) {
    }

    public void armChoiceClicked(ActionEvent actionEvent) {
    }

    public void legChoiceClicked(ActionEvent actionEvent) {
    }

    public void backChoiceClicked(ActionEvent actionEvent) {
    }

    public void skinChoiceClicked(ActionEvent actionEvent) {
    }

    public void lymphNodesChoiceClicked(ActionEvent actionEvent) {
    }

    public void otherAilmentsChoiceClicked(ActionEvent actionEvent) {
    }

    public void stage2nextButtonClicked(ActionEvent actionEvent) {
    }

    public void answer21ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer22ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer23ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer24ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer25ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer26ChoiceClicked(ActionEvent actionEvent) {
    }

    public void stage3nextButtonClicked(ActionEvent actionEvent) {
    }

    public void runnyNoseChoiceClicked(ActionEvent actionEvent) {
    }

    public void coughChoiceClicked(ActionEvent actionEvent) {
    }

    public void dyspnoeaChoiceClicked(ActionEvent actionEvent) {
    }

    public void swoonChoiceClicked(ActionEvent actionEvent) {
    }

    public void diarrheaChoiceClicked(ActionEvent actionEvent) {
    }

    public void nosebleedChoiceClicked(ActionEvent actionEvent) {
    }

    public void tickBiteChoiceClicked(ActionEvent actionEvent) {
    }

    public void feverChoiceClicked(ActionEvent actionEvent) {
    }

    public void psychoChoiceClicked(ActionEvent actionEvent) {
    }

    public void pregnacyChoiceClicked(ActionEvent actionEvent) {
    }

    public void otherAilments3StageChoiceClicked(ActionEvent actionEvent) {
    }

    public void stage4nextButtonClicked(ActionEvent actionEvent) {
    }

    public void answer41ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer42ChoiceClicked(ActionEvent actionEvent) {
    }

    public void answer43ChoiceClicked(ActionEvent actionEvent) {
    }

    public void stage5nextButtonClicked(ActionEvent actionEvent) {
    }

    public void stage6nextButtonClicked(ActionEvent actionEvent) {
    }

    public void stage7nextButtonClicked(ActionEvent actionEvent) {
    }

    public void stage8nextButtonClicked(ActionEvent actionEvent) {
    }

    public void endButtonClicked(ActionEvent actionEvent) {
    }

    public void askForAReferralButtonClicked(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ShortMedicalInterviewController.ReadFromServer = Client.ReadFromServer;
        ShortMedicalInterviewController.SendToServer = Client.SendToServer;

    }
}
