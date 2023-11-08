module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.healthguardian to javafx.fxml;
    exports com.healthguardian;
    exports Scenes;
    opens Scenes to javafx.fxml;
}