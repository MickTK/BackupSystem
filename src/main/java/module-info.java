module com.example.backupsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.backup.system to javafx.fxml;
    exports it.backup.system;
    exports it.backup.system.utils;
    opens it.backup.system.utils to javafx.fxml;
    exports it.backup.system.backup;
    opens it.backup.system.backup to javafx.fxml;
}