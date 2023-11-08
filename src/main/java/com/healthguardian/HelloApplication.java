package com.healthguardian;

import Client.Client;
import Server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        //Button button1 = new Button("przycisk1");

        //VBox layout = new VBox(10);
        //layout.getChildren().addAll(button1, fxmlLoader.load());
        Scene scene = new Scene(fxmlLoader.load(), 800, 500); // Szerokość i wysokość sceny
        Image icon = new Image(String.valueOf(HelloApplication.class.getResource("/photos/3.png")));
        stage.getIcons().add(icon);
        //Scene scene1 = new Scene(fxmlLoader.load(), 820, 540);
        stage.setTitle("HealthGuardian");
        stage.setScene(scene);
        stage.show();
        System.out.println("2step");
    }

    public static void main(String[] args) {
        System.out.println("1step");
        launch(args);
        System.out.println("Client window closed!");
    }
}