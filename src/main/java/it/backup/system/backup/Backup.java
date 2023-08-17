package it.backup.system.backup;

import it.backup.system.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backup {
    /* Macros */
    private final int MIN_INCREMENTAL_NUMBER = 1;                       // Valore minimo di un backup incrementale (x ∈ N)
    private final String DELETED_FILES_FILE_NAME = "_deleted_._files_"; // Nome del file che tiene traccia dei file eliminati

    /* Attributes */
    private BackupType type;              // Rappresenta il tipo di backup da effettuare (completo, incrementale, differenziale)

    private String sourceFolderPath;      // Rappresenta la cartella da salvare
    private String destinationFolderPath; // Rappresenta la cartella che conterrà la cartella da salvare
    private String completeFolderName;    // Rappresenta il nome della cartella originale che sarà poi presente nella cartella di destinazione
    private String incrementalFolderName; // Nome della cartella del backup incrementale

    private File delFile;                 // File che tiene traccia dei file eliminati rispetto al backup completo
    private File completeBackupFolder;    // Cartella del backup completo

    /******************************************************
     * Constructors
     *****************************************************/

    public Backup(String sourceFolderPath, String destinationFolderPath){
        this.sourceFolderPath = sourceFolderPath;
        this.destinationFolderPath = destinationFolderPath;
        this.completeFolderName = new File(sourceFolderPath).getName();
        this.type = Files.exists(new File(Utils.combine(destinationFolderPath, completeFolderName)).toPath()) ?
                BackupType.Differential : BackupType.Complete;
    }
    public Backup(String sourceFolderPath, String destinationFolderPath, BackupType type){
        this.sourceFolderPath = sourceFolderPath;
        this.destinationFolderPath = destinationFolderPath;
        this.completeFolderName = new File(sourceFolderPath).getName();
        this.type = type;
    }

    /******************************************************
     * Getters and setters
     *****************************************************/

    public BackupType getType() {
        return type;
    }
    public void setType(BackupType type) {
        this.type = type;
    }

    public String getSourceFolderPath() {
        return sourceFolderPath;
    }
    public void setSourceFolderPath(String sourceFolderPath) {
        this.sourceFolderPath = sourceFolderPath;
    }

    public String getDestinationFolderPath() {
        return destinationFolderPath;
    }
    public void setDestinationFolderPath(String destinationFolderPath) {
        this.destinationFolderPath = destinationFolderPath;
    }

    /******************************************************
     * Backup functions
     *****************************************************/

    /**
     * Effettua il backup in base al tipo
     */
    public void start(){
        switch (type){
            case Complete:
                new File(Utils.combine(destinationFolderPath, completeFolderName)).mkdir();
                startComplete(new File(sourceFolderPath));
                break;
            case Incremental:
                completeBackupFolder = new File(Utils.combine(destinationFolderPath, completeFolderName));
                if (completeBackupFolder.exists() && completeBackupFolder.isDirectory()){
                    incrementalFolderName = getIncrementalBackupFolderName();
                    new File(Utils.combine(destinationFolderPath,incrementalFolderName)).mkdir();
                    startIncremental(new File(sourceFolderPath));
                    createDelFile();
                    startIncrementalDeleted(completeBackupFolder);
                }
                else {
                    System.out.println("Non esiste nessun backup completo chiamato " + completeFolderName + " all'interno della destinazione.");
                    return;
                }
                break;
            case Differential: break;
            default: break;
        }
    }

    /********************************************
     * Complete backup
     *******************************************/

    /**
     * Effettua un backup completo
     * @param sourceFolder
     */
    private void startComplete(@NotNull File sourceFolder) {
        if (sourceFolder.isDirectory()) {
            File[] sourceFiles = sourceFolder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    // Crea un file con il percorso di destinazione
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                            sourceFolderPath, Utils.combine(destinationFolderPath, completeFolderName)
                    ));
                    // Se il file è una cartella, la crea
                    if (sourceFile.isDirectory()) {
                        if(!Files.exists(destinationFile.toPath())) destinationFile.mkdir();
                        startComplete(sourceFile);
                    }
                    // Se il file non è una cartella, copia il contenuto del file originale nella cartella di destinazione
                    else if (sourceFile.isFile()) {
                        try{Files.copy(sourceFile.toPath(), destinationFile.toPath());}
                        catch(Exception e){e.printStackTrace();}
                    }
                }
            }
        }
    }

    /********************************************
     * Incremental backup
     *******************************************/


    /**
     * Effettua un backup incrementale
     * @param sourceFolder
     */
    private void startIncremental(@NotNull File sourceFolder) {
        if (sourceFolder.isDirectory()) {
            File[] sourceFiles = sourceFolder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (isIgnored(sourceFile)) continue;

                    // Cerchiamo il file nella cartella del backup completo
                    File completeFile = new File(sourceFile.getAbsolutePath().replace(
                            sourceFolderPath, completeBackupFolder.getAbsolutePath()
                    ));
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                            sourceFolderPath, Utils.combine(destinationFolderPath,incrementalFolderName)
                    ));

                    /* File creati/modificati */
                    try {
                        if (sourceFile.isFile()){
                            // Se il file è stato creato/modificato
                            if (!completeFile.exists() || (completeFile.exists() && Utils.lastModifiedDateCompare(sourceFile, completeFile) < 0)){
                                if(!destinationFile.getParentFile().exists())
                                    destinationFile.getParentFile().mkdirs();
                                Files.copy(sourceFile.toPath(), destinationFile.toPath());
                            }
                        }
                        else if (sourceFile.isDirectory()){
                            startIncremental(sourceFile);
                        }
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        }
    }

    private void startIncrementalDeleted(@NotNull File completeFolder){
        if (completeFolder.isDirectory()) {
            File[] completeFiles = completeFolder.listFiles();
            if (completeFiles != null) {
                for (File completeFile : completeFiles) {
                    if (isIgnored(completeFile)) continue;

                    // Viene cercato il file nella cartella sorgente
                    File sourceFile = new File(completeFile.getAbsolutePath().replace(
                            Utils.combine(destinationFolderPath, completeFolderName), sourceFolderPath
                    ));

                    // Se il file è stato rimosso
                    if (!sourceFile.exists()){
                        // Aggiunge il file rimosso alla lista dei file rimossi
                        writeOnDelFile(completeFile);
                    }
                    // Se il file non è stato rimosso ed è una cartella
                    else if (completeFile.isDirectory()){
                        // Itera sul contenuto della cartella
                        startIncrementalDeleted(completeFile);
                    }
                }
            }
        }
    }

    /**
     * Ottiene il nome della cartella del backup incrementale (ad esempio: "Nome (x)")
     * @return
     */
    private String getIncrementalBackupFolderName(){
        int max_number = MIN_INCREMENTAL_NUMBER;
        int current;

        File folder = new File(destinationFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] destinationFiles = folder.listFiles();
            if (destinationFiles != null) {
                for (File destinationFile : destinationFiles) {
                    if (destinationFile.isDirectory()){
                        current = getIncrementalBackupFolderNumber(destinationFile.getName());
                        if (current > max_number){
                            max_number = current;
                        }
                    }
                }
            }
        }
        return completeFolderName + " (inc." + max_number + ")";
    }

    /**
     * Cerca e restituisce il numero di backup (incrementale) nel nome della cartella
     * @param folder nome della cartella
     * @return il numero di backup
     */
    private int getIncrementalBackupFolderNumber(String folder){
        String regex = completeFolderName + "\\s*\\(\\s*(inc\\.(\\d+))\\s*\\)"; // Esempio: "Nome (inc.2)"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(folder);
        if (matcher.matches()) {
            String extractedNumber = matcher.group(2);
            return Integer.parseInt(extractedNumber) + 1;
        } else {
            return MIN_INCREMENTAL_NUMBER;
        }
    }

    /**
     * Confronta il nome del file passato come input con i nomi dei file da ignorare
     * @param file file da controllare
     * @return true se il file è da ignorare, altrimenti false
     */
    private boolean isIgnored(@NotNull File file){
        return file.getName().equals(DELETED_FILES_FILE_NAME);
    }

    /* Deleted files */
    private void createDelFile(){
        try {
            delFile = new File(Utils.combine(
                    destinationFolderPath, incrementalFolderName, DELETED_FILES_FILE_NAME)
            );
            delFile.createNewFile();
        }
        catch(Exception e){e.printStackTrace();}
    }
    private void writeOnDelFile(File file){
        try {
            String f = file.getAbsolutePath().replace(
                    Utils.combine(destinationFolderPath, completeFolderName), ""
            ) + "\n";
            Files.write(
                    delFile.toPath(),
                    f.getBytes(),
                    StandardOpenOption.APPEND
            );
        }
        catch(Exception e){e.printStackTrace();}
    }
}
