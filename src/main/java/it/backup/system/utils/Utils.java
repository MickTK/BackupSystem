package it.backup.system.utils;

import javafx.scene.control.TextArea;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Utils {

    /**
     * Effettua dei controlli di validità sulla cartella di origine
     * @param path
     * @return
     */
    public static boolean isSourcePathValid(File path){
        return isSourcePathValid(path,null);
    }
    public static boolean isSourcePathValid(File path, TextArea textArea){
        if (path == null){
            addLog(textArea,"Il percorso della cartella di origine non è valido.");
            return false;
        }
        if (!path.exists()){
            addLog(textArea, "La cartella di origine non esiste.");
        }
        if (!path.isDirectory()){
            addLog(textArea,"Il file selezionato non è una cartella.");
            return false;
        }
        return true;
    }

    /**
     * Effettua dei controlli di validità sulla cartella di destinazione
     * @param path
     * @return
     */
    public static boolean isDestinationPathValid(File path){
        return isDestinationPathValid(path,null);
    }
    public static boolean isDestinationPathValid(File path, TextArea textArea){
        if (path == null){
            addLog(textArea,"Percorso non valido.");
            return false;
        }
        if (!path.exists()){
            addLog(textArea, "La cartella di destinazione non esiste.");
        }
        if (!path.isDirectory()){
            addLog(textArea,"Il file selezionato non è una cartella.");
            return false;
        }
        return true;
    }

    /**
     * Restituisce il numero di file della cartella e delle sue sotto-cartelle
     * @param folder
     * @return
     */
    public static Integer numberOfFiles(File folder){
        int counter = 0;
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        counter += numberOfFiles(file);
                    }
                    else if(file.isFile()){
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    /**
     * Restituisce il numero di sotto-cartelle della cartella radice
     * @param folder
     * @return
     */
    public static Integer numberOfFolders(File folder){
        int counter = 0;
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        counter++;
                        counter += numberOfFolders(file);
                    }
                }
            }
        }
        return counter;
    }

    /**
     * Aggiunge del testo in coda al box che compare nell'interfaccia utente
     * @param textArea
     * @param text
     */
    public static void addLog(TextArea textArea, String text){
        if (textArea != null && text != null)
            textArea.appendText(text + "\n");
    }

    /**
     * Concatena delle stringhe separandole con degli slash (/)
     * @param names
     * @return
     */
    public static String concatPath(String... names){
        String path = null;

        for (int i = 0; i < names.length; i ++){
            if (i == 0)
                path = names[i];
            else if (i > 0)
                path += "/" + names[i];
        }

        return path;
    }

    /**
     * Ottiene il timestamp attuale (dd/mm/yyyy)
     * @return timestamp
     */
    public static String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
