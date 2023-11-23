package ScenesControllers;


import Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.Message;
import javafx.embed.swing.SwingFXUtils;


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
    private ImageView previewImageView;

    @FXML
    private AnchorPane ePrescriptionScene;


    @FXML
    private void userPanelButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("ClientPanelScene.fxml");
    }


    private void loadPDF() {
        try {
            File file = new File("src/main/resources/photos/e-prescription.pdf");
            PDDocument doc1 = Loader.loadPDF(file);

            PDFRenderer pr = new PDFRenderer (doc1);
            BufferedImage bi = pr.renderImageWithDPI(0, 300);

            Image fxImage = SwingFXUtils.toFXImage(bi, null);

            previewImageView.setImage(fxImage);

            doc1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //loadPDF();
        EPrescriptionController.ReadFromServer = Client.ReadFromServer;
        EPrescriptionController.SendToServer = Client.SendToServer;
    }
}
