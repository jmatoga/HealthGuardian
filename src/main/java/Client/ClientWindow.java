package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.Color;

import java.io.IOException;

import static java.lang.System.exit;

public class ClientWindow extends Application {
    public static Stage primaryStage;

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

    public void startWindow() {
        launch();
        System.out.println(Color.ColorString("Disconnected from server.",Color.ANSI_PURPLE));
        exit(0);
    }
}