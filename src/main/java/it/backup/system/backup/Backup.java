package it.backup.system.backup;

import it.backup.system.utils.Utils;

import java.io.File;
import java.nio.file.Files;

public class Backup {
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
            case Incremental: break;
            case Differential: break;
            default: break;
        }
    }

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

    /**
     * Effettua un backup incrementale
     * @param sourceFolder
     */
    private void startIncremental(File sourceFolder) {
        // Iteriamo su ogni file della sorgente
        // Cerchiamo il file nella cartella del backup completo
            // Se il file non è presente, lo creiamo nella cartella di backup incrementale
            // Se il file è presente ma la data di modifica è precedente alla data di modifica della sorgente, lo salva nella cartella di backup incrementale
        // Iteriamo su ogni file del backup completo
        // Cerchiamo il file nella cartella sorgente
            // Se il file non è presente, ne creiamo uno vuoto marchiato* nella cartella di backup incrementale




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
}
