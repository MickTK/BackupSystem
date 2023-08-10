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
    private void startComplete(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    File copyFile = new File(file.getAbsolutePath().replace(
                            source,
                            Utils.concatPath(destination,folderName)
                    ));

                    if (file.isDirectory()) {
                        if(!Files.exists(copyFile.toPath())) copyFile.mkdir();
                        startComplete(file);
                    }
                    else if (file.isFile()) {
                        try{Files.copy(file.toPath(), copyFile.toPath());}
                        catch(Exception e){System.out.println(e.getMessage());}
                    }
                }
            }
        }
    }
}
