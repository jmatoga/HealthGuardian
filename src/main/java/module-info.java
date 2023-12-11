module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires javafx.web;
    opens Client to javafx.fxml;
    exports Client;
    opens ScenesControllers to javafx.fxml;
    exports ScenesControllers;
}
