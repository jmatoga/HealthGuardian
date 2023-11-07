package com.healthguardian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Button button1 = new Button("przycisk1");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(button1, fxmlLoader.load());
        Scene scene = new Scene(layout, 300, 200); // Szerokość i wysokość sceny

        //Scene scene1 = new Scene(fxmlLoader.load(), 820, 540);
        stage.setTitle("HealthGuardian");
        stage.setScene(scene);
        stage.show();

        System.out.println("2step");

    }

    public static void main(String[] args) {
        System.out.println("1step");
        launch();
        System.out.println("after kill window");
    }
}