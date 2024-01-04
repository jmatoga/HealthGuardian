package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import utils.Color;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalHistoryController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    private List<List<String>> content = new ArrayList<>();

    @FXML
    private GridPane gridPane;

    @FXML
    private Button userPanelButton;

    @FXML
    private Label downloadStatusLabel;

    @FXML
    public void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @FXML
    public void downloadButtonClicked(ActionEvent event) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;

            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            float tableHeight = page.getMediaBox().getHeight() - (2 * margin);

            float rowHeight = 20f;
            float tableYPosition = yStart;

            for (List<String> row : content) {
                float tableXPosition = margin;
                for (String cell : row) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(tableXPosition, tableYPosition);
                    contentStream.showText(cell);
                    contentStream.endText();
                    tableXPosition += tableWidth / row.size();
                }
                tableYPosition -= rowHeight;
            }

            contentStream.close();
            document.save("src/main/resources/PDFs/" + "medicalHistory.pdf");
            document.close();
            downloadStatusLabel.setTextFill(Color.greenGradient());
            downloadStatusLabel.setText("Medical history downloaded correctly");
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(1300), TimeEvent -> {
                        try {
                            downloadStatusLabel.setText("");
                            new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            timeline.setCycleCount(1);
            timeline.play();

            System.out.println("Medical history downloaded correctly.");
        } catch (IOException e) {
            downloadStatusLabel.setTextFill(Color.redGradient());
            downloadStatusLabel.setText("Error while downloading medical history!");
            System.out.println(Color.ColorString("Error downloading medical history: " + e.getMessage(), Color.ANSI_RED));
        }
    }

    private void getMedicalHistoryFromDB() throws IOException {
        message.sendGetMedicalHistoryMessage(SendToServer, Client.clientId + "," + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No medical history in database]]")) {
            Pane newMedicalHistory = new Pane();
            Label newMedicalHistoryTitle = new Label("There is no medical history.");
            newMedicalHistoryTitle.setPrefHeight(120);
            newMedicalHistoryTitle.setPrefWidth(1334);
            newMedicalHistoryTitle.setLayoutX(14);
            newMedicalHistoryTitle.setAlignment(Pos.CENTER);
            newMedicalHistoryTitle.setFont(new Font("Consolas Bold", 50.0));
            newMedicalHistory.getChildren().add(newMedicalHistoryTitle);
            gridPane.add(newMedicalHistory, 0, 0);
        } else {
            String[] medicalHistoriesData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < medicalHistoriesData.length; i++) {
                String[] medicalHistoryData = medicalHistoriesData[i].split(", ");

                // be ready to generate pdf without checking database again
                content.add(List.of(medicalHistoryData[0] + medicalHistoryData[1], medicalHistoryData[2]));

                Pane newMedicalHistory = new Pane();
                Label newMedicalHistoryTitle = new Label(medicalHistoryData[0] + medicalHistoryData[1]);
                Label newMedicalHistoryContent = new Label(medicalHistoryData[2]);

                newMedicalHistoryTitle.setPrefHeight(40);
                // Set fitting to scroll bar
                if (medicalHistoriesData.length > 6) {
                    newMedicalHistoryTitle.setPrefWidth(1321);
                    newMedicalHistoryContent.setPrefWidth(1321);
                } else {
                    newMedicalHistoryTitle.setPrefWidth(1334);
                    newMedicalHistoryContent.setPrefWidth(1334);
                }

                newMedicalHistoryTitle.setLayoutX(14);
                newMedicalHistoryTitle.setFont(new Font("Consolas Bold", 36.0));
                newMedicalHistoryContent.setPrefHeight(120);
                newMedicalHistoryContent.setLayoutX(14);
                newMedicalHistoryContent.setPadding(new Insets(40, 0, 0, 0));
                newMedicalHistoryContent.setWrapText(true); // Text wrapping
                newMedicalHistoryContent.setFont(new Font("Consolas", 28.0));
                newMedicalHistory.getChildren().addAll(newMedicalHistoryTitle, newMedicalHistoryContent);

                // Add new notification to the GridPane on the appropriate row
                gridPane.add(newMedicalHistory, 0, i);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MedicalHistoryController.ReadFromServer = Client.ReadFromServer;
        MedicalHistoryController.SendToServer = Client.SendToServer;

        content.add(List.of("ICD10 Code:", "Description:"));
        content.add(List.of("", ""));
        try {
            getMedicalHistoryFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
