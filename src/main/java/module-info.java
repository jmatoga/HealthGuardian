module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;



    opens com.healthguardian to javafx.fxml;
    exports com.healthguardian;
    exports ScenesControllers;
    opens ScenesControllers to javafx.fxml;
}