module it.backup.system {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.jetbrains.annotations;
    requires com.google.gson;

    opens it.backup.system to javafx.fxml;
    exports it.backup.system;
    exports it.backup.system.utils;
    opens it.backup.system.utils to javafx.fxml;
    exports it.backup.system.configuration.backup;
    opens it.backup.system.configuration.backup to javafx.fxml;
}