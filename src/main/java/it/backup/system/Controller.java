package it.backup.system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class Controller {

    @FXML private TextField sourceInput;
    private File source;
    @FXML private TextField destinationInput;
    private File destination;
    @FXML private TextArea consoleLog;

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
        Integer nFolders, nFiles;
        if (Utils.isSourcePathValid(source,consoleLog) && Utils.isDestinationPathValid(destination,consoleLog)){

            // Numero di cartelle e di file
            nFolders = Utils.numberOfFolders(source);
            nFiles = Utils.numberOfFiles(source);
            log("Cartelle: " + nFolders.toString() + ", files: " + nFiles.toString());

            Utils.createZip(source, destination);

            // iterateFilesRecursively(source);
        }
    }
    public void iterateFilesRecursively(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        iterateFilesRecursively(file);
                    }
                    else {
                        // Nuovo file che sarà salvato nella destinazione come copia di quello originale
                        File copyFile = new File(file.getPath().replace(sourceInput.getText(), destinationInput.getText()));
                        // Se il file esiste già nella destinazione
                        if(Files.exists(copyFile.toPath())){
                            // Non fa niente e continua con il prossimo
                            log("Il file \"" + copyFile.getPath() + "\" esiste già.");
                        }
                        else{
                            try{
                                Files.copy(file.toPath(), copyFile.toPath());
                            }
                            catch(Exception e){
                                log(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    private void log(String text){
        Utils.addLog(consoleLog, text);
    }
}
