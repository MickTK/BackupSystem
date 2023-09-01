package it.backup.system;

import it.backup.system.backup.*;
import it.backup.system.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    // Text fields
    @FXML private TextField sourceInput;
    @FXML private TextField destinationInput;
    @FXML private TextArea consoleLog;

    // Buttons
    @FXML private RadioButton completeButton;
    @FXML private RadioButton differentialButton;
    @FXML private RadioButton incrementalButton;
    @FXML private Button chooseSourcePath;
    @FXML private Button chooseDestinationPath;
    @FXML private Button processButton;

    // Progress bar
    @FXML private ProgressBar progressBar;

    Backup backup;
    private File source;
    private File destination;

    /**
     * Inizializza il controller
     */
    public void initialize() {
        completeButton.setOnAction(event -> {
            differentialButton.setSelected(false);
            incrementalButton.setSelected(false);
        });
        differentialButton.setOnAction(event -> {
            completeButton.setSelected(false);
            incrementalButton.setSelected(false);
        });
        incrementalButton.setOnAction(event -> {
            differentialButton.setSelected(false);
            completeButton.setSelected(false);
        });
        chooseSourcePath.setOnAction(this::selectSourcePath);
        chooseSourcePath.requestFocus();
        chooseDestinationPath.setOnAction(this::selectDestinationPath);
        processButton.setOnAction(this::process);
    }

    /**
     * Recupera e salva il percorso della cartella da salvare
     * @param event
     */
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

    /**
     * Recupera e salva il percorso della cartella di destinazione
     * @param event
     */
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

    /**
     * Inizia il processo di backup
     * @param event
     */
    private void process(ActionEvent event){
        BackupType backupType;
        if (differentialButton.isSelected())
            backupType = BackupType.Differential;
        else if (incrementalButton.isSelected())
            backupType = BackupType.Incremental;
        else
            backupType = BackupType.Complete;

        if (Application.DEBUG){
            source = new File("/home/michele/Desktop/Cartella da salvare");
            destination = new File("/home/michele/Desktop/Destinazione");
        }

        if (Utils.isSourcePathValid(source,consoleLog) && Utils.isDestinationPathValid(destination,consoleLog)){
            try {
                switch (backupType) {
                    case Complete:
                        backup = new CompleteBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    case Differential:
                        backup = new DifferentialBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    case Incremental:
                        backup = new IncrementalBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    default: break;
                }

                consoleLog.setText("");
                log("Backup in corso.");
                log("Numero di cartelle trovate: " + Utils.numberOfFolders(source) + ".");
                log("Numero di file trovati: " + Utils.numberOfFiles(source) + ".");

                long lastDeltaTime = System.nanoTime();
                backup.start();
                long deltaTime = (long) ((System.nanoTime() - lastDeltaTime) / 1_000_000_000.0D);
                log("Backup completato in " + deltaTime + " secondi.");
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Aggiunge del testo in coda al campo di log
     * @param text testo da aggiungere
     */
    private void log(String text){
        Utils.addLog(consoleLog, text);
    }
}
