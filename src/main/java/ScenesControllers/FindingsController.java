package ScenesControllers;

import Client.Client;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Color;
import utils.Message;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Color.greenGradient;
import static utils.Color.redGradient;

public class FindingsController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private GridPane gridPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button userPanelButton;

    @FXML
    private ImageView previewImageView;

    @FXML
    private Button saveButton;

    @FXML
    private Pane imagePane;

    @FXML
    private Button printButton;

    @FXML
    private Label buttonStatusLabel;

    private PDDocument document = null;

    private Pane selectedFinding = null;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FindingsController.ReadFromServer = Client.ReadFromServer;
        FindingsController.SendToServer = Client.SendToServer;

        saveButton.setVisible(false);
        printButton.setVisible(false);

        try {
            getFindingsFromDB();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void resetButtonStatus() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(2000), TimeEvent -> {
                    buttonStatusLabel.setText("");
                }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void generatePDF(String findingId, String findingName, String findingDate) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println(scrollPane.getBackground() + " " + scrollPane.getBackground().toString() + " " + scrollPane.getBackground().getFills());
//        //Color backgroundColor = (Color) scrollPane.getStyle()
//       // System.out.println("Background color: " + backgroundColor.toString());
        imagePane.setOpacity(1);
        try {
            // Creating new PDF
            document = new PDDocument();

            // Adding new page to PDF
            PDPage page = new PDPage();
            document.addPage(page);

            // Creating new page content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDType0Font font1 = PDType0Font.load(document, new File("src/main/resources/Fonts/calibri.ttf"));

            int fontSize = 30;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            contentStream.beginText();
            String tempText = "Finding " + findingId;
            float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 700);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 30;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            contentStream.beginText();
            tempText = findingName;
            textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 660);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.beginText();
            tempText = "Issued on " + findingDate;
            textWidth = PDType1Font.HELVETICA.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 500);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.TIMES_ROMAN, fontSize);
            contentStream.beginText();
            tempText = "Code: " + findingId;
            textWidth = PDType1Font.TIMES_ROMAN.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLine();
            contentStream.newLineAtOffset((595 - textWidth) / 2, 180);
            contentStream.showText(tempText);
            contentStream.endText();

