package ScenesControllers;


import Client.Client;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.AnchorPane;
//import org.apache.pdfbox.Loader;
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
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Color;
import utils.Message;
import javafx.embed.swing.SwingFXUtils;


import java.awt.image.BufferedImage; // TODO
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Color.greenGradient;
import static utils.Color.redGradient;

public class EPrescriptionController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private ImageView previewImageView;

    @FXML
    private AnchorPane ePrescriptionScene;

    @FXML
    private GridPane gridPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button saveButton;

    @FXML
    private Pane imagePane;

    @FXML
    private Button printButton;

    @FXML
    private Label buttonStatusLabel;

    private PDDocument document = null;

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EPrescriptionController.ReadFromServer = Client.ReadFromServer;
        EPrescriptionController.SendToServer = Client.SendToServer;

        saveButton.setVisible(false);
        printButton.setVisible(false);

        //scrollPane.setFocusTraversable(false);
        //gridPane.setFocusTraversable(false);

        try {
            getEPrescriptionFromDB();
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

    public void generatePDF(String ePrescriptionCode, String ePrescriptionBarcode, String ePrescriptionDate, String ePrescriptionMedicines, String ePrescriptionDRFirstName, String ePrescriptionDRLastName) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
            String tempText = "E-Prescription ";
            float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 700);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.beginText();
            tempText = "Issued on " + ePrescriptionDate;
            textWidth = PDType1Font.HELVETICA.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 500);
            contentStream.showText(tempText);
            contentStream.endText();

            int textHeight = 450;
            String[] medicines = ePrescriptionMedicines.split("; ");
            for (String medicine : medicines) {
                contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                contentStream.beginText();
                contentStream.newLineAtOffset((635 - textWidth) / 2, textHeight);
                contentStream.showText(medicine);
                contentStream.endText();
                textHeight -= 30;
            }

            fontSize = 18;
            contentStream.setFont(font1, fontSize);
            contentStream.beginText();
            tempText = "by dr. " + ePrescriptionDRFirstName + " " + ePrescriptionDRLastName;
            textWidth = font1.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 210);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.TIMES_ROMAN, fontSize);
            contentStream.beginText();
            tempText = "Code: " + ePrescriptionCode;
            textWidth = PDType1Font.TIMES_ROMAN.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 180);
            contentStream.showText(tempText);
            contentStream.endText();

            // Adding barcode
            int width = 400;
            int height = 50;
            BufferedImage bufferedImage = generateBarcode(ePrescriptionBarcode, width, height);

            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, bufferedImage);
            // Adding barcode to the page
            contentStream.drawImage(pdImageXObject, 100, 600, width, height);

            contentStream.close();

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
                        document.save("src/main/resources/PDFs/" + "ePrescription" + ePrescriptionCode + ".pdf");

                        buttonStatusLabel.setTextFill(greenGradient());
                        System.out.println("ePrescription" + ePrescriptionCode + " saved correctly.");
                        buttonStatusLabel.setText("ePrescription " + ePrescriptionCode + " saved correctly");
                        resetButtonStatus();
                    } else {
                        buttonStatusLabel.setTextFill(redGradient());
                        buttonStatusLabel.setText("Error while saving ePrescription " + ePrescriptionCode + ", document is null!");
                        System.out.println(Color.ColorString("Error while saving ePrescription " + ePrescriptionCode + ", document is null!", Color.ANSI_RED));
                        resetButtonStatus();
                    }
                } catch (IOException e) {
                    buttonStatusLabel.setTextFill(redGradient());
                    buttonStatusLabel.setText("Error while saving ePrescription " + ePrescriptionCode + "!");
                    System.out.println(Color.ColorString("Error while saving ePrescription " + ePrescriptionCode + "!", Color.ANSI_RED));
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
                            System.out.println("ePrescription" + ePrescriptionCode + " printed correctly.");
                            buttonStatusLabel.setText("ePrescription " + ePrescriptionCode + " printed correctly");
                            resetButtonStatus();
                        } else {
                            buttonStatusLabel.setTextFill(redGradient());
                            buttonStatusLabel.setText("Error while printing ePrescription " + ePrescriptionCode + "!");
                            System.out.println(Color.ColorString("Error while printing ePrescription " + ePrescriptionCode + "!", Color.ANSI_RED));
                            resetButtonStatus();
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage generateBarcode(String data, int width, int height) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.CODE_128, width, height);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bufferedImage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getEPrescriptionFromDB() throws IOException {
        message.sendGetEPrescriptionMessage(SendToServer, Client.clientId + "," + Client.user_id);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("[[No EPrescriptions in database]]")) {
            Pane newEPrescription = new Pane();
            Label newEPrescriptionTitle = new Label("There is no EPrescriptions.");
            newEPrescriptionTitle.setPrefHeight(200);
            newEPrescriptionTitle.setPrefWidth(817);
            newEPrescription.setMinWidth(817);
            newEPrescriptionTitle.setAlignment(Pos.CENTER);
            newEPrescriptionTitle.setFont(new Font("Consolas Bold", 35.0));
            newEPrescription.getChildren().add(newEPrescriptionTitle);
            gridPane.add(newEPrescription, 0, 0);
        } else {
            String[] EPrescriptionsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < EPrescriptionsData.length; i++) {
                String[] EPrescriptionData = EPrescriptionsData[i].split(", ");

                VBox newEPrescription = new VBox();
                newEPrescription.setFocusTraversable(false);
                gridPane.setFocusTraversable(false);
                scrollPane.setFocusTraversable(false);
                newEPrescription.setPrefHeight(200);
                newEPrescription.setId("ePrescription" + i);
                newEPrescription.setOnMouseClicked(mouseEvent -> {
                    saveButton.requestFocus();
                    saveButton.setVisible(true);
                    printButton.setVisible(true);
                    generatePDF(EPrescriptionData[3], EPrescriptionData[1], EPrescriptionData[2], EPrescriptionData[4], EPrescriptionData[7], EPrescriptionData[8]);
                });

                // Set fitting to scroll bar
                if (EPrescriptionsData.length > 9) {
                    newEPrescription.setPrefWidth(268);
                    newEPrescription.setMinWidth(268);
                    newEPrescription.setMaxWidth(268);
                } else {
                    newEPrescription.setPrefWidth(272);
                    newEPrescription.setMinWidth(272);
                    newEPrescription.setMaxWidth(272);
                }

                newEPrescription.setOnMouseEntered(mouseEvent -> {
                    newEPrescription.setStyle("-fx-background-color: #e6e6e6; -fx-background-radius: 10;");
                });

                newEPrescription.setOnMouseExited(mouseEvent -> {
                    newEPrescription.setStyle("-fx-background-color: #f2f2f2; -fx-background-radius: 10;");
                });

                newEPrescription.setOnMousePressed(mouseEvent -> {
                    newEPrescription.setStyle("-fx-background-color: #cccccc; -fx-background-radius: 10;");
                });

                Label newEPrescriptionTitle = new Label("E-Prescription " + (i+1));
                Label newEPrescriptionCode = new Label("Code: " + EPrescriptionData[3]);
                Label newEPrescriptionMedicines = new Label("Medicines:\n" + EPrescriptionData[4]);
                Label newEPrescriptionDate = new Label("Date of issue: " + EPrescriptionData[2]);
                Label newEPrescriptionDoctor = new Label("dr. " + EPrescriptionData[7] + " " + EPrescriptionData[8]);

                newEPrescriptionTitle.setAlignment(Pos.CENTER);
                newEPrescriptionTitle.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                newEPrescriptionTitle.setPrefHeight(50);
                newEPrescriptionTitle.setPrefWidth(268);
                newEPrescriptionTitle.setFont(new Font("Consolas Bold", 20.0));
                newEPrescriptionTitle.setWrapText(true); // Text wrapping

                newEPrescriptionCode.setPrefHeight(14);
                newEPrescriptionCode.setPadding(new Insets(0, 0, 0, 10));
                newEPrescriptionCode.setFont(new Font("Consolas", 12.0));

                newEPrescriptionMedicines.setMinHeight(24);
                newEPrescriptionMedicines.setPadding(new Insets(10, 0, 0, 10));
                newEPrescriptionMedicines.setFont(new Font("Consolas", 14.0));
                newEPrescriptionMedicines.setWrapText(true); // Text wrapping

                newEPrescriptionDate.setPrefWidth(268);
                newEPrescriptionDate.setAlignment(Pos.CENTER);
                newEPrescriptionDate.setPrefHeight(20);
                newEPrescriptionDate.setPadding(new Insets(10, 0, 0, 0));
                newEPrescriptionDate.setFont(new Font("Consolas", 18.0));

                newEPrescriptionDoctor.setPrefWidth(268);
                newEPrescriptionDoctor.setAlignment(Pos.CENTER);
                newEPrescriptionDoctor.setPrefHeight(26);
                newEPrescriptionDoctor.setPadding(new Insets(10, 0, 0, 0));
                newEPrescriptionDoctor.setFont(new Font("Consolas Italic", 18.0));

                newEPrescription.getChildren().addAll(newEPrescriptionTitle, newEPrescriptionCode, newEPrescriptionMedicines, newEPrescriptionDate, newEPrescriptionDoctor);

                // Add new EPrescription to the GridPane on the appropriate row and column
                gridPane.add(newEPrescription, i % 3, i / 3);
            }
        }
    }
}
