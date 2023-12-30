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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Color;
import utils.Message;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Color.greenGradient;
import static utils.Color.redGradient;

public class EReferralController implements Initializable {
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

    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/ClientPanelScene.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EReferralController.ReadFromServer = Client.ReadFromServer;
        EReferralController.SendToServer = Client.SendToServer;

        saveButton.setVisible(false);
        printButton.setVisible(false);

        //scrollPane.setFocusTraversable(false);
        //gridPane.setFocusTraversable(false);


        try {
            getEReferralFromDB();
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

    public void generatePDF(String eReferralCode, String eReferralBarcode, String eReferralTitle, String eReferralDate, String eReferralDRFirstName, String eReferralDRLastName) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("!!" + scrollPane.isFocusVisible() + scrollPane.isFocused() + scrollPane.isFocusTraversable());
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

            int fontSize = 30;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            contentStream.beginText();
            String tempText = "E-Referral " + eReferralTitle;
            float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 700);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.beginText();
            tempText = "Issued on " + eReferralDate;
            textWidth = PDType1Font.HELVETICA.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 500);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.TIMES_ROMAN, fontSize);
            contentStream.beginText();
            tempText = "Code: " + eReferralCode;
            textWidth = PDType1Font.TIMES_ROMAN.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLine();
            contentStream.newLineAtOffset((595 - textWidth) / 2, 180);
            contentStream.showText(tempText);
            contentStream.endText();

            fontSize = 18;
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.beginText();
            tempText = "by dr. " + eReferralDRFirstName + " " + eReferralDRLastName;
            textWidth = PDType1Font.HELVETICA.getStringWidth(tempText) / 1000 * fontSize;
            contentStream.newLineAtOffset((595 - textWidth) / 2, 210);
            contentStream.showText(tempText);
            contentStream.endText();

            // Adding barcode
            int width = 400;
            int height = 50;
            BufferedImage bufferedImage = generateBarcode(eReferralBarcode, width, height);

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
                        document.save("src/main/resources/PDFs/" + "eReferral" + eReferralCode + ".pdf");

                        buttonStatusLabel.setTextFill(greenGradient());
                        System.out.println("eReferral" + eReferralCode + " saved corretly.");
                        buttonStatusLabel.setText("eReferral " + eReferralCode + " saved corretly");
                        resetButtonStatus();
                    } else {
                        buttonStatusLabel.setTextFill(redGradient());
                        buttonStatusLabel.setText("Error while saving eReferral " + eReferralCode + ", document is null!");
                        System.out.println(Color.ColorString("Error while saving eReferral " + eReferralCode + ", document is null!", Color.ANSI_RED));
                        resetButtonStatus();
                    }
                } catch (IOException e) {
                    buttonStatusLabel.setTextFill(redGradient());
                    buttonStatusLabel.setText("Error while saving eReferral " + eReferralCode + "!");
                    System.out.println(Color.ColorString("Error while saving eReferral " + eReferralCode + "!", Color.ANSI_RED));
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
                            System.out.println("eReferral" + eReferralCode + " printed corretly.");
                            buttonStatusLabel.setText("eReferral " + eReferralCode + " printed corretly");
                            resetButtonStatus();
                        } else {
                            buttonStatusLabel.setTextFill(redGradient());
                            buttonStatusLabel.setText("Error while printing eReferral " + eReferralCode + "!");
                            System.out.println(Color.ColorString("Error while printing eReferral " + eReferralCode + "!", Color.ANSI_RED));
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

    private void getEReferralFromDB() throws IOException {
        message.sendGetEReferralMessage(SendToServer, Client.clientId  + "," + Client.user_id);
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);

        if(serverAnswer.equals("[[No EReferrals in database]]")) {
            Pane newEReferral = new Pane();
            Label newEReferralTitle = new Label("There is no EReferrals.");
            newEReferralTitle.setPrefHeight(200);
            newEReferralTitle.setPrefWidth(817);
            newEReferral.setMinWidth(817);
            newEReferralTitle.setAlignment(Pos.CENTER);
            newEReferralTitle.setFont(new Font("Consolas Bold", 35.0));
            newEReferral.getChildren().add(newEReferralTitle);
            gridPane.add(newEReferral, 0, 0);
        } else {
            String[] EReferralsData = serverAnswer.substring(2, serverAnswer.length() - 2).split("], \\[");

            for (int i = 0; i < EReferralsData.length; i++) {
                String[] EReferralData = EReferralsData[i].split(", ");

                VBox newEReferral = new VBox();
                newEReferral.setFocusTraversable(false);
                gridPane.setFocusTraversable(false);
                scrollPane.setFocusTraversable(false);
                newEReferral.setPrefHeight(200);
                newEReferral.setId("eReferral" + i);
                newEReferral.setOnMouseClicked(mouseEvent -> {
                    saveButton.requestFocus();
                    saveButton.setVisible(true);
                    printButton.setVisible(true);
                    generatePDF(EReferralData[3], EReferralData[1], EReferralData[4], EReferralData[2], EReferralData[7], EReferralData[8]);
                });

                // Set fitting to scroll bar
                if(EReferralsData.length > 9) {
                    newEReferral.setPrefWidth(268);
                    newEReferral.setMinWidth(268);
                    newEReferral.setMaxWidth(268);
                } else {
                    newEReferral.setPrefWidth(272);
                    newEReferral.setMinWidth(272);
                    newEReferral.setMaxWidth(272);
                }

                Label newEReferralTitle = new Label(EReferralData[4]);
                Label newEReferralCode = new Label("Code: " + EReferralData[3]);
                Label newEReferralBarcode = new Label("Barcode:\n" + EReferralData[1]);
                Label newEReferralDate = new Label("Date of issue: " + EReferralData[2]);
                Label newEReferralDoctor = new Label("dr. " + EReferralData[7] + " " + EReferralData[8]);

                newEReferralTitle.setAlignment(Pos.CENTER);
                newEReferralTitle.setTextAlignment(TextAlignment.CENTER); // to set text to center after text wrapping
                newEReferralTitle.setPrefHeight(50);
                newEReferralTitle.setPrefWidth(268);
                newEReferralTitle.setFont(new Font("Consolas Bold", 20.0));
                newEReferralTitle.setWrapText(true); // Text wrapping

                newEReferralCode.setPrefHeight(14);
                newEReferralCode.setPadding(new Insets(0, 0, 0, 10));
                newEReferralCode.setFont(new Font("Consolas", 12.0));

                newEReferralBarcode.setMinHeight(24);
                newEReferralBarcode.setPadding(new Insets(10, 0, 0, 10));
                newEReferralBarcode.setFont(new Font("Consolas", 10.0));

                newEReferralDate.setPrefWidth(268);
                newEReferralDate.setAlignment(Pos.CENTER);
                newEReferralDate.setPrefHeight(20);
                newEReferralDate.setPadding(new Insets(10, 0, 0, 0));
                newEReferralDate.setFont(new Font("Consolas", 18.0));

                newEReferralDoctor.setPrefWidth(268);
                newEReferralDoctor.setAlignment(Pos.CENTER);
                newEReferralDoctor.setPrefHeight(26);
                newEReferralDoctor.setPadding(new Insets(20, 0, 0, 0));
                newEReferralDoctor.setFont(new Font("Consolas Italic", 18.0));

                newEReferral.getChildren().addAll(newEReferralTitle, newEReferralCode, newEReferralBarcode, newEReferralDate, newEReferralDoctor);

                // Add new EReferral to the GridPane on the appropriate row and column
                gridPane.add(newEReferral, i % 3, i / 3);
            }
        }
    }
}
