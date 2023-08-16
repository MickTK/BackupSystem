package it.backup.system.backup;

import it.backup.system.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backup {
    private final int MIN_INCREMENTAL_NUMBER = 1;
    private final String DELETED_FILES_FILE_NAME = "deleted_files.txt";

    private BackupType type;    // Rappresenta il tipo di backup da effettuare
    private String source;      // Rappresenta la cartella da salvare
    private String destination; // Rappresenta la cartella che conterrà la cartella da salvare
    private String folderName;  // Rappresenta il nome della cartella originale che sarà poi presente nella cartella di destinazione

    /******************************************************
     * Constructors
     *****************************************************/

    public Backup(String source, String destination){
        this.source = source;
        this.destination = destination;
        this.folderName = new File(source).getName();
        this.type = Files.exists(new File(Utils.concatPath(destination,folderName)).toPath()) ?
                BackupType.Differential : BackupType.Complete;
    }
    public Backup(String source, String destination, BackupType type){
        this.source = source;
        this.destination = destination;
        this.folderName = new File(source).getName();
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

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
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
                new File(Utils.concatPath(destination,folderName)).mkdir();
                startComplete(new File(source));
                break;
            case Incremental:
                completeBackupFolder = new File(Utils.concatPath(destination,folderName));
                if (completeBackupFolder.exists() && completeBackupFolder.isDirectory()){
                    incrementalFolderName = getIncrementalBackupFolderName();
                    new File(Utils.concatPath(destination,incrementalFolderName)).mkdir();
                    startIncremental(new File(source));
                    startIncrementalDeleted(completeBackupFolder);
                }
                else {
                    System.out.println("Non esiste nessun backup completo chiamato " + folderName + " all'interno della destinazione.");
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
    private void startComplete(File sourceFolder) {
        if (sourceFolder.isDirectory()) {
            File[] sourceFiles = sourceFolder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    // Crea un file con il percorso di destinazione
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                            source, Utils.concatPath(destination,folderName)
                    ));
                    // Se il file è una cartella, la crea
                    if (sourceFile.isDirectory()) {
                        if(!Files.exists(destinationFile.toPath())) destinationFile.mkdir();
                        startComplete(sourceFile);
                    }
                    // Se il file non è una cartella, copia il contenuto del file originale nella cartella di destinazione
                    else if (sourceFile.isFile()) {
                        try{Files.copy(sourceFile.toPath(), destinationFile.toPath());}
                        catch(Exception e){System.out.println(e.getMessage());}
                    }
                }
            }
        }
    }

    /********************************************
     * Incremental backup
     *******************************************/

    private File completeBackupFolder;
    private String incrementalFolderName;

    /**
     * Effettua un backup incrementale
     * @param sourceFolder
     */
    private void startIncremental(File sourceFolder) {
        if (sourceFolder.isDirectory()) {
            File[] sourceFiles = sourceFolder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (isIgnored(sourceFile)) continue;

                    // Cerchiamo il file nella cartella del backup completo
                    File completeFile = new File(sourceFile.getAbsolutePath().replace(
                            source, completeBackupFolder.getAbsolutePath()
                    ));
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                            source, Utils.concatPath(destination,incrementalFolderName)
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
                    catch (Exception e){System.out.println(e.getMessage());}
                }
            }
        }

        // Iteriamo su ogni file del backup completo
        // Cerchiamo il file nella cartella sorgente
            // Se il file non è presente, ne creiamo uno vuoto marchiato* nella cartella di backup incrementale
    }

    private void startIncrementalDeleted(File completeFolder){
        if (completeFolder.isDirectory()) {
            File[] completeFiles = completeFolder.listFiles();
            if (completeFiles != null) {
                for (File completeFile : completeFiles) {
                    if (isIgnored(completeFile)) continue;
                    // Cerchiamo il file nella cartella attuale
                    File sourceFile = new File(completeFile.getAbsolutePath().replace(
                            Utils.concatPath(destination,folderName), source
                    ));
                    File delFile = new File(Utils.concatPath(
                            destination,
                            Utils.concatPath(incrementalFolderName, DELETED_FILES_FILE_NAME)));

                    /* File rimossi */
                    try {
                        if (completeFile.isFile()){
                            // Se il file è stato eliminato
                            if (!sourceFile.exists()){
                                if (!delFile.exists()) delFile.createNewFile();
                                String f = completeFile.getAbsolutePath().replace(
                                        Utils.concatPath(destination,folderName), ""
                                ) + "\n";
                                Files.write(
                                    delFile.toPath(),
                                    f.getBytes(),
                                    StandardOpenOption.APPEND
                                );
                                System.out.println(delFile.getAbsolutePath());
                            }
                        }
                        else if (completeFile.isDirectory()){
                            startIncrementalDeleted(completeFile);
                        }
                    }
                    catch (Exception e){System.out.println(e.getMessage());}
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

        File folder = new File(destination);
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
        return folderName + " (" + max_number + ")";
    }

    /**
     * Cerca e restituisce il numero di backup (incrementale) nel nome della cartella
     * @param folder nome della cartella
     * @return il numero di backup
     */
    private int getIncrementalBackupFolderNumber(String folder){
        String regex = folderName + "\\s*\\(\\s*(\\d+)\\s*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(folder);
        if (matcher.matches()) {
            String extractedNumber = matcher.group(1);
            return Integer.parseInt(extractedNumber) + 1;
        } else {
            return MIN_INCREMENTAL_NUMBER;
        }
    }

    private boolean isIgnored(File file){
        return file.getName().equals(DELETED_FILES_FILE_NAME);
    }
}
