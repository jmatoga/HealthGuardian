package com.healthguardian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class WindowApplication extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        WindowApplication.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource("LogInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500); // Width and height
        Image icon = new Image(String.valueOf(WindowApplication.class.getResource("/photos/iconImage.png")));
        stage.getIcons().add(icon);
        stage.setTitle("HealthGuardian");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void startWindow() {
        launch();
    }
}