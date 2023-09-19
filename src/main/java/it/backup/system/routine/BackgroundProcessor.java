package it.backup.system.routine;

import it.backup.system.configuration.backup.Backup;

import java.util.ArrayList;
import java.util.List;

public class BackgroundProcessor implements Runnable {
    static List<Backup> list = new ArrayList<>(); // Backup da effettuare
    public static boolean canRun = true;          // Se il thread deve continuare l'esecuzione

    /**
     * Viene aggiunto un backup alla coda dei backup da effettuare
     * @param backup backup da aggiungere
     */
    public static void addBackup(Backup backup) {
        list.add(backup);
    }

    /**
     * Esegue i backup della coda in ordine di aggiunta (FIFO)
     */
    public void run() {
        while (canRun) {
            synchronized (BackgroundProcessor.class) {
                try {
                    if (!list.isEmpty()) {
                        list.get(0).start(); // Effettua il primo backup della cods
                        list.remove(0);      // Rimuove il primo backup dalla coda
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}
