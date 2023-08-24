package it.backup.system.zip;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip {

    private ZipFile zipFile;

    public Zip(String path) {
        try {
            zipFile = new ZipFile(path);
        }
        catch (Exception e) { zipFile = null; }
    }

    public ZipEntry getEntry(String relativePathToFile) {
        return zipFile.getEntry(relativePathToFile);
    }

    public void extractEntry(ZipEntry zipEntry, String filename) {
        if (zipEntry != null)
            try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                 OutputStream outputStream = new FileOutputStream(filename)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("File extracted successfully.");
            } catch (IOException e) { e.printStackTrace(); }
    }

    /*public long getLastModifiedTime(String relativePathToFile) throws Exception {
        ZipEntry entry = zipFile.getEntry(relativePathToFile);
        long lastModifiedTimestamp;
        if (entry != null) {
            lastModifiedTimestamp = entry.getTime();
            System.out.println("Last Modified Time: " + lastModifiedTimestamp);
        } else { throw new Exception("File " + relativePathToFile + " not found in the zip archive."); }
        return lastModifiedTimestamp;
    }*/

    public static boolean isZip(File file) {
        return file.getName().endsWith(".zip");
    }
}
