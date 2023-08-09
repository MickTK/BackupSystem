module com.example.backupsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.backup.system to javafx.fxml;
    exports it.backup.system;
}