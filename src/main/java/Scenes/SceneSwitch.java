package Scenes;

import com.healthguardian.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitch {
    public SceneSwitch(AnchorPane currentAnchorPane, String fxml) throws IOException {
        AnchorPane nextAnchorPane = FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource(fxml)));
        currentAnchorPane.getChildren().removeAll();
        currentAnchorPane.getChildren().setAll(nextAnchorPane);
    }




//        private Stage stage;
//        private Scene scene;
//        private Parent root;
//
//        public void switchToScene1(ActionEvent event) throws IOException {
//            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
//            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//            scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        }
//
//        public void switchToScene2(ActionEvent event) throws IOException {
//            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ClientPanelScene.fxml")));
//            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//            scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        }
    }

