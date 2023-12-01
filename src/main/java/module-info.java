module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires javafx.web;
    opens com.healthguardian to javafx.fxml;
    exports com.healthguardian;
    opens ScenesControllers to javafx.fxml;
    exports ScenesControllers;
}
