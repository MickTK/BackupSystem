module com.example.backupsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.backupsystem to javafx.fxml;
    exports com.example.backupsystem;
}