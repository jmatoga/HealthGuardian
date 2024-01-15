package ScenesControllers;

import Client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import utils.Color;
import utils.Message;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminPanelController implements Initializable {
    private static final Message message = new Message();
    private static BufferedReader ReadFromServer;
    private static PrintWriter SendToServer;

    @FXML
    private Label statusLabel;

    @FXML
    private void addNewDoctorButtonClicked(ActionEvent actionEvent) throws IOException {
        GridPane grid = new GridPane();
        TextField inputFieldFirstName = new TextField();
        inputFieldFirstName.setPromptText("Eg. Jan");
        TextField inputFieldLastName = new TextField();
        inputFieldLastName.setPromptText("Eg. Kowalski");
        TextField inputFieldPhone = new TextField();
        inputFieldPhone.setPromptText("Eg. 123456789");
        TextField inputFieldEmail = new TextField();
        inputFieldEmail.setPromptText("Eg. jan.kowalski@mail.com");
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>();
        genderChoiceBox.getItems().addAll("Male", "Female", "Other");
        TextField inputFieldProfession = new TextField();
        inputFieldProfession.setPromptText("Eg. Pediatrician");
        TextField inputFieldUsername = new TextField();
        inputFieldUsername.setPromptText("Eg. jkowalski");
        PasswordField inputFieldPassword = new PasswordField();
        inputFieldPassword.setPromptText("Eg. password");
        TextField inputFieldClinic = new TextField();
        inputFieldClinic.setPromptText("Eg. Health Guardian Clinic");

        Label label = new Label("");
        label.setTextFill(Paint.valueOf("#ff0000"));

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", okButtonType);
        alert.setTitle("Add new doctor:");
        alert.setHeaderText("Write below details of doctor.");
        label.setMinWidth(100);
        label.setMinHeight(30);
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 10, 0));
        grid.setMinWidth(420);
        grid.setMaxWidth(420);

        grid.add(new Label("First name:"), 0, 0);
        grid.add(inputFieldFirstName, 1, 0);
        inputFieldFirstName.setMinWidth(200);
        grid.add(new Label("Last name:"), 0, 1);
        grid.add(inputFieldLastName, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(inputFieldPhone, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(inputFieldEmail, 1, 3);
        grid.add(new Label("Gender:"), 0, 4);
        grid.add(genderChoiceBox, 1, 4);
        grid.add(new Label("Profession:"), 0, 5);
        grid.add(inputFieldProfession, 1, 5);
        grid.add(new Label("Username:"), 0, 6);
        grid.add(inputFieldUsername, 1, 6);
        grid.add(new Label("Password:"), 0, 7);
        grid.add(inputFieldPassword, 1, 7);
        grid.add(new Label("Clinic:"), 0, 8);
        grid.add(inputFieldClinic, 1, 8);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, grid);
        vBox.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(vBox);

        alert.setOnCloseRequest(alertEvent -> {
            if (alert.getResult() == okButtonType) {
                alertEvent.consume(); // cancel closing

                if (checkWrittenText(label, inputFieldFirstName.getText(), inputFieldLastName.getText(), inputFieldPhone.getText(), inputFieldEmail.getText(), genderChoiceBox.getValue(), inputFieldProfession.getText(), inputFieldUsername.getText(), inputFieldPassword.getText(), inputFieldClinic.getText())) {
                    message.checkIfDoctorExists(SendToServer, Client.clientId + "#/#" + inputFieldUsername.getText() + "#/#" + inputFieldEmail.getText() + "#/#" + inputFieldClinic.getText());

                    try {
                        String serverAnswer = Client.getServerResponse(ReadFromServer);

                        if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).startsWith("Free to use, clinic ID: ")) {
                            message.addNewDoctorMessage(SendToServer, Client.clientId + "#/#" + inputFieldFirstName.getText() + "#/#" + inputFieldLastName.getText() + "#/#" + inputFieldPhone.getText() + "#/#" + inputFieldEmail.getText() + "#/#" + genderChoiceBox.getValue() + "#/#" + inputFieldProfession.getText() + "#/#" + inputFieldUsername.getText() + "#/#" + inputFieldPassword.getText() + "#/#" + serverAnswer.substring(40));
                            String serverAnswer1 = Client.getServerResponse(ReadFromServer);

                            if (serverAnswer1.equals("Added new doctor correctly.")) {
                                statusLabel.setTextFill(Color.greenGradient());
                                statusLabel.setText(serverAnswer1);
                            } else {
                                statusLabel.setTextFill(Color.redGradient());
                                statusLabel.setText("Error: " + serverAnswer1);
                            }

                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.millis(2000), TimeEvent -> {
                                        statusLabel.setText("");
                                    }));
                            timeline.setCycleCount(1);
                            timeline.play();

                        } else if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("Doctor exists")) {
                            alertEvent.consume(); // cancel closing
                            label.setText("Username is used, change it!");
                        } else if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("Email exists")) {
                            alertEvent.consume(); // cancel closing
                            label.setText("Email is used, change it!");
                        } else if (serverAnswer.startsWith("EXISTING RESULT:") && serverAnswer.substring(16).equals("Wrong clinic name")) {
                            alertEvent.consume(); // cancel closing
                            label.setText("Wrong clinic name, change it!");
                        } else {
                            alertEvent.consume(); // cancel closing
                            label.setText("Error in database! Try again later.");
                            System.out.println(Color.ColorString("Error in database while adding new doctor! Try again later.", Color.ANSI_RED));
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        alert.showAndWait();
    }

    boolean checkWrittenText(Label label, String firstName, String lastName, String phoneNumber, String email, String gender, String profession, String username, String password, String clinic) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || gender == null || profession.isEmpty() || username.isEmpty() || password.isEmpty() || clinic.isEmpty())
            label.setText("You have to fill all gaps!");
        else if (firstName.contains(" ") || !firstName.matches("^[a-zA-Z]+$"))
            label.setText("Wrong first name!");
        else if (lastName.contains(" ") || !lastName.matches("^[a-zA-Z]+$"))
            label.setText("Wrong last name!");
        else if (!email.contains("@") || !email.contains(".") || email.contains(" ") || email.lastIndexOf('.') != email.indexOf('.') || email.lastIndexOf('@') != email.indexOf('@') || email.length() < 6 || email.indexOf(".", email.indexOf("@")) != email.lastIndexOf(".") || email.substring(email.lastIndexOf(".")).length() <= 2) // if contains 2 @ or 2 . , . should be after @ , after . should be at least 2 chars
            label.setText("Wrong email!");
        else if (phoneNumber.length() != 9 || !phoneNumber.matches("\\d+"))
            label.setText("Wrong phone number!");
        else if (!profession.matches("^[a-zA-Z]+$"))
            label.setText("Wrong profession!");
        else if (!clinic.matches("^[a-zA-Z\\s]+$"))
            label.setText("Wrong clinic name!");
        else if (username.contains(" "))
            label.setText("Wrong username, space inside!");
        else if (password.contains(" "))
            label.setText("Wrong password!");
        else {
            label.setText("");
            return true;
        }
        return false;
    }

    @FXML
    private void newOneTimeCodeButtonClicked(ActionEvent actionEvent) throws IOException {
        message.sendCreateNewOneTimeCodesMessage(SendToServer, "" + Client.clientId);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        if (serverAnswer.equals("100 new one-time codes added correctly.")) {
            statusLabel.setTextFill(Color.greenGradient());
            statusLabel.setText(serverAnswer);
        } else {
            statusLabel.setTextFill(Color.redGradient());
            statusLabel.setText("Error: " + serverAnswer);
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(2000), TimeEvent -> {
                    statusLabel.setText("");
                }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    private void openDataBaseButtonClicked(ActionEvent actionEvent) {
        openWebpage("https://client.filess.io/?username=HealthGuardian_alloweager&server=5he.h.filess.io:3307&db=HealthGuardian_alloweager&password=2c2673f30c0d5912c63a30445aeb6dde46e713d0&driver=server");
    }

    @FXML
    private void generateReportButtonClicked(ActionEvent actionEvent) throws IOException {
        message.generateReport(SendToServer, "" + Client.clientId);
        String serverAnswer = Client.getServerResponse(ReadFromServer);

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDType0Font font1 = PDType0Font.load(document, new File("src/main/resources/Fonts/calibri.ttf"));

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            int textHeight = 750; // The initial height of the text
            int remainingSpace = 750; // Maximum height available on the website
            int textHeightRequired = 30;

            String[] dataArray = serverAnswer.substring(1, serverAnswer.length() - 1).split(", ");
            int fontSize = 16;
            int temp = 0;

            for (String value : dataArray) {
                if (value.equals("&/&")) {
                    // if &/& - go to new page
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);

                    // Reset text height and available space for the new page
                    textHeight = 750;
                    remainingSpace = 750;

                    float textWidth = font1.getStringWidth(value) / 1000 * fontSize;
                    contentStream.setFont(font1, fontSize);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, textHeight);

                    if (temp == 1)
                        contentStream.showText("Clinics list:");
                    else if (temp == 2)
                        contentStream.showText("Count of prescribed e-referral in each month:");
                    else if (temp == 3)
                        contentStream.showText("Count of prescribed e-prescription in each month:");
                    else if (temp == 4)
                        contentStream.showText("Number of users in each clinic:");
                    else if (temp == 5)
                        contentStream.showText("Number of doctors in each clinic:");
                    else if (temp == 6)
                        contentStream.showText("One time codes:");
                    else
                        contentStream.showText("");

                    contentStream.endText();
                    temp++;
                    textHeight -= 30;
                    remainingSpace -= textHeightRequired;

                } else if (value.equals("$/$")) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, textHeight);
                    contentStream.showText("------------------------------------------------------------------------------------");
                    contentStream.endText();
                    textHeight -= 30;
                    remainingSpace -= textHeightRequired;
                } else {
                    // Normal adding text
                    float textWidth = font1.getStringWidth(value) / 1000 * fontSize;

                    // Check if there is enough space on the current page
                    if (remainingSpace - textHeightRequired < 0) {
                        // if not add new page
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);

                        // Reset text height and available space for the new page
                        textHeight = 750;
                        remainingSpace = 750;
                    }

                    contentStream.setFont(font1, fontSize);

                    if (temp == 0) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(200, textHeight);
                        contentStream.showText("Report from " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                        contentStream.endText();
                        textHeight-=30;
                        remainingSpace -= textHeightRequired;

                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, textHeight);
                        contentStream.showText("Number of users and doctors:");
                        contentStream.endText();
                        textHeight-=30;
                        remainingSpace -= textHeightRequired;

                        temp++;
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset((595 - textWidth) / 2, textHeight);
                    contentStream.showText(value);
                    contentStream.endText();

                    // Update text height and available space
                    textHeight -= 30;
                    remainingSpace -= textHeightRequired;
                }
            }

            contentStream.close();

            document.save("src/main/resources/PDFs/" + "Report" + LocalDate.now() + ".pdf");
            document.close();
            statusLabel.setTextFill(Color.greenGradient());
            statusLabel.setText("Report generated and saved correctly.");

        } catch (IOException e) {
            statusLabel.setTextFill(Color.redGradient());
            statusLabel.setText("Error while generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void logOutButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch("/ScenesLayout/LogInScene.fxml", 820, 500, 800, 500, false, false, "HealthGuardian");
    }

    private static void openWebpage(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AdminPanelController.ReadFromServer = Client.ReadFromServer;
        AdminPanelController.SendToServer = Client.SendToServer;

    }
}
