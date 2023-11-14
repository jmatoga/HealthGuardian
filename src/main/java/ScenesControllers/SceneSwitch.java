package ScenesControllers;

import com.healthguardian.WindowApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import utils.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SceneSwitch {
    public SceneSwitch(String newFxmlScene) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), WindowApplication.primaryStage.getWidth(), WindowApplication.primaryStage.getHeight());
        WindowApplication.primaryStage.setScene(scene);
    }

    public SceneSwitch(String newFxmlScene, int width, int height, boolean ifMaximized, boolean ifResizable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        WindowApplication.primaryStage.setScene(scene);
        WindowApplication.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
        WindowApplication.primaryStage.setResizable(ifResizable);
    }

    public SceneSwitch(String newFxmlScene, int width, int height, boolean ifMaximized, boolean ifResizable, String newTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        WindowApplication.primaryStage.setScene(scene);
        WindowApplication.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
        WindowApplication.primaryStage.setResizable(ifResizable);
        WindowApplication.primaryStage.setTitle(newTitle);
    }

    public SceneSwitch(String newFxmlScene,int width, int height, int MaxWidth, int MaxHeight, boolean ifMaximized, boolean ifResizable, String newTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WindowApplication.class.getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        WindowApplication.primaryStage.setScene(scene);
        WindowApplication.primaryStage.setResizable(!ifResizable);
        WindowApplication.primaryStage.setMaxHeight(MaxHeight);
        WindowApplication.primaryStage.setMaxWidth(MaxWidth);
        WindowApplication.primaryStage.setTitle(newTitle);
        WindowApplication.primaryStage.setResizable(ifResizable);
        WindowApplication.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
    }
}
