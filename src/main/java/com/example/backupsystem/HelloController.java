package com.example.backupsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class HelloController {

    /** Source **/
    @FXML private TextField sourceTextField;
    @FXML private Button sourceButton;
    String sourcePath;

    /** Destination **/
    @FXML private TextField destinationTextField;
    @FXML private Button destinationButton;
    String destinationPath;

    @FXML private Button processButton;
    @FXML private TextArea consoleLog;

    @FXML
    private void selectSourcePath(ActionEvent event){
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli una cartella");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            sourcePath = selectedDirectory.getAbsolutePath();
            sourceTextField.setText(sourcePath);
        }
    }

    @FXML
    private void selectDestinationPath(ActionEvent event) {
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli una cartella");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            destinationPath = selectedDirectory.getAbsolutePath();
            destinationTextField.setText(destinationPath);
        }
    }

    @FXML
    private void process(ActionEvent event){
        File startFolder = new File(sourcePath);

        if (startFolder.exists() && startFolder.isDirectory()) {
            iterateFilesRecursively(startFolder);
        } else {
            System.out.println("La cartella di partenza non esiste o non è una cartella.");
        }
    }
    public void iterateFilesRecursively(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        iterateFilesRecursively(file); // Ricorsione per le sotto-cartelle
                    } else {
                        System.out.println("File trovato: " + file.getAbsolutePath());



                        //////////////////////////////////////////////


                        // Nuovo file che sarà salvato nella destinazione come copia di quello originale
                        File copyFile = new File(file.getPath().replace(sourcePath, destinationPath));
                        // Se il file esiste già nella destinazione
                        if(Files.exists(copyFile.toPath())){
                            // Non fa niente e continua con il prossimo
                            //System.out.println("Il file \"" + copyFile.getPath() + "\" esiste già.");
                            consoleLog.appendText("Il file \"" + copyFile.getPath() + "\" esiste già.\n");
                        }
                        else{
                            try{
                                Files.copy(file.toPath(), copyFile.toPath());
                            }
                            catch(Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
}
