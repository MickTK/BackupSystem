package it.backup.system;

import it.backup.system.backup.Backup;
import it.backup.system.backup.BackupType;
import it.backup.system.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    @FXML private TextField sourceInput;
    @FXML private TextField destinationInput;
    @FXML private TextArea consoleLog;

    Backup backup;
    private File source;
    private File destination;

    @FXML
    private void selectSourcePath(ActionEvent event){
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli la cartella che vuoi salvare.");

        source = directoryChooser.showDialog(primaryStage);

        if (Utils.isSourcePathValid(source, consoleLog)){
            sourceInput.setText(source.getAbsolutePath());
        }
        else {
            source = null;
            log("La cartella di origine non è valida.");
        }
    }

    @FXML
    private void selectDestinationPath(ActionEvent event) {
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli una cartella di destinazione.");

        destination = directoryChooser.showDialog(primaryStage);

        if (Utils.isDestinationPathValid(destination, consoleLog)) {
            destinationInput.setText(destination.getAbsolutePath());
        }
        else {
            destination = null;
            log("La cartella di destinazione non è valida.");
        }
    }

    @FXML
    private void process(ActionEvent event){
        if (Application.DEBUG){
            source = new File("/home/michele/Desktop/Da");
            destination = new File("/home/michele/Desktop/A");
        }

        if (Utils.isSourcePathValid(source,consoleLog) && Utils.isDestinationPathValid(destination,consoleLog)){

            consoleLog.setText("");
            log("Backup in corso.");
            log("Numero di cartelle trovate: " + Utils.numberOfFolders(source) + ".");
            log("Numero di file trovati: " + Utils.numberOfFiles(source) + ".");

            long lastDeltaTime = System.nanoTime();

            try {
                backup = new Backup(
                    source.getAbsolutePath(),
                    destination.getAbsolutePath(),
                    BackupType.Incremental
                );
                backup.start();
            }
            catch (Exception e) { e.printStackTrace(); }
            Long deltaTime = (long) ((System.nanoTime() - lastDeltaTime) / 1_000_000_000.0D);

            log("Backup completato in " + deltaTime.toString() + " secondi.");
        }
    }

    private void log(String text){
        Utils.addLog(consoleLog, text);
    }
}
