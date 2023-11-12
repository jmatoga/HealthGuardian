package com.healthguardian;

import Client.Client;
import Scenes.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class WindowApplication extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        WindowApplication.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource("LogInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500); // Szerokość i wysokość sceny
        Image icon = new Image(String.valueOf(WindowApplication.class.getResource("/photos/iconImage.png")));
        stage.getIcons().add(icon);
        //Scene scene1 = new Scene(fxmlLoader.load(), 820, 540);
        stage.setTitle("HealthGuardian");
        stage.setScene(scene);
        stage.show();
    }

    public void startWindow() {
        launch();
    }
}