package Scenes;

import Client.Client;
import com.healthguardian.WindowApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import Server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;


public class LogInController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button logInButton;

    @FXML
    private Button signInButton;

    @FXML
    private Label loggingStatus;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane helloScene;

    @FXML
    private void logInButtonClicked(ActionEvent event){
        Server.e();
        checkWrittenText();
    }

    @FXML
    private void enterPressedInField(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER)
            checkWrittenText();
    }

    @FXML
    private void unHighlightButtonsOnImageView(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED)
            imageView.requestFocus();
    }

    @FXML
    private void SignInButtonClicked(ActionEvent event) throws IOException {
        new SceneSwitch(helloScene, "SignInScene.fxml");
    }

//    private Stage stage;
//    private Scene scene;
//    private Parent root;

    private void checkWrittenText() {
        if (username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Username and Password can't be empty!");
        else if (!username.getText().isEmpty() && password.getText().isEmpty())
            loggingStatus.setText("Password can't be empty!");
        else if (username.getText().isEmpty() && !password.getText().isEmpty())
            loggingStatus.setText("Username can't be empty!");
        else {
            String[] tempArray = Server.chceckUsernameAndPasswordInDataBase(Client.clientId, username.getText(), password.getText());
            if (Boolean.parseBoolean(tempArray[0])) {
                Client.user_id = Integer.parseInt(tempArray[1]);
                System.out.println("user_id: " + Client.user_id);

                loggingStatus.setTextFill(Paint.valueOf("0x2aff00")); // green color
                loggingStatus.setText("Logged succesfully!");

                Platform.runLater(() -> {
                    try {
                        Thread.sleep(1500);
                        username.setText("");
                        password.setText("");
                        System.out.println("aaaaa" + Client.clientId);
                        //new SceneSwitch(helloScene, "ClientPanelScene.fxml");
                        WindowApplication.primaryStage.setTitle("HealthGuardian - clientID: " + Client.clientId + " ,user_id: " + Client.user_id);
                        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource("ClientPanelScene.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 1500, 900); // Szerokość i wysokość sceny
                        WindowApplication.primaryStage.setScene(scene);
//                      Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ClientPanelScene.fxml")));
//                      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//                      scene = new Scene(root);
//                      stage.setScene(scene);
//                      stage.show();
                    } catch (InterruptedException e) {
                        // Obsłuż ewentualny wyjątek spowodowany przerwaniem uśpienia
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else
                loggingStatus.setText("Wrong username or password!");
        }
    }
}



