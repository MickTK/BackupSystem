package it.backup.system.backup;

import it.backup.system.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;

public class DifferentialBackup extends Backup {

    public DifferentialBackup(String sourceFolderPath, String destinationFolderPath) throws Exception {
        super(sourceFolderPath, destinationFolderPath, BackupType.Differential);
    }

    @Override
    public void start() throws Exception {
        String temp = getLatestBackupName(sourceFolder.getName(), BackupType.Complete);
        previousBackupFolder = new File(Utils.combine(destinationFolder.getAbsolutePath(), temp));
        if (previousBackupFolder.exists()){
            if (previousBackupFolder.isDirectory()) {
                backupFolder = new File(Utils.combine(
                        destinationFolder.getAbsolutePath(),
                        backupNameBuilder(temp))
                );
                if (backupFolder.mkdir()) {
                    createDeletedFilesFile();
                    saveModifiedFilesRecursively(new File(sourceFolder.getAbsolutePath()));
                    saveDeletedFilesLogRecursively(previousBackupFolder);
                    /*if (Utils.numberOfFiles(backupFolder) == 0 && Utils.numberOfFolders(backupFolder) == 0)
                        backupFolder.delete();*/
                }
            }
                    /*else if (Zip.isZip(previousBackupFolder)) {
                        if (Zip.isZip(previousBackupFolder))
                            previousBackupFolderZip = new Zip(previousBackupFolder.getAbsolutePath());
                        backupFolder = new File(Utils.combine(
                                destinationFolder.getAbsolutePath(),
                                backupNameBuilder(temp))
                        );
                        if (backupFolder.mkdir()) {
                            startDifferentialZip(new File(sourceFolder.getAbsolutePath()));
                            //startDifferentialDeleted(previousBackupFolder);
                            if (Utils.numberOfFiles(backupFolder) == 0 && Utils.numberOfFolders(backupFolder) == 0)
                                backupFolder.delete();
                        }
                    }*/
            else { throw new Exception(previousBackupFolder + " is not a directory."); }
        }
        else { throw new Exception(previousBackupFolder + " does not exists."); }
    }

    /**
     * Effettua un backup differenziale (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    void saveModifiedFilesRecursively(@NotNull File folder) {
        if (folder.isDirectory()) {
            File[] sourceFiles = folder.listFiles();
            if (sourceFiles != null) {
                for (File sourceFile : sourceFiles) {
                    if (isIgnored(sourceFile)) continue;
                    // Cerchiamo il file nella cartella dell'ultimo backup effettuato
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
                            saveModifiedFilesRecursively(sourceFile);
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
    void saveDeletedFilesLogRecursively(@NotNull File folder){
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
                    // Se il file è stato rimosso
                    if (!sourceFile.exists()){
                        // Aggiunge il file rimosso alla lista dei file rimossi
                        writeOnDeletedFilesFile(file);
                    }
                    // Se il file non è stato rimosso ed è una cartella
                    else if (file.isDirectory()){
                        // Itera sul contenuto della cartella
                        saveDeletedFilesLogRecursively(file);
                    }
                }
            }
        }
    }

}
