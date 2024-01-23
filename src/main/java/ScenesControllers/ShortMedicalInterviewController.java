package ScenesControllers;

import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ShortMedicalInterviewController implements Initializable {

    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    public Button userPanelButton;
    @FXML
    public Pane startSmiPane;
    @FXML
    public Button startSmiButton;
    @FXML
    public Pane smiStage1;
    @FXML
    public Button stage1nextButton;
    @FXML
    public RadioButton throatChoice;
    @FXML
    public RadioButton headChoice;
    @FXML
    public RadioButton eyesChoice;
    @FXML
    public RadioButton earsChoice;
    @FXML
    public RadioButton teethChoice;
    @FXML
    public RadioButton neckChoice;
    @FXML
    public RadioButton chestChoice;
    @FXML
    public RadioButton stomachChoice;
    @FXML
    public RadioButton pelvisChoice;
    @FXML
    public RadioButton armChoice;
    @FXML
    public RadioButton legChoice;
    @FXML
    public RadioButton backChoice;
    @FXML
    public RadioButton skinChoice;
    @FXML
    public RadioButton lymphNodesChoice;
    @FXML
    public RadioButton otherAilmentsChoice;
    @FXML
    public Pane smiStage2;
    @FXML
    public Button stage2nextButton;
    @FXML
    public RadioButton answer21Choice;
    @FXML
    public RadioButton answer22Choice;
    @FXML
    public RadioButton answer23Choice;
    @FXML
    public RadioButton answer24Choice;
    @FXML
    public RadioButton answer25Choice;
    @FXML
    public RadioButton answer26Choice;
    @FXML
    public Pane smiStage3;
    @FXML
    public Button stage3nextButton;
    @FXML
    public RadioButton runnyNoseChoice;
    @FXML
    public RadioButton coughChoice;
    @FXML
    public RadioButton dyspnoeaChoice;
    @FXML
    public RadioButton swoonChoice;
    @FXML
    public RadioButton diarrheaChoice;
    @FXML
    public RadioButton nosebleedChoice;
    @FXML
    public RadioButton tickBiteChoice;
    @FXML
    public RadioButton feverChoice;
    @FXML
    public RadioButton psychoChoice;
    @FXML
    public RadioButton pregnancyChoice;
    @FXML
    public RadioButton otherAilments3StageChoice;
    @FXML
    public Pane smiStage4;
    @FXML
    public Button stage4nextButton;
    @FXML
    public RadioButton answer41Choice;
    @FXML
    public RadioButton answer42Choice;
    @FXML
    public RadioButton answer43Choice;
    @FXML
    public Pane smiStage5;
    @FXML
    public Button stage5nextButton;
    @FXML
    public TextArea medicinesTextFiled;
    @FXML
    public Pane smiStage6;
    @FXML
    public Button stage6nextButton;
    @FXML
    public TextArea painDurationTextField;
    @FXML
    public Pane smiStage7;
    @FXML
    public Button stage7nextButton;
    @FXML
    public TextArea extentOfThePainTextField;
    @FXML
    public Pane smiStage8;
    @FXML
    public Button stage8nextButton;
    @FXML
    public Pane smiStage9;
    @FXML
    public Button endtButton;
    @FXML
    public TextArea extentOfThePainTextField1;
    @FXML
    public TextField currentTemperatureTextFiled;
    @FXML
    public TextArea additionalDescriptionTextField;
    @FXML
    public Label doctorResultLabel;
    @FXML
    public Pane resultSmiPane;
    @FXML
    public Button askForAReferralButton;
    @FXML
    public Label smiCodeLabel;
    @FXML
    public ProgressBar ProgressProgressBar;
    @FXML
    public ProgressIndicator progressProgressIndicator;
    public int firstStageNum = 0;
    public int secondStageNum = 0;
    public int thirdStageNum = 0;
    public int fourthStageNum = 0;

    public String whatHurtsYouToDB = "";
    public String painSymptomsToDB = "";
    public String otherSymptomsToDB = "";
    public String symptomsOtherSymptomsToDB = "";
    public String medicinesToDB = "";
    public String extentOfPainToDB = "";
    public String whenThePainStartedToDB = "";
    public String temperatureToDB = "";
    public String additionalDescriptionToDB = "";
    public String resultSmiToDB = "";
    public String SMIcode = "";

    @FXML
    public void userPanelButtonClicked(ActionEvent actionEvent) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    public void saveStage1(RadioButton radioButton)
    {
        whatHurtsYouToDB = radioButton.getText();
    }

    public void saveStage2(RadioButton radioButton)
    {
        painSymptomsToDB = radioButton.getText();
    }

    public void saveStage3(RadioButton radioButton)
    {
        otherSymptomsToDB = radioButton.getText();
    }

    public void saveStage4(RadioButton radioButton)
    {
        symptomsOtherSymptomsToDB = radioButton.getText();
    }

    public void show()
    {
        System.out.printf(whatHurtsYouToDB + painSymptomsToDB + otherSymptomsToDB + symptomsOtherSymptomsToDB + medicinesToDB + extentOfPainToDB + whenThePainStartedToDB + temperatureToDB + additionalDescriptionToDB + resultSmiToDB);
    }

    @FXML
    public void startButtonClicked(ActionEvent actionEvent) throws IOException {
        startSmiPane.setOpacity(0);
        smiStage1.setOpacity(1);
        smiStage1.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage2.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage7.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage1nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.14);
    }

    @FXML
    public void stage1nextButtonClicked(ActionEvent actionEvent) {

        if(firstStageNum == 4 || firstStageNum == 14) {
            smiStage1.setOpacity(0);
            smiStage5.setOpacity(1);
            smiStage5.setDisable(false);
            startSmiPane.setDisable(true);
            smiStage1.setDisable(true);
            smiStage3.setDisable(true);
            smiStage4.setDisable(true);
            smiStage2.setDisable(true);
            smiStage6.setDisable(true);
            smiStage7.setDisable(true);
            smiStage8.setDisable(true);
            smiStage9.setDisable(true);
            resultSmiPane.setDisable(true);
            stage5nextButton.setDisable(true);
            ProgressProgressBar.setProgress(0.30);

        } else if (firstStageNum == 15) {
            smiStage1.setOpacity(0);
            smiStage3.setOpacity(1);
            smiStage3.setDisable(false);
            startSmiPane.setDisable(true);
            smiStage1.setDisable(true);
            smiStage5.setDisable(true);
            smiStage4.setDisable(true);
            smiStage2.setDisable(true);
            smiStage6.setDisable(true);
            smiStage7.setDisable(true);
            smiStage8.setDisable(true);
            smiStage9.setDisable(true);
            resultSmiPane.setDisable(true);
            stage3nextButton.setDisable(true);
            ProgressProgressBar.setProgress(0.22);

        }else {
            smiStage1.setOpacity(0);
            smiStage2.setOpacity(1);
            smiStage2.setDisable(false);
            startSmiPane.setDisable(true);
            smiStage1.setDisable(true);
            smiStage3.setDisable(true);
            smiStage4.setDisable(true);
            smiStage5.setDisable(true);
            smiStage6.setDisable(true);
            smiStage7.setDisable(true);
            smiStage8.setDisable(true);
            smiStage9.setDisable(true);
            resultSmiPane.setDisable(true);
            stage2nextButton.setDisable(true);
            ProgressProgressBar.setProgress(0.22);
        }

    }

    @FXML
    public void headChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 1;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(headChoice);
    }

    @FXML
    public void eyesChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 2;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(eyesChoice);
    }

    @FXML
    public void earsChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 3;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(earsChoice);
    }

    @FXML
    public void teethChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 4;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(teethChoice);
    }

    @FXML
    public void neckChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 5;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(neckChoice);
    }

    @FXML
    public void throatChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 6;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(throatChoice);
    }

    @FXML
    public void chestChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 7;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(chestChoice);
    }

    @FXML
    public void stomachChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 8;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(stomachChoice);
    }

    @FXML
    public void pelvisChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 9;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(pelvisChoice);
    }

    @FXML
    public void armChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 10;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(armChoice);
    }

    @FXML
    public void legChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 11;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(legChoice);
    }

    @FXML
    public void backChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 12;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(backChoice);
    }

    @FXML
    public void skinChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 13;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(skinChoice);
    }

    @FXML
    public void lymphNodesChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 14;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(lymphNodesChoice);
    }

    @FXML
    public void otherAilmentsChoiceClicked(ActionEvent actionEvent) {
        firstStageNum = 15;
        smiEngine01(firstStageNum);
        stage1nextButton.setDisable(false);
        saveStage1(otherAilmentsChoice);
    }

    @FXML
    public void stage2nextButtonClicked(ActionEvent actionEvent) {
        smiStage2.setOpacity(0);
        smiStage5.setOpacity(1);
        smiStage5.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage2.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage1.setDisable(true);
        smiStage6.setDisable(true);
        smiStage7.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage5nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.30);
    }

    @FXML
    public void answer21ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 1;
        stage2nextButton.setDisable(false);
        saveStage2(answer21Choice);
    }

    @FXML
    public void answer22ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 2;
        stage2nextButton.setDisable(false);
        saveStage2(answer22Choice);
    }

    @FXML
    public void answer23ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 3;
        stage2nextButton.setDisable(false);
        saveStage2(answer23Choice);
    }

    @FXML
    public void answer24ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 4;
        stage2nextButton.setDisable(false);
        saveStage2(answer24Choice);
    }

    @FXML
    public void answer25ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 5;
        stage2nextButton.setDisable(false);
        saveStage2(answer25Choice);
    }

    @FXML
    public void answer26ChoiceClicked(ActionEvent actionEvent) {
        secondStageNum = 6;
        stage2nextButton.setDisable(false);
        saveStage2(answer26Choice);
    }

    @FXML
    public void stage3nextButtonClicked(ActionEvent actionEvent) {

        if(thirdStageNum == 1 || thirdStageNum == 2 || thirdStageNum == 3 || thirdStageNum == 5) {
            smiStage3.setOpacity(0);
            smiStage4.setOpacity(1);
            smiStage4.setDisable(false);
            startSmiPane.setDisable(true);
            smiStage3.setDisable(true);
            smiStage1.setDisable(true);
            smiStage5.setDisable(true);
            smiStage2.setDisable(true);
            smiStage6.setDisable(true);
            smiStage7.setDisable(true);
            smiStage8.setDisable(true);
            smiStage9.setDisable(true);
            resultSmiPane.setDisable(true);
            stage4nextButton.setDisable(true);
            ProgressProgressBar.setProgress(0.22);
        }else {
            smiStage3.setOpacity(0);
            smiStage5.setOpacity(1);
            smiStage5.setDisable(false);
            startSmiPane.setDisable(true);
            smiStage1.setDisable(true);
            smiStage3.setDisable(true);
            smiStage4.setDisable(true);
            smiStage2.setDisable(true);
            smiStage6.setDisable(true);
            smiStage7.setDisable(true);
            smiStage8.setDisable(true);
            smiStage9.setDisable(true);
            resultSmiPane.setDisable(true);
            stage5nextButton.setDisable(true);
            ProgressProgressBar.setProgress(0.30);
        }
    }

    @FXML
    public void runnyNoseChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 1;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(runnyNoseChoice);
    }

    @FXML
    public void coughChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 2;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(coughChoice);
    }

    @FXML
    public void dyspnoeaChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 3;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(dyspnoeaChoice);
    }

    @FXML
    public void swoonChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 4;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(swoonChoice);
    }

    @FXML
    public void diarrheaChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 5;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(diarrheaChoice);
    }

    @FXML
    public void nosebleedChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 6;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(nosebleedChoice);
    }

    @FXML
    public void tickBiteChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 7;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(tickBiteChoice);
    }

    @FXML
    public void feverChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 8;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(feverChoice);
    }

    @FXML
    public void psychoChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 9;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        otherSymptomsToDB = "MENTAL DISORDERS";
    }

    @FXML
    public void pregnacyChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 10;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(pregnancyChoice);
    }

    @FXML
    public void otherAilments3StageChoiceClicked(ActionEvent actionEvent) {
        thirdStageNum = 11;
        smiEngine02(thirdStageNum);
        stage3nextButton.setDisable(false);
        saveStage3(otherAilments3StageChoice);
    }

    @FXML
    public void stage4nextButtonClicked(ActionEvent actionEvent) {
        smiStage4.setOpacity(0);
        smiStage5.setOpacity(1);
        smiStage5.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage6.setDisable(true);
        smiStage7.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage5nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.30);
    }

    @FXML
    public void answer41ChoiceClicked(ActionEvent actionEvent) {
        fourthStageNum = 1;
        stage4nextButton.setDisable(false);
        saveStage4(answer41Choice);
    }

    @FXML
    public void answer42ChoiceClicked(ActionEvent actionEvent) {
        fourthStageNum = 2;
        stage4nextButton.setDisable(false);
        saveStage4(answer42Choice);
    }

    @FXML
    public void answer43ChoiceClicked(ActionEvent actionEvent) {
        fourthStageNum = 3;
        stage4nextButton.setDisable(false);
        saveStage4(answer43Choice);
    }

    @FXML
    public void stage5nextButtonClicked(ActionEvent actionEvent) {
        smiStage5.setOpacity(0);
        smiStage6.setOpacity(1);
        smiStage6.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage5.setDisable(true);
        smiStage7.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage6nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.44);
        medicinesToDB = medicinesTextFiled.getText().trim().replaceAll("\\s+", " ");
    }

    @FXML
    public void stage6nextButtonClicked(ActionEvent actionEvent) {
        smiStage6.setOpacity(0);
        smiStage7.setOpacity(1);
        smiStage7.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage7nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.58);
        extentOfPainToDB = painDurationTextField.getText().trim().replaceAll("\\s+", " ");
    }

    @FXML
    public void stage7nextButtonClicked(ActionEvent actionEvent) {
        smiStage7.setOpacity(0);
        smiStage8.setOpacity(1);
        smiStage8.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage7.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        stage8nextButton.setDisable(true);
        ProgressProgressBar.setProgress(0.72);
        whenThePainStartedToDB = extentOfThePainTextField.getText().trim().replaceAll("\\s+", " ");
    }

    @FXML
    public void stage8nextButtonClicked(ActionEvent actionEvent) {
        smiStage8.setOpacity(0);
        smiStage9.setOpacity(1);
        smiStage9.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage8.setDisable(true);
        smiStage7.setDisable(true);
        resultSmiPane.setDisable(true);
        endtButton.setDisable(true);
        ProgressProgressBar.setProgress(0.86);
        temperatureToDB = currentTemperatureTextFiled.getText().trim().replaceAll("\\s+", " ");
    }

    @FXML
    public void endButtonClicked(ActionEvent actionEvent) throws IOException {
        smiStage9.setOpacity(0);
        resultSmiPane.setOpacity(1);
        resultSmiPane.setDisable(false);
        startSmiPane.setDisable(true);
        smiStage1.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage2.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        smiStage7.setDisable(true);
        askForAReferralButton.setDisable(true);
        progressProgressIndicator.setOpacity(0);
        ProgressProgressBar.setProgress(1);
        doctorResult();
        additionalDescriptionToDB = additionalDescriptionTextField.getText().trim().replaceAll("\\s+", " ");
        resultSmiToDB = doctorResultLabel.getText();
        System.out.println(Client.clientId + "#/#" + whatHurtsYouToDB + "#/#" + painSymptomsToDB + "#/#" + otherSymptomsToDB + "#/#" + symptomsOtherSymptomsToDB + "#/#" + medicinesToDB + "#/#" + whenThePainStartedToDB + "#/#" + temperatureToDB + "#/#" + additionalDescriptionToDB + "#/#" + resultSmiToDB + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.user_id);
        message.addShortMedicalInterview(SendToServer, Client.clientId + "#/#" + whatHurtsYouToDB + "#/#" + painSymptomsToDB + "#/#" + otherSymptomsToDB + "#/#" + symptomsOtherSymptomsToDB + "#/#" + medicinesToDB + "#/#" + extentOfPainToDB + "#/#" + whenThePainStartedToDB + "#/#" + temperatureToDB + "#/#" + additionalDescriptionToDB + "#/#" + resultSmiToDB + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + Client.user_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

        if(serverAnswer.startsWith("SMI added correctly with nr: ")){
            SMIcode = serverAnswer.substring(29);
            smiCodeLabel.setText("Your unique SMI code: " + SMIcode);
            askForAReferralButton.setDisable(false);
        }else{
            smiCodeLabel.setText("Error in database while adding SMI.");
        }

    }

    @FXML
    public void askForAReferralButtonClicked(ActionEvent actionEvent) throws IOException {
        askForAReferralButton.setDisable(true);
        askForAReferralButton.setVisible(false);
        progressProgressIndicator.setOpacity(1);
        progressProgressIndicator.setProgress(0.2);
        message.addReferralSMI(SendToServer, Client.clientId + "#/#" + SMIcode + "#/#" + SMIcode + "#/#" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "#/#" + SMIcode + "#/#" + ("to " + resultSmiToDB + " (created by SMI)") + "#/#" + 16 + "#/#" + Client.user_id);
        progressProgressIndicator.setProgress(0.5);
        String serverAnswer = ReadFromServer.readLine();
        progressProgressIndicator.setProgress(0.7);
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        progressProgressIndicator.setProgress(1);
    }

    void smiEngine01 (int number)
    {
        switch(number)
        {
            case 1: //head
                answer21Choice.setText("Post-traumatic head pain.");
                answer22Choice.setText("Headache without additional symptoms.");
                answer23Choice.setText("Accompanying symptoms (droopy eyelids, facial or limb paresis, sensory disturbances, dizziness, vomiting, migraine pain).");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 2: //eyes
                answer21Choice.setText("Post-traumatic eyes pain.");
                answer22Choice.setText("Eyes discomfort without additional symptoms.");
                answer23Choice.setText("Visual disturbances occurred (luminous circles, flashes before the eyes, deterioration of visual acuity).");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 3: //ears
                answer21Choice.setText("I have discharge from my ears.");
                answer22Choice.setText("Ears discomfort without additional symptoms.");
                answer23Choice.setText("Hearing loss, tinnitus.");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 4: //teeth
                break;
            case 5: //neck
                answer21Choice.setText("Post-traumatic neck pain.");
                answer22Choice.setText("Neck discomfort without additional symptoms.");
                answer23Choice.setDisable(true);
                answer23Choice.setOpacity(0);
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 6: //throat
                answer21Choice.setText("I have additional symptoms (swallowing disorders, hoarseness lasting for more than 2 weeks).");
                answer22Choice.setText("Throat discomfort without additional symptoms.");
                answer23Choice.setDisable(true);
                answer23Choice.setOpacity(0);
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 7: //chest
                answer21Choice.setText("Post-traumatic chest pain.");
                answer22Choice.setText("There is retrosternal pain.");
                answer23Choice.setText("Chest discomfort without additional symptoms.");
                //TODO
//                if("TUTAJ USTAWIĆ WARUNEK POPIERANIA Z BAZY GENDER I JEŻELI KOBIETA TO")
//                {
//                    answer24Choice.setText("Breast pain.");
//                }else
//                {
//                    answer24Choice.setDisable(true);
//                    answer24Choice.setOpacity(0);
//                }
                answer24Choice.setText("Breast pain.");
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 8: //stomach
                answer21Choice.setText("Post-traumatic stomach pain.");
                answer22Choice.setText("Stomach discomfort without additional symptoms.");
                answer23Choice.setText("Additional symptoms occur (heartburn, reflux, nausea, defecation disorders)");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 9: //plevis
                answer21Choice.setText("Post-traumatic plevis pain.");
                answer22Choice.setText("Plevis discomfort without additional symptoms.");
                answer23Choice.setText("Hematuria, urination disorders.");
                answer24Choice.setText("Defecation disorders (blood in the stool, problems with defecation).");
                answer25Choice.setText("Male genital disorders.");
                answer26Choice.setText("Female genital disorders.");
                //TODO
//                if("TUTAJ USTAWIĆ WARUNEK POPIERANIA Z BAZY GENDER I JEŻELI KOBIETA TO")
//                {
//                answer25Choice.setText("Female genital disorders.");
//                answer26Choice.setDisable(true);
//                answer26Choice.setOpacity(0);
//                }else
//                {
//                answer25Choice.setText("Male genital disorders.");
//                answer26Choice.setDisable(true);
//                answer26Choice.setOpacity(0);
//                }
                break;
            case 10: //arm
                answer21Choice.setText("Post-traumatic pain in the arm without fracture.");
                answer22Choice.setText("Post-traumatic pain in the arm with suspected fracture or sprain.");
                answer23Choice.setText("Arm discomfort without additional symptoms.");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 11: //leg
                answer21Choice.setText("Post-traumatic pain in the leg without fracture.");
                answer22Choice.setText("Post-traumatic pain in the leg with suspected fracture or sprain.");
                answer23Choice.setText("Leg discomfort without additional symptoms.");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 12: //back
                answer21Choice.setText("Post-traumatic pain in the back without fracture.");
                answer22Choice.setText("Post-traumatic pain in the back with suspected fracture or sprain.");
                answer23Choice.setText("Back discomfort without additional symptoms.");
                answer24Choice.setDisable(true);
                answer24Choice.setOpacity(0);
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 13: //skin
                answer21Choice.setText("Post-traumatic skin pain (wound, burn with blisters, frostbite).");
                answer22Choice.setText("Skin discomfort without additional symptoms.");
                answer23Choice.setText("Skin changes (blisters, rash, skin lesions).");
                answer24Choice.setText("Skin lesions suspected of being allergic (redness, rash, itch).");
                answer25Choice.setDisable(true);
                answer25Choice.setOpacity(0);
                answer26Choice.setDisable(true);
                answer26Choice.setOpacity(0);
                break;
            case 14: //lymph nodes
                break;
            case 15: //other
                break;
        }

    }

    void smiEngine02 (int number) {

        switch (number) {
            case 1: //runny nose
                answer41Choice.setText("Itching of the mucous membrane, watering from the eyes.");
                answer42Choice.setText("Runny nose without additional symptoms.");
                answer43Choice.setText("Headaches when changing head position.");
                break;
            case 2: //cough
                answer41Choice.setText("Hemoptysis.");
                answer42Choice.setText("Cough without additional symptoms.");
                answer43Choice.setText("Sinus pain.");
                break;
            case 3: //dyspnoea
                answer41Choice.setText("Pain in the chest.");
                answer42Choice.setText("Dyspnoea without additional symptoms.");
                answer43Choice.setText("Wheezing and coughing.");
                break;
            case 4: //swoon
                break;
            case 5: //diarrhea
                answer41Choice.setText("Severe diarrhea.");
                answer42Choice.setText("Chronic diarrhea (abnormal stool, undigested food remains, blood in the stool, mucus in the stool).");
                answer43Choice.setDisable(true);
                answer43Choice.setOpacity(0);
                break;
            case 6: //nosebleed
                break;
            case 7: //tick bite
                break;
            case 8: //fever
                break;
            case 9: //psycho
                break;
            case 10: //pregnacy
                break;
            case 11: //other
                break;
        }
    }

    void doctorResult (){
        if((firstStageNum == 1 && secondStageNum == 1) || (firstStageNum == 2 && secondStageNum == 1) || (firstStageNum == 5 && secondStageNum == 1) || (firstStageNum == 7 && secondStageNum == 1) || (firstStageNum == 8 && secondStageNum == 1) || (firstStageNum == 9 && secondStageNum == 1) || (firstStageNum == 10 && secondStageNum == 1) || (firstStageNum == 11 && secondStageNum == 1) || (firstStageNum == 12 && secondStageNum == 1) || (firstStageNum == 13 && secondStageNum == 1))
        {
            doctorResultLabel.setText("SURGEON");
        }else if((firstStageNum == 1 && secondStageNum == 2) || (firstStageNum == 2 && secondStageNum == 2) || (firstStageNum == 3 && secondStageNum == 2) || (firstStageNum == 5 && secondStageNum == 2) || (firstStageNum == 6 && secondStageNum == 2) || (firstStageNum == 7 && secondStageNum == 3) || (firstStageNum == 8 && secondStageNum == 2) || (firstStageNum == 9 && secondStageNum == 2)  || (firstStageNum == 10 && secondStageNum == 3) || (firstStageNum == 11 && secondStageNum == 3) || (firstStageNum == 12 && secondStageNum == 3) || (firstStageNum == 13 && secondStageNum == 2) || (firstStageNum == 14) || (firstStageNum == 15 && thirdStageNum == 1 && fourthStageNum == 2) || (firstStageNum == 15 && thirdStageNum == 2 && fourthStageNum == 2) || (firstStageNum == 15 && thirdStageNum == 3 && fourthStageNum == 2) || (firstStageNum == 15 && thirdStageNum == 4) || (firstStageNum == 15 && thirdStageNum == 5 && fourthStageNum == 1) || (firstStageNum == 15 && thirdStageNum == 8) || (firstStageNum == 15 && thirdStageNum == 11))
        {
            doctorResultLabel.setText("INTERNIST");
        }else if((firstStageNum == 1 && secondStageNum == 3))
        {
            doctorResultLabel.setText("NEUROLOGIST");
        }else if((firstStageNum == 2 && secondStageNum == 3))
        {
            doctorResultLabel.setText("OPHTHALMOLOGIST");
        }else if((firstStageNum == 3 && secondStageNum == 1) || (firstStageNum == 3 && secondStageNum == 3) || (firstStageNum == 6 && secondStageNum == 1) || (firstStageNum == 15 && thirdStageNum == 1 && fourthStageNum == 3) || (firstStageNum == 15 && thirdStageNum == 2 && fourthStageNum == 3) || (firstStageNum == 15 && thirdStageNum == 6))
        {
            doctorResultLabel.setText("LARYNGOLOGIST");
        }else if(firstStageNum == 4)
        {
            doctorResultLabel.setText("DENTIST");
        }else if((firstStageNum == 7 && secondStageNum == 2) || (firstStageNum == 15 && thirdStageNum == 3 && fourthStageNum == 1))
        {
            doctorResultLabel.setText("CARDIOLOGIST");
        }else if((firstStageNum == 7 && secondStageNum == 4) || (firstStageNum == 15 && thirdStageNum == 10))
        {
            doctorResultLabel.setText("GYNECOLOGIST");
        }else if((firstStageNum == 8 && secondStageNum == 3) || (firstStageNum == 9 && secondStageNum == 6) || (firstStageNum == 15 && thirdStageNum == 5 && fourthStageNum == 2))
        {
            doctorResultLabel.setText("GASTROENTEROLOGIST");
        }else if((firstStageNum == 9 && secondStageNum == 3) || (firstStageNum == 9 && secondStageNum == 5))
        {
            doctorResultLabel.setText("UROLOGIST");
        }else if(firstStageNum == 9 && secondStageNum == 4)
        {
            doctorResultLabel.setText("PROCTOLOGIST");
        }else if((firstStageNum == 10 && secondStageNum == 2) || (firstStageNum == 11 && secondStageNum == 2) || (firstStageNum == 12 && secondStageNum == 2))
        {
            doctorResultLabel.setText("ORTHOPEDIC SURGEON");
        }else if(firstStageNum == 13 && secondStageNum == 3)
        {
            doctorResultLabel.setText("DERMATOLOGIST");
        }else if((firstStageNum == 13 && secondStageNum == 4) || (firstStageNum == 15 && thirdStageNum == 1 && fourthStageNum == 1))
        {
            doctorResultLabel.setText("ALLERGOLOGIST");
        }else if(firstStageNum == 15 && thirdStageNum == 9)
        {
            doctorResultLabel.setText("PSYCHIATRIST");
        }else if((firstStageNum == 15 && thirdStageNum == 2 && fourthStageNum == 1) || (firstStageNum == 15 && thirdStageNum == 3 && fourthStageNum == 3))
        {
            doctorResultLabel.setText("PULLMONOLOGIST");
        }else if(firstStageNum == 15 && thirdStageNum == 7)
        {
            doctorResultLabel.setText("SPECIALIST IN INFECTIOUS DISEASES");
        }
        doctorResultLabel.setAlignment(Pos.CENTER);
    }

    void start()
    {
        startSmiPane.setOpacity(1);
        startSmiPane.setDisable(false);
        smiStage1.setDisable(true);
        smiStage2.setDisable(true);
        smiStage3.setDisable(true);
        smiStage4.setDisable(true);
        smiStage5.setDisable(true);
        smiStage6.setDisable(true);
        smiStage7.setDisable(true);
        smiStage8.setDisable(true);
        smiStage9.setDisable(true);
        resultSmiPane.setDisable(true);
        ProgressProgressBar.setStyle("-fx-accent: #72db48;");
        smiCodeLabel.setAlignment(Pos.CENTER);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ShortMedicalInterviewController.ReadFromServer = Client.ReadFromServer;
        ShortMedicalInterviewController.SendToServer = Client.SendToServer;

        start();

        ToggleGroup group1 = new ToggleGroup();
        headChoice.setToggleGroup(group1);
        throatChoice.setToggleGroup(group1);
        headChoice.setToggleGroup(group1);
        eyesChoice.setToggleGroup(group1);
        earsChoice.setToggleGroup(group1);
        teethChoice.setToggleGroup(group1);
        neckChoice.setToggleGroup(group1);
        chestChoice.setToggleGroup(group1);
        stomachChoice.setToggleGroup(group1);
        pelvisChoice.setToggleGroup(group1);
        armChoice.setToggleGroup(group1);
        legChoice.setToggleGroup(group1);
        backChoice.setToggleGroup(group1);
        skinChoice.setToggleGroup(group1);
        lymphNodesChoice.setToggleGroup(group1);
        otherAilmentsChoice.setToggleGroup(group1);

        ToggleGroup group2 = new ToggleGroup();
        answer21Choice.setToggleGroup(group2);
        answer22Choice.setToggleGroup(group2);
        answer23Choice.setToggleGroup(group2);
        answer24Choice.setToggleGroup(group2);
        answer25Choice.setToggleGroup(group2);
        answer26Choice.setToggleGroup(group2);

        ToggleGroup group3 = new ToggleGroup();
        runnyNoseChoice.setToggleGroup(group3);
        coughChoice.setToggleGroup(group3);
        dyspnoeaChoice.setToggleGroup(group3);
        swoonChoice.setToggleGroup(group3);
        diarrheaChoice.setToggleGroup(group3);
        nosebleedChoice.setToggleGroup(group3);
        tickBiteChoice.setToggleGroup(group3);
        feverChoice.setToggleGroup(group3);
        psychoChoice.setToggleGroup(group3);
        pregnancyChoice.setToggleGroup(group3);
        otherAilments3StageChoice.setToggleGroup(group3);

        ToggleGroup group4 = new ToggleGroup();
        answer41Choice.setToggleGroup(group4);
        answer42Choice.setToggleGroup(group4);
        answer43Choice.setToggleGroup(group4);

        medicinesTextFiled.textProperty().addListener((observable, oldValue, newValue) -> {
            stage5nextButton.setDisable(newValue.length() <= 1 || newValue.length() > 500);
        });

        painDurationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            stage6nextButton.setDisable(newValue.length() <= 1 || newValue.length() > 255);
        });

        extentOfThePainTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            stage7nextButton.setDisable(newValue.length() <= 1 || newValue.length() > 255);
        });

        currentTemperatureTextFiled.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!currentTemperatureTextFiled.getText().matches("\\d*\\.?\\d+") || Float.parseFloat(currentTemperatureTextFiled.getText()) >= 50.0 || Float.parseFloat(currentTemperatureTextFiled.getText()) <= 20.0) {
                stage8nextButton.setDisable(true);
            }else {
                stage8nextButton.setDisable(!(newValue.length() == 4));
            }
        });

        additionalDescriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            endtButton.setDisable(newValue.length() <= 1 || newValue.length() > 1000);
        });
    }
}
