package ScenesControllers;


import Client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Message;

//import javafx.scene.image.Image;

import java.awt.image.BufferedImage; // TODO
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class EPrescriptionController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Button userPanelButton;

    @FXML
    private AnchorPane ePrescriptionScene;

    @FXML
    private ImageView previewImageView;



    private void loadPDF() {
        try {

            File file = new File("src/main/resources/photos/e-prescription.pdf");
            PDDocument doc1 = Loader.loadPDF(file);

            PDFRenderer pr = new PDFRenderer (doc1);
            BufferedImage bi = pr.renderImageWithDPI(0, 300);
            WritableImage writableImage = new WritableImage(bi.getWidth(), bi.getHeight());


            previewImageView.setImage(writableImage);

            doc1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPDF();
        EPrescriptionController.ReadFromServer = Client.ReadFromServer;
        EPrescriptionController.SendToServer = Client.SendToServer;
    }
}
