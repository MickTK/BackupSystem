package it.backup.system.configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
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
        // Recupera le configurazioni dal file
        File file = new File(configFilePath);
        if (!file.exists()) return;
        String data = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                data += scanner.nextLine();
            }
        }
        catch (Exception e) {e.printStackTrace();}
        if (!data.isEmpty()) {
            try {
                Type listType = new TypeToken<ArrayList<BackupConfigurationData>>(){}.getType();
                List<BackupConfigurationData> confDatas = new Gson().fromJson(data, listType);
                for (BackupConfigurationData d : confDatas) {
                    configurations.add(new BackupConfiguration(d));
                }
            }
            catch (Exception e) {
                System.out.println("Il file di configurazione è corrotto e per tanto non verrà caricato.");
            }
        }
    }

    /**
     * Salva lo scheduler su file
     */
    public void saveToFile() {
        List<BackupConfigurationData> dataC = new ArrayList<>();
        for (BackupConfiguration conf : configurations)
            dataC.add(conf.toData());
        String data = new Gson().toJson(dataC);
        File file = new File(configFilePath);
        try {
            if (!file.exists()) file.createNewFile();
            Files.write(
                    file.toPath(),
                    "".getBytes(),
                    StandardOpenOption.WRITE
            );
            Files.write(
                    file.toPath(),
                    data.getBytes(),
                    StandardOpenOption.WRITE
            );
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
