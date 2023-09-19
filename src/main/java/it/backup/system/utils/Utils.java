package it.backup.system.utils;

import it.backup.system.configuration.schedule.WeekDay;
import javafx.scene.control.TextArea;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
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
    public static String combine(String... names){
        StringBuilder path = null;

        if (names.length > 0)
            path = new StringBuilder(names[0]);
        for (int i = 1; i < names.length; i ++){
            path.append("/").append(names[i]);
        }

        return path.toString();
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

    /**
     * Calcola la differenza tra le date delle ultime modifiche di due file
     * @param file1
     * @param file2
     * @return "> 0" il file 1 è stato modificato prima del file 2
     * @throws Exception
     */
    public static int lastModifiedDateCompare(File file1, File file2) throws Exception {
        Path file1Path = file1.toPath();
        Path file2Path = file2.toPath();

        BasicFileAttributes attributes1 = Files.readAttributes(file1Path, BasicFileAttributes.class);
        FileTime lastModifiedTime1 = attributes1.lastModifiedTime();
        Instant instant1 = lastModifiedTime1.toInstant();
        LocalDateTime lastModifiedDateTime1 = instant1.atZone(ZoneId.systemDefault()).toLocalDateTime();

        BasicFileAttributes attributes2 = Files.readAttributes(file2Path, BasicFileAttributes.class);
        FileTime lastModifiedTime2 = attributes2.lastModifiedTime();
        Instant instant2 = lastModifiedTime2.toInstant();
        LocalDateTime lastModifiedDateTime2 = instant2.atZone(ZoneId.systemDefault()).toLocalDateTime();

        return lastModifiedDateTime2.compareTo(lastModifiedDateTime1);
    }

    /**
     * Converte un valore DayOfWeek in WeekDay
     * @param weekDay valore da convertire
     * @return valore corrispondente
     */
    public static WeekDay dayOfWeek(DayOfWeek weekDay) {
        switch (weekDay) {
            case SUNDAY: return WeekDay.Sunday;
            case MONDAY: return WeekDay.Monday;
            case TUESDAY: return WeekDay.Tuesday;
            case WEDNESDAY: return WeekDay.Wednesday;
            case THURSDAY: return WeekDay.Thursday;
            case FRIDAY: return WeekDay.Friday;
            case SATURDAY: return WeekDay.Saturday;
            default: return null;
        }
    }
}
