package it.backup.system.utils;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.*;
import java.util.zip.*;

public class Utils {

    /** Controlli sulla cartella di origine **/
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

    /** Controlli sulla cartella di destinazione **/
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

    /** Restituisce il numero di file e cartelle trovate **/
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

    /** Log **/
    public static void addLog(TextArea textArea, String text){
        if (textArea != null && text != null)
            textArea.appendText(text + "\n");
    }


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










    public static boolean createZip(File source, File destination){
        if (!isSourcePathValid(source) || !isDestinationPathValid(destination)) return false;

        String zipFilePath = destination.getAbsolutePath() + "/" + source.getName() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipDirectory(source, source.getName(), zipOut);
                return true;
            } catch (IOException e) { e.printStackTrace(); }
        } catch (IOException e) { e.printStackTrace(); }
        return true;
    }

    public static void zipDirectory(File folder, String parentFolder, ZipOutputStream zipOut) throws IOException {
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
