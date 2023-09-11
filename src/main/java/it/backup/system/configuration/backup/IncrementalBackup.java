package it.backup.system.configuration.backup;

import it.backup.system.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncrementalBackup extends Backup {

    public IncrementalBackup(String sourceFolderPath, String destinationFolderPath) throws Exception {
        super(sourceFolderPath, destinationFolderPath, BackupType.Incremental);
    }

    @Override
    public void start() throws Exception {
        String temp = getLatestBackupName(sourceFolder.getName(), BackupType.Incremental);
        previousBackupFolder = new File(Utils.combine(destinationFolder.getAbsolutePath(), temp));
        if (previousBackupFolder.exists() && previousBackupFolder.isDirectory()){
            backupFolder = new File(Utils.combine(
                    destinationFolder.getAbsolutePath(), backupNameBuilder(temp))
            );
            if (backupFolder.mkdir()){
                saveModifiedFilesRecursively(sourceFolder);
                saveDeletedFilesOnLog(getDeletedFiles());
                /*if(Utils.numberOfFiles(backupFolder) == 0 && Utils.numberOfFolders(backupFolder) == 0)
                        backupFolder.delete();*/
            }
        }
        else {
            new CompleteBackup(sourceFolder.getAbsolutePath(), destinationFolder.getAbsolutePath()).start();
        }
    }

    /**
     * Effettua un backup incrementale (il metodo è ricorsivo)
     * @param folder cartella corrente
     */
    private void saveModifiedFilesRecursively(@NotNull File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (shouldBeIgnored(file)) continue;

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
                                writeOnLogFile(destinationFile, backupFolder);
                            }
                        }
                        else if (file.isDirectory()){
                            saveModifiedFilesRecursively(file);
                        }
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        }
    }

    /**
     * Ottiene i file eliminati da salvare con il backup incrementale corrente
     * @return lista di nomi di file (percorso relativo)
     */
    private List<String> getDeletedFiles(){
        String firstBackupFolderName, previousBackupFolderName;
        int currentIncrementalVersion;
        List<String> deletedFiles = new ArrayList<>();

        firstBackupFolderName = getLatestBackupName(sourceFolder.getName(), BackupType.Complete);
        currentIncrementalVersion = getBackupVersion(firstBackupFolderName, BackupType.Incremental);
        previousBackupFolderName = backupNameBuilder(
                sourceFolder.getName(),
                getBackupVersion(firstBackupFolderName, BackupType.Complete),
                getBackupVersion(firstBackupFolderName, BackupType.Differential),
                currentIncrementalVersion
        );
        // Itera su ogni backup incrementale effettuato tra il corrente e quello completo
        while (currentIncrementalVersion < getBackupVersion(backupFolder.getName(), BackupType.Incremental)){
            previousBackupFolder = new File(backupFolder.getAbsolutePath().replace(backupFolder.getName(), previousBackupFolderName));
            deletedFiles.addAll(getDeletedFilesFromPreviousBackup(previousBackupFolder)); // Aggiunge i file che non trova
            clearDeletedFilesList(deletedFiles); // Rimuove i file che sono già stati eliminati
            currentIncrementalVersion++;
            previousBackupFolderName = backupNameBuilder(
                    sourceFolder.getName(),
                    getBackupVersion(firstBackupFolderName, BackupType.Complete),
                    getBackupVersion(firstBackupFolderName, BackupType.Differential),
                    currentIncrementalVersion
            );
        }
        return deletedFiles;
    }

    /**
     * Controlla i file eliminati rispetto alla sorgente
     * @param folder cartella del backup incrementale
     * @return lista dei nomi dei file non presenti nella sorgente (con percorso relativo)
     */
    private List<String> getDeletedFilesFromPreviousBackup(@NotNull File folder){
        List<String> deleted = new ArrayList<>();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (shouldBeIgnored(file)) continue;

                    // Viene cercato il file nella cartella sorgente
                    File sourceFile = new File(file.getAbsolutePath().replace(
                            previousBackupFolder.getAbsolutePath(),
                            sourceFolder.getAbsolutePath()
                    ));
                    // Se il file è stato rimosso
                    if (!sourceFile.exists()){
                        // Aggiunge il file rimosso alla lista dei file rimossi
                        deleted.add(file.getAbsolutePath().replace(previousBackupFolder.getAbsolutePath(), ""));
                    }
                    // Se il file non è stato rimosso ed è una cartella
                    else if (file.isDirectory()){
                        // Itera sul contenuto della cartella
                        deleted.addAll(getDeletedFilesFromPreviousBackup(file));
                    }
                }
            }
        }
        return deleted;
    }

    /**
     * Cancella le copie di file eliminati (presenti nella lista) con lo stesso nome
     * @param list lista aggiornata
     */
    private void clearDeletedFilesList(List<String> list){
        List<String> comp = new ArrayList<>();
        File delFile = new File(Utils.combine(previousBackupFolder.getAbsolutePath(), DELETED_FILES_FILE_NAME));

        // Legge e salva tutti i percorsi dei file eliminati presenti su file
        if (delFile.exists() && delFile.isFile()){
            try(BufferedReader br = new BufferedReader(new FileReader(delFile))) {
                String line = br.readLine();
                while (line != null) {
                    comp.add(line);
                    line = br.readLine();
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        // Rimuove i file già precedentemente eliminati
        list.removeAll(comp);
        // Rimuove i file già precedentemente eliminati se il percorso corrisponde ad uno di quelli precedenti
        for (String c : comp){
            list.removeIf(s -> s.startsWith(c + "/"));
        }
        // Rimuove i duplicati
        Set<String> uniqueStrings = new HashSet<>(list);
        list = new ArrayList<>(uniqueStrings);
    }
}
