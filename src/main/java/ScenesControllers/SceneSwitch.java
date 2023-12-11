package ScenesControllers;

import Client.ClientWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class SceneSwitch {
    public SceneSwitch(String newFxmlScene) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), ClientWindow.primaryStage.getWidth(), ClientWindow.primaryStage.getHeight());
        ClientWindow.primaryStage.setScene(scene);
    }

    public SceneSwitch(String newFxmlScene, int width, int height, boolean ifMaximized, boolean ifResizable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        ClientWindow.primaryStage.setScene(scene);
        ClientWindow.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
        ClientWindow.primaryStage.setResizable(ifResizable);
    }

    public SceneSwitch(String newFxmlScene, int width, int height, boolean ifMaximized, boolean ifResizable, String newTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        ClientWindow.primaryStage.setScene(scene);
        ClientWindow.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
        ClientWindow.primaryStage.setResizable(ifResizable);
        ClientWindow.primaryStage.setTitle(newTitle);
    }

    public SceneSwitch(String newFxmlScene,int width, int height, int MaxWidth, int MaxHeight, boolean ifMaximized, boolean ifResizable, String newTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newFxmlScene));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        ClientWindow.primaryStage.setScene(scene);
        ClientWindow.primaryStage.setResizable(!ifResizable);
        ClientWindow.primaryStage.setMaxHeight(MaxHeight);
        ClientWindow.primaryStage.setMaxWidth(MaxWidth);
        ClientWindow.primaryStage.setTitle(newTitle);
        ClientWindow.primaryStage.setResizable(ifResizable);
        ClientWindow.primaryStage.setMaximized(ifMaximized); // Fullscreen in window if true
    }
}
