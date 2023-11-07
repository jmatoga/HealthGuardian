module com.healthguardian.healthguardian {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.healthguardian.healthguardian to javafx.fxml;
    exports com.healthguardian.healthguardian;
}