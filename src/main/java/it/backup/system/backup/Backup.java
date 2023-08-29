package it.backup.system.backup;

import it.backup.system.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Backup {

    /* Macros */
    public final String DELETED_FILES_FILE_NAME = "_deleted_._files_"; // Nome del file che tiene traccia dei file eliminati
    public final String REGEX = "(.*?)\\s*\\(b\\.(\\d+)\\.(\\d+)\\.(\\d+)\\)"; // Esempio: "Nome backup (b.1.2.3)"

    /* Attributes */
    private BackupType backupType; // Rappresenta il tipo di backup da effettuare (completo, incrementale, differenziale)

    File sourceFolder;         // Cartella da salvare
    File backupFolder;         // Cartella del backup corrente
    File previousBackupFolder; // Ultimo backup effettuato
    File destinationFolder;    // Cartella di destinazione
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
        this.backupFolder = null;
        this.previousBackupFolder = null;
        this.deletedFilesFile = null;
        this.backupType = type;
    }

    public Backup(String sourceFolderPath, String destinationFolderPath) throws Exception {
        sourceFolder = new File(sourceFolderPath);
        if (!sourceFolder.exists() || !sourceFolder.isDirectory())
            throw new Exception(sourceFolderPath + " does not exists or it is not a directory.");
        this.destinationFolder = new File(destinationFolderPath);
        if (!destinationFolder.exists() || !destinationFolder.isDirectory())
            throw new Exception(destinationFolderPath + " does not exists or it is not a directory.");
        this.backupFolder = null;
        this.previousBackupFolder = null;
        this.deletedFilesFile = null;
    }

    /* Backup */

    /**
     * Effettua un backup della sorgente nella destinazione in base al tipo di backup
     * @throws Exception non esiste un backup precedente (per il differenziale/incrementale)
     */
    public void start() throws Exception { }

    /* Deleted files file */

    void createDeletedFilesFile(){
        try {
            deletedFilesFile = new File(Utils.combine(backupFolder.getAbsolutePath(), DELETED_FILES_FILE_NAME));
            if (!deletedFilesFile.exists())
                deletedFilesFile.createNewFile();
        }
        catch(Exception e){e.printStackTrace();}
    }
    void writeOnDeletedFilesFile(File file){
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
    void writeOnDeletedFilesFile(List<String> list){
        try {
            String f = String.join("\n", list);
            Files.write(
                    deletedFilesFile.toPath(),
                    f.getBytes(),
                    StandardOpenOption.APPEND
            );
        }
        catch(Exception e){e.printStackTrace();}
    }

    /* Utils */

    String backupNameBuilder(String name, int completeVersion, int differentialVersion, int incrementalVersion){
        return String.format("%s (b.%d.%d.%d)", getBackupName(name), completeVersion, differentialVersion, incrementalVersion);
    }

    /**
     * Crea il nome della cartella di backup in base al tipo di backup da effettuare
     * @param name nome della sorgente
     * @return
     */
    String backupNameBuilder(String name){
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
                differentialVersion = 0;
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
        Pattern pattern = Pattern.compile(REGEX);
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
    int getBackupVersion(String name, BackupType backupType){
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches()) {
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
    String getLatestBackupName(String name, BackupType backupType){
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
                differentialVersion = 0;
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
    boolean isIgnored(@NotNull File file){
        return file.getName().equals(DELETED_FILES_FILE_NAME);
    }

    /**
     * Crea un file compresso della cartella sorgente nella cartella di destinazione
     * @param deleteDecompressedFolder se la cartella originale decompressa deve essere eliminata
     */
    public void createZip(boolean deleteDecompressedFolder){
        if (!backupFolder.exists()) return;
        String zipFilePath = backupFolder.getAbsolutePath() + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipDirectory(backupFolder, backupFolder.getName(), zipOut);
                if (deleteDecompressedFolder) backupFolder.delete();
            } catch (IOException e) { e.printStackTrace(); }
        } catch (IOException e) { e.printStackTrace(); }
    }
    public void createZip(){
        createZip(false);
    }

    /**
     * Comprime le cartelle e le sotto-cartelle
     * @param folder
     * @param parentFolder
     * @param zipOut
     * @throws IOException
     */
    private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zipOut) throws IOException {
        File[] files = folder.listFiles();
        byte[] buffer = new byte[1024];

        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zipOut);
            }
            else {
                try(FileInputStream fis = new FileInputStream(file)) {
                    zipOut.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, length);
                    }
                }
            }
        }
    }
}