//            fontSize = 18;
//            contentStream.setFont(font1, fontSize);
//            contentStream.beginText();
//            tempText = "by dr. " + eReferralDRFirstName + " " + eReferralDRLastName;
//            textWidth = font1.getStringWidth(tempText) / 1000 * fontSize;
//            contentStream.newLineAtOffset((595 - textWidth) / 2, 210);
//            contentStream.showText(tempText);
//            contentStream.endText();

            // Adding barcode
            int width = 400;
            int height = 50;
            //BufferedImage bufferedImage = generateBarcode(eReferralBarcode, width, height);

            //PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, bufferedImage);
            // Adding barcode to the page
            //contentStream.drawImage(pdImageXObject, 100, 580, width, height);
            //contentStream.close();

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bImage = pdfRenderer.renderImageWithDPI(0, 600);
            Image image = SwingFXUtils.toFXImage(bImage, null);

            previewImageView.setImage(image);

            // Set clip to cut image outside pane
            previewImageView.setClip(new Rectangle(600, 650));

            // Save button clicked
            saveButton.setOnMouseClicked(mouseEvent -> {
                try {
                    if (document != null) {
                        document.save("src/main/resources/PDFs/" + "finding" + findingId + ".pdf");

                        buttonStatusLabel.setTextFill(greenGradient());
                        System.out.println("Finding" + findingId + " saved correctly.");
                        buttonStatusLabel.setText("Finding " + findingId + " saved correctly");
                        resetButtonStatus();
                    } else {
                        buttonStatusLabel.setTextFill(redGradient());
                        buttonStatusLabel.setText("Error while saving finding " + findingId + ", document is null!");
                        System.out.println(Color.ColorString("Error while saving finding " + findingId + ", document is null!", Color.ANSI_RED));
                        resetButtonStatus();
                    }
                } catch (IOException e) {
                    buttonStatusLabel.setTextFill(redGradient());
                    buttonStatusLabel.setText("Error while saving finding " + findingId + "!");
                    System.out.println(Color.ColorString("Error while saving finding " + findingId + "!", Color.ANSI_RED));
                    resetButtonStatus();
                    throw new RuntimeException(e);
                }
            });

            // Print button clicked
            printButton.setOnMouseClicked(mouseEvent -> {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    boolean printDialog = job.showPrintDialog(null);

                    if (printDialog) {
                        boolean success = job.printPage(previewImageView); // Print imageView

                        if (success) {
                            job.endJob();

                            buttonStatusLabel.setTextFill(greenGradient());
                            System.out.println("Finding" + findingId + " printed correctly.");
                            buttonStatusLabel.setText("Finding " + findingId + " printed correctly");
                            resetButtonStatus();
                        } else {
                            buttonStatusLabel.setTextFill(redGradient());
                            buttonStatusLabel.setText("Error while printing Finding " + findingId + "!");
                            System.out.println(Color.ColorString("Error while printing Finding " + findingId + "!", Color.ANSI_RED));
                            resetButtonStatus();
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFindingsFromDB() throws IOException {
        message.sendGetFindingsMessage(SendToServer, Client.clientId  + "#/#" + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if(serverAnswer.equals("[[No findings in database]]")) {
            Pane newFinding = new Pane();
            Label newFindingTitle = new Label("There is no findings.");
            newFindingTitle.setPrefHeight(200);
            newFindingTitle.setPrefWidth(817);
            newFinding.setMinWidth(817);
            newFindingTitle.setAlignment(Pos.CENTER);
            newFindingTitle.setFont(new Font("Consolas Bold", 35.0));
            newFinding.getChildren().add(newFindingTitle);
            gridPane.add(newFinding, 0, 0);
        } else {
            String[] findingsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < findingsData.length; i++) {
                String[] findingData = findingsData[i].split(", ");

                VBox newFinding = new VBox();
                newFinding.setFocusTraversable(false);
                gridPane.setFocusTraversable(false);
                scrollPane.setFocusTraversable(false);
                newFinding.setPrefHeight(200);
                newFinding.setId("finding" + i);
                newFinding.setOnMouseClicked(mouseEvent -> {
                    saveButton.requestFocus();
                    saveButton.setVisible(true);
                    printButton.setVisible(true);
                    generatePDF(findingData[0], findingData[2], findingData[4]);
                });

                // Set fitting to scroll bar
                if(findingsData.length > 9) {
                    newFinding.setPrefWidth(268);
                    newFinding.setMinWidth(268);
                    newFinding.setMaxWidth(268);
                } else {
                    newFinding.setPrefWidth(272);
                    newFinding.setMinWidth(272);
                    newFinding.setMaxWidth(272);
                }

                setColors(newFinding);

                Label newFindingTitle = new Label(findingData[2]);
                Label newFindingId = new Label("ID: " + findingData[0]);
//                Label newEReferralBarcode = new Label("Barcode:\n" + findingData[1]);
                Label newFindingDate = new Label("Date of issue: " + findingData[2]);
//                Label newEReferralDoctor = new Label("dr. " + findingData[7] + " " + findingData[8]);

                newFindingTitle.setAlignment(Pos.CENTER);
                newFindingTitle.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                newFindingTitle.setPrefHeight(50);
                newFindingTitle.setMinHeight(50);
                newFindingTitle.setPrefWidth(268);
                newFindingTitle.setFont(new Font("Consolas Bold", 20.0));
                newFindingTitle.setWrapText(true); // Text wrapping

                newFindingId.setPrefHeight(14);
                newFindingId.setPadding(new Insets(0, 0, 0, 10));
                newFindingId.setFont(new Font("Consolas", 12.0));

//                newEReferralBarcode.setMinHeight(24);
//                newEReferralBarcode.setPadding(new Insets(10, 0, 0, 10));
//                newEReferralBarcode.setFont(new Font("Consolas", 10.0));
//
                newFindingDate.setPrefWidth(268);
                newFindingDate.setAlignment(Pos.CENTER);
                newFindingDate.setPrefHeight(20);
                newFindingDate.setPadding(new Insets(10, 0, 0, 0));
                newFindingDate.setFont(new Font("Consolas", 18.0));
//
//                newEReferralDoctor.setPrefWidth(268);
//                newEReferralDoctor.setAlignment(Pos.CENTER);
//                newEReferralDoctor.setPrefHeight(26);
//                newEReferralDoctor.setPadding(new Insets(20, 0, 0, 0));
//                newEReferralDoctor.setFont(new Font("Consolas Italic", 18.0));

                newFinding.getChildren().addAll(newFindingTitle, newFindingId, newFindingDate);

                // Add new EReferral to the GridPane on the appropriate row and column
                gridPane.add(newFinding, i % 3, i / 3);
            }
        }
    }

    void setColors(Pane newFinding) {
        newFinding.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");

        newFinding.setOnMouseEntered(mouseEvent -> {
            if (newFinding != selectedFinding) {
                newFinding.setStyle("-fx-background-color: #e6e6e6; -fx-border-radius: 10; -fx-border-color: #edae55; -fx-border-width: 4");
            }
        });

        newFinding.setOnMouseExited(mouseEvent -> {
            if (newFinding != selectedFinding) {
                newFinding.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }
        });

        newFinding.setOnMousePressed(mouseEvent -> {
            if (selectedFinding != null) {

                selectedFinding.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 1");
            }


            selectedFinding = newFinding;


            newFinding.setStyle("-fx-background-color: #f2f2f2; -fx-border-radius: 10; -fx-border-color: #00FF00; -fx-border-width: 4");
        });
    }
}
