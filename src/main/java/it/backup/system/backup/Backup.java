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
    private final String DELETED_FILES_FILE_NAME = "_deleted_._files_"; // Nome del file che tiene traccia dei file eliminati

    /* Attributes */
    private BackupType backupType; // Rappresenta il tipo di backup da effettuare (completo, incrementale, differenziale)

    private File sourceFolder;         // Cartella da salvare
    private File backupFolder;         // Cartella del backup corrente
    private File previousBackupFolder; // Ultimo backup effettuato
    private File destinationFolder;    // Cartella di destinazione
    private File deletedFilesFile;     // File che tiene traccia dei file eliminati rispetto al backup completo

    /**
     * Costruttore
     * @param sourceFolderPath cartella da salvare
     * @param destinationFolderPath cartella che conterrò il backup
     * @param type tipo di backup da effettuare
     * @throws Exception almeno una delle date cartelle non esiste
     */
    public Backup(String sourceFolderPath, String destinationFolderPath, @NotNull BackupType type) throws Exception {
        sourceFolder = new File(sourceFolderPath);
        if (!sourceFolder.exists() || !sourceFolder.isDirectory())
            throw new Exception(sourceFolderPath + " does not exists or it is not a directory.");
        this.destinationFolder = new File(destinationFolderPath);
        if (!destinationFolder.exists() || !destinationFolder.isDirectory())
            throw new Exception(destinationFolderPath + " does not exists or it is not a directory.");
        this.backupType = type;
    }

    /* Backup */

    /**
     * Effettua un backup della sorgente nella destinazione in base al tipo di backup
     * @throws Exception non esiste un backup precedente (per il differenziale/incrementale)
     */
    public void start() throws Exception {
        String temp;
        switch (backupType){
            case Complete:
                backupFolder = new File(Utils.combine(
                    destinationFolder.getAbsolutePath(),
                    backupNameBuilder(sourceFolder.getName()))
                );
                if (backupFolder.mkdir())
                    startComplete(sourceFolder);
                break;
            case Differential:
                temp = getLatestBackupName(sourceFolder.getName(), BackupType.Complete);
                previousBackupFolder = new File(Utils.combine(destinationFolder.getAbsolutePath(), temp));
                if (previousBackupFolder.exists() && previousBackupFolder.isDirectory()){
                    backupFolder = new File(Utils.combine(
                        destinationFolder.getAbsolutePath(),
                        backupNameBuilder(temp))
                    );
                    if (backupFolder.mkdir()){
                        startDifferential(new File(sourceFolder.getAbsolutePath()));
                        startDifferentialDeleted(previousBackupFolder);
                        if(Utils.numberOfFiles(backupFolder) == 0 && Utils.numberOfFolders(backupFolder) == 0)
                            backupFolder.delete();
                    }
                }
                else { throw new Exception(previousBackupFolder + " does not exists or it is not a directory."); }
                break;
            case Incremental:
                temp = getLatestBackupName(sourceFolder.getName(), BackupType.Incremental);
                previousBackupFolder = new File(Utils.combine(destinationFolder.getAbsolutePath(), temp));
                if (previousBackupFolder.exists() && previousBackupFolder.isDirectory()){
                    backupFolder = new File(Utils.combine(
                            destinationFolder.getAbsolutePath(),
                            backupNameBuilder(temp))
                    );
                    if (backupFolder.mkdir()){
                        startIncremental(sourceFolder);
                        startIncrementalDeleted(previousBackupFolder);
                        if(Utils.numberOfFiles(backupFolder) == 0 && Utils.numberOfFolders(backupFolder) == 0)
                            backupFolder.delete();
                    }
                }
                else { throw new Exception(previousBackupFolder + " does not exists or it is not a directory."); }
                break;
            default: break;
        }
    }

    /* Complete backup */

    /**
     * Effettua un backup completo (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    private void startComplete(@NotNull File folder) {
        if (folder.isDirectory()) {
            File[] sourceFiles = folder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    // Crea un file con il percorso di destinazione
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                        sourceFolder.getAbsolutePath(),
                        backupFolder.getAbsolutePath()
                    ));
                    // Se il file è una cartella, la crea
                    if (sourceFile.isDirectory()) {
                        if(!Files.exists(destinationFile.toPath())) destinationFile.mkdir();
                        startComplete(sourceFile);
                    }
                    // Se il file non è una cartella, copia il contenuto del file originale nella cartella di destinazione
                    else if (sourceFile.isFile()) {
                        try{Files.copy(sourceFile.toPath(), destinationFile.toPath());}
                        catch(Exception e){ e.printStackTrace(); }
                    }
                }
            }
        }
    }

    /* Differential backup */

    /**
     * Effettua un backup differenziale (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    private void startDifferential(@NotNull File folder) {
        if (folder.isDirectory()) {
            File[] sourceFiles = folder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (isIgnored(sourceFile)) continue;

                    // Cerchiamo il file nella cartella del backup completo
                    File previousFile = new File(sourceFile.getAbsolutePath().replace(
                        sourceFolder.getAbsolutePath(),
                        previousBackupFolder.getAbsolutePath()
                    ));
                    File destinationFile = new File(sourceFile.getAbsolutePath().replace(
                        sourceFolder.getAbsolutePath(),
                        backupFolder.getAbsolutePath()
                    ));

                    /* File creati/modificati */
                    try {
                        if (sourceFile.isFile()){
                            // Se il file è stato creato/modificato
                            if (!previousFile.exists() || (previousFile.exists() && Utils.lastModifiedDateCompare(sourceFile, previousFile) < 0)){
                                if(!destinationFile.getParentFile().exists())
                                    destinationFile.getParentFile().mkdirs();
                                Files.copy(sourceFile.toPath(), destinationFile.toPath());
                            }
                        }
                        else if (sourceFile.isDirectory()){
                            startDifferential(sourceFile);
                        }
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        }
    }

    /**
     * Salva tutti i nomi dei file eliminati in un file (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    private void startDifferentialDeleted(@NotNull File folder){
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isIgnored(file)) continue;

                    // Viene cercato il file nella cartella sorgente
                    File sourceFile = new File(file.getAbsolutePath().replace(
                        previousBackupFolder.getAbsolutePath(),
                        sourceFolder.getAbsolutePath()
                    ));
                    System.out.println(sourceFile.getAbsolutePath());
                    // Se il file è stato rimosso
                    if (!sourceFile.exists()){
                        // Aggiunge il file rimosso alla lista dei file rimossi
                        createDeletedFilesFile();
                        writeOnDeletedFilesFile(file);
                    }
                    // Se il file non è stato rimosso ed è una cartella
                    else if (file.isDirectory()){
                        // Itera sul contenuto della cartella
                        startDifferentialDeleted(file);
                    }
                }
            }
        }
    }

    /* Incremental Backup */

    /**
     * Effettua un backup incrementale (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    private void startIncremental(@NotNull File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isIgnored(file)) continue;

                    // Cerchiamo il file nella cartella dell'ultimo backup
                    File previousFile = new File(file.getAbsolutePath().replace(
                        sourceFolder.getAbsolutePath(),
                        previousBackupFolder.getAbsolutePath()
                    ));
                    File destinationFile = new File(file.getAbsolutePath().replace(
                        sourceFolder.getAbsolutePath(),
                        backupFolder.getAbsolutePath()
                    ));

                    /* File creati/modificati */
                    try {
                        if (file.isFile()){
                            // Se il file è stato creato/modificato
                            if (Utils.lastModifiedDateCompare(file, previousBackupFolder) < 0){
                                if(!destinationFile.getParentFile().exists())
                                    destinationFile.getParentFile().mkdirs();
                                Files.copy(file.toPath(), destinationFile.toPath());
                            }
                        }
                        else if (file.isDirectory()){
                            startIncremental(file);
                        }
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        }
    }

    /**
     * Salva tutti i nomi dei file eliminati in un file (il metodo è ricorsivo)
     * @param completeFolder cartella corrente
     */
    private void startIncrementalDeleted(@NotNull File completeFolder){
        if (completeFolder.isDirectory()) {
            File[] completeFiles = completeFolder.listFiles();
            if (completeFiles != null) {
                for (File completeFile : completeFiles) {
                    if (isIgnored(completeFile)) continue;

                    // Viene cercato il file nella cartella sorgente
                    File sourceFile = new File(completeFile.getAbsolutePath().replace(
                            Utils.combine(
                                    destinationFolder.getAbsolutePath(),
                                    backupFolder.getName()
                            ),
                            sourceFolder.getAbsolutePath()
                    ));

                    // Se il file è stato rimosso
                    if (!sourceFile.exists()){
                        // Aggiunge il file rimosso alla lista dei file rimossi
                        createDeletedFilesFile();
                        writeOnDeletedFilesFile(completeFile);
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

    /* Deleted files file */

    private void createDeletedFilesFile(){
        try {
            deletedFilesFile = new File(Utils.combine(backupFolder.getAbsolutePath(), DELETED_FILES_FILE_NAME));
            System.out.println(deletedFilesFile.getAbsolutePath());
            if (!deletedFilesFile.exists())
                deletedFilesFile.createNewFile();
        }
        catch(Exception e){e.printStackTrace();}
    }
    private void writeOnDeletedFilesFile(File file){
        try {
            String f = file.getAbsolutePath().replace(previousBackupFolder.getAbsolutePath(),"") + "\n";
            Files.write(
                    deletedFilesFile.toPath(),
                    f.getBytes(),
                    StandardOpenOption.APPEND
            );
        }
        catch(Exception e){e.printStackTrace();}
    }

    /* Utils */

    /**
     * Crea il nome della cartella di backup in base al tipo di backup da effettuare
     * @param name nome della sorgente
     * @return
     */
    private String backupNameBuilder(String name){
        int completeVersion;
        int differentialVersion;
        int incrementalVersion;

        switch (backupType){
            case Complete:
                completeVersion = getLatestCompleteBackupVersion() + 1;
                differentialVersion = 0;
                incrementalVersion = 0;
                break;
            case Differential:
                completeVersion = getLatestCompleteBackupVersion();
                differentialVersion = getLatestDifferentialBackupVersion(completeVersion) + 1;
                incrementalVersion = 0;
                break;
            case Incremental:
                completeVersion = getLatestCompleteBackupVersion();
                differentialVersion = getLatestDifferentialBackupVersion(completeVersion);
                incrementalVersion = getLatestIncrementalBackupVersion(completeVersion) + 1;
                break;
            default:
                throw new ExceptionInInitializerError("Attribute \"type\" is null.");
        }

        return String.format("%s (b.%d.%d.%d)", getBackupName(name), completeVersion, differentialVersion, incrementalVersion);
    }

    /**
     * Ottiene il nome del backup senza la versione
     * @param name nome completo del backup (con versione)
     * @return nome del backup (senza versione)
     */
    private String getBackupName(String name){
        String regex = "(.*?)\\s*\\(b\\.(\\d+)\\.(\\d+)\\.(\\d+)\\)"; // Esempio: "Nome backup (b.1.2.3)"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches()) {
            return matcher.group(1);
        }
        return name;
    }

    /**
     * Ottiene la versione dal nome di un backup
     * @param name nome del backup completo (con versione)
     * @param backupType tipo di versione
     * @return numero di versione
     */
    private int getBackupVersion(String name, BackupType backupType){
        String regex = "(.*?)\\s*\\(b\\.(\\d+)\\.(\\d+)\\.(\\d+)\\)"; // Esempio: "Nome backup (b.1.2.3)"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches()) {
            // String genericName = matcher.group(1);
            switch(backupType){
                case Complete: return Integer.parseInt(matcher.group(2));     // Numero di backup completo (primo valore numerico)
                case Differential: return Integer.parseInt(matcher.group(3)); // Numero di backup differenziale (secondo valore numerico)
                case Incremental: return Integer.parseInt(matcher.group(4));  // Numero di backup incrementale (terzo valore numerico)
                default: break;
            }
        } else { System.out.println("La stringa non segue il formato corretto."); }
        return -1;
    }

    /**
     * Ottiene l'ultima versione di backup completo
     * @return versione
     */
    private int getLatestCompleteBackupVersion(){
        int version = 0; // Minimum version
        int current;
        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            File[] destinationFiles = destinationFolder.listFiles();
            if (destinationFiles != null) {
                for (File destinationFile : destinationFiles) {
                    if (destinationFile.isDirectory()){
                        current = getBackupVersion(destinationFile.getName(), BackupType.Complete);
                        if (current > version){
                            version = current;
                        }
                    }
                }
            }
        }
        return version;
    }

    /**
     * Ottiene l'ultima versione di backup differenziale
     * @param completeVersion versione di backup completo
     * @return
     */
    private int getLatestDifferentialBackupVersion(int completeVersion){
        int version = 0; // Minimum version
        int current;

        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            File[] destinationFiles = destinationFolder.listFiles();
            if (destinationFiles != null) {
                for (File destinationFile : destinationFiles) {
                    if (destinationFile.isDirectory()){
                        if (getBackupVersion(destinationFile.getName(), BackupType.Complete) == completeVersion){
                            current = getBackupVersion(destinationFile.getName(), BackupType.Differential);
                            if (current > version){
                                version = current;
                            }
                        }
                    }
                }
            }
        }
        return version;
    }

    /**
     * Ottiene l'ultima versione di backup incrementale
     * @param completeVersion versione di backup completo
     * @return
     */
    private int getLatestIncrementalBackupVersion(int completeVersion){
        int version = 0; // Minimum version
        int current;

        if (destinationFolder.exists() && destinationFolder.isDirectory()) {
            File[] destinationFiles = destinationFolder.listFiles();
            if (destinationFiles != null) {
                for (File destinationFile : destinationFiles) {
                    if (destinationFile.isDirectory()){
                        if (getBackupVersion(destinationFile.getName(), BackupType.Complete) == completeVersion){
                            current = getBackupVersion(destinationFile.getName(), BackupType.Incremental);
                            if (current > version){
                                version = current;
                            }
                        }
                    }
                }
            }
        }
        return version;
    }

    /**
     * Ottiene il nome completo (con versione) dell'ultimo backup effettuato
     * @param name nome del backup (senza versione)
     * @param backupType tipo di backup
     * @return nome completo dell'ultimo backup effettuato
     */
    private String getLatestBackupName(String name, BackupType backupType){
        int completeVersion;
        int differentialVersion;
        int incrementalVersion;

        switch (backupType){
            case Complete:
                completeVersion = getLatestCompleteBackupVersion();
                differentialVersion = 0;
                incrementalVersion = 0;
                break;
            case Differential:
                completeVersion = getLatestCompleteBackupVersion();
                differentialVersion = getLatestDifferentialBackupVersion(completeVersion);
                incrementalVersion = 0;
                break;
            case Incremental:
                completeVersion = getLatestCompleteBackupVersion();
                differentialVersion = getLatestDifferentialBackupVersion(completeVersion);
                incrementalVersion = getLatestIncrementalBackupVersion(completeVersion);
                break;
            default:
                throw new ExceptionInInitializerError("Attribute \"type\" is null.");
        }

        if (completeVersion < 1)
            return null;
        else
            return String.format("%s (b.%d.%d.%d)", name, completeVersion, differentialVersion, incrementalVersion);
    }

    /**
     * Confronta il nome del file passato come input con i nomi dei file da ignorare
     * @param file file da controllare
     * @return true se il file è da ignorare, altrimenti false
     */
    private boolean isIgnored(@NotNull File file){
        return file.getName().equals(DELETED_FILES_FILE_NAME);
    }
}
