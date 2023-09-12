package it.backup.system.configuration;

import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Collezione di configurazioni
 */
public class Scheduler {
    // Percorso del file di configurazione
    public static final String configFilePath = System.getProperty("user.dir") + "/scheduler.config";

    public List<BackupConfiguration> configurations;

    /**
     * Costruttore
     */
    public Scheduler() {
        configurations = new ArrayList<>();
    }

    public void saveToFile() {
        String data = new Gson().toJson(this);
        File file = new File(configFilePath);
        try {
            if (!file.exists()) file.createNewFile();
            Files.write(
                    file.toPath(),
                    data.getBytes(),
                    StandardOpenOption.WRITE
            );
        }
        catch (Exception e) {e.printStackTrace();}
    }
    public static Scheduler loadFromFile() {
        File file = new File(configFilePath);
        if (!file.exists()) return null;
        String data = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                data += scanner.nextLine();
            }
        }
        catch (Exception e) {e.printStackTrace();}
        if (data.length() > 0) {
            return new Gson().fromJson(data, Scheduler.class);
        }
        else return null;
    }
}
