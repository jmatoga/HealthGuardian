package Client;

import Server.SQLEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.Color;
import utils.Message;

import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.System.exit;

/**
 * The ClientWindow class extends the Application class from JavaFX.
 * It is responsible for creating and managing the client's window application.
 */
public class ClientWindow extends Application {
    public static Stage primaryStage;

    /**
     * The start method is the main entry point for all JavaFX applications.
     * It is called after the init method has returned, and after the system is ready for the application to begin running.
     * @param stage the primary stage for this application, onto which the application scene can be set.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void start(Stage stage) throws IOException {
        ClientWindow.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ScenesLayout/LogInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500); // Width and height
        Image icon = new Image(String.valueOf(ClientWindow.class.getResource("/Images/iconImage64x64.png")));
        stage.getIcons().add(icon);
        stage.setTitle("HealthGuardian");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * The startWindow method is responsible for launching the window application and handling the disconnection from the server.
     */
    public void startWindow() {
        launch();
        System.out.println(Color.ColorString("Disconnected from server.",Color.ANSI_PURPLE));
        // unlock the hour for examination if the client disconnects from the server
        final Message message = new Message();
        message.unLockHourForExaminationAfter5Minutes(Client.SendToServer, Client.clientId + "#/#" + Client.user_id);
        exit(0);
    }
}