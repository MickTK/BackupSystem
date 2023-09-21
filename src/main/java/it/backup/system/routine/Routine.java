package it.backup.system.routine;

import it.backup.system.Application;
import it.backup.system.configuration.BackupConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Compara le pianificazioni di backup con il timestamp attuale (precisione al minuto)
 */
public class Routine implements Runnable {
    String lastCheck = null;                 // Variabile di confronto (tiene la data attuale)
    String datePattern = "dd/MM/yyyy HH:mm"; // Patterna data
    public static boolean canRun = true;     // Se il thread deve continuare l'esecuzione

    /**
     * Effettua i controlli sulle pianificazioni di backup
     */
    public void run() {
        while (canRun) {
            try {
                Thread.sleep(Application.sleepingTimeRoutine);
            } catch (InterruptedException e) { throw new RuntimeException(e); }
            // Salva il timestamp attuale
            String time = DateTimeFormatter.ofPattern(datePattern).format(LocalDateTime.now());
            // Effettua un controllo al minuto
            if (!time.equals(lastCheck)) {
                lastCheck = time;
                synchronized (Application.class) {
                    for (BackupConfiguration conf : Application.scheduler.configurations) {
                        // Se la pianificazione viene rispettata
                        if (conf.getSchedule().check()) {
                            synchronized (BackgroundProcessor.class) {
                                // Aggiunge il backup alla coda dei backup da effettuare
                                BackgroundProcessor.addBackup(conf.getBackup());
                            }
                        }
                    }
                }
            }
        }
    }
}
