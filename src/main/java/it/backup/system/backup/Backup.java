package it.backup.system.backup;

import it.backup.system.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backup {
    private final int MIN_INCREMENTAL_NUMBER = 1;

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
                // Iteriamo su ogni file della sorgente
                for (File sourceFile : sourceFiles) {
                    // Cerchiamo il file nella cartella del backup completo
                    File completeFile = new File(sourceFile.getAbsolutePath().replace(
                            source, completeBackupFolder.getAbsolutePath()
                    ));
                    // Se il file è presente ma la data di modifica è precedente alla data di modifica della sorgente, lo salva nella cartella di backup incrementale
                    if (completeFile.exists()){
                        try{
                            if (Utils.lastModifiedDateCompare(sourceFile, completeFile) < 0){
                                File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                                        source, Utils.concatPath(destination,incrementalFolderName)
                                ));
                                if (sourceFile.isFile()) {
                                    try{Files.copy(sourceFile.toPath(), destinationFile.toPath());}
                                    catch(Exception e){System.out.println(e.getMessage());}
                                }
                                else if (sourceFile.isDirectory()){
                                    destinationFile.mkdir();
                                }
                            }
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    // Se il file non è presente, lo creiamo nella cartella di backup incrementale
                    else {
                        File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                                source, Utils.concatPath(destination,incrementalFolderName)
                        ));
                        if (sourceFile.isDirectory()) {
                            if(!Files.exists(destinationFile.toPath())) destinationFile.mkdir();
                            startIncremental(sourceFile);
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

        // Iteriamo su ogni file del backup completo
        // Cerchiamo il file nella cartella sorgente
            // Se il file non è presente, ne creiamo uno vuoto marchiato* nella cartella di backup incrementale
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
            System.out.println("Numero estratto: " + extractedNumber);
            return Integer.parseInt(extractedNumber) + 1;
        } else {
            return MIN_INCREMENTAL_NUMBER;
        }
    }
}
