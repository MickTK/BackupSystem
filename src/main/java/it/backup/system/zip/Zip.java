package it.backup.system.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

    /**
     * Crea un file compresso della cartella sorgente nella cartella di destinazione
     * @param source
     * @param destination
     */
    public static void createZip(File source, File destination){
        String zipFilePath = destination.getAbsolutePath() + "/" + source.getName() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipDirectory(source, source.getName(), zipOut);
            } catch (IOException e) { e.printStackTrace(); }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Comprime le cartelle e le sotto-cartelle
     * @param folder
     * @param parentFolder
     * @param zipOut
     * @throws IOException
     */
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
