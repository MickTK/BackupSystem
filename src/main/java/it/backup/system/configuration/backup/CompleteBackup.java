package it.backup.system.configuration.backup;

import it.backup.system.utils.Utils;

import java.io.File;
import java.nio.file.Files;

public class CompleteBackup extends Backup {

    public CompleteBackup(String sourceFolderPath, String destinationFolderPath) throws Exception {
        super(sourceFolderPath, destinationFolderPath, BackupType.Complete);
    }

    /**
     * Effettua un backup completo
     */
    @Override
    public void start(){
        backupFolder = new File(Utils.combine(
                destinationFolder.getAbsolutePath(),
                backupNameBuilder(sourceFolder.getName()))
        );
        if (backupFolder.mkdir())
            saveModifiedFilesRecursively(sourceFolder);
    }

    private void saveModifiedFilesRecursively(File folder){
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
                        saveModifiedFilesRecursively(sourceFile);
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
}
