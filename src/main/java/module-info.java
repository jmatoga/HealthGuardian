module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.healthguardian to javafx.fxml;
    exports com.healthguardian;
}