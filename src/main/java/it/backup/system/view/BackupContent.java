package it.backup.system.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class BackupContent extends AnchorPane {

    public BackupContent(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("backup_content.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) { e.printStackTrace(); }
    }

}
