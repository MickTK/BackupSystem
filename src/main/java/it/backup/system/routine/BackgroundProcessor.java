package it.backup.system.routine;

import it.backup.system.configuration.backup.Backup;

import java.util.ArrayList;
import java.util.List;

public class BackgroundProcessor implements Runnable {
    static List<Backup> list = new ArrayList<>();
    public static boolean canRun = true;

    public static void addBackup(Backup backup) {
        list.add(backup);
    }

    public void run() {
        while (canRun) {
            synchronized (BackgroundProcessor.class) {
                try {
                    if (!list.isEmpty()) {
                        list.get(0).start();
                        list.remove(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
