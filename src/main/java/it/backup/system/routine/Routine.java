package it.backup.system.routine;

import it.backup.system.Application;
import it.backup.system.configuration.BackupConfiguration;
import it.backup.system.configuration.schedule.ScheduleType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Compara le pianificazioni con il timestamp attuale
 */
public class Routine implements Runnable {
    String lastCheck = null;
    String datePattern = "dd/MM/yyyy HH:mm";
    public boolean canRun = true;

    public void run() {
        while (canRun) {
            // Recupera il timestamp attuale
            String time = DateTimeFormatter.ofPattern(datePattern).format(LocalDateTime.now());
            if (!time.equals(lastCheck)) {
                lastCheck = time;
                synchronized (Application.class) {
                    for (BackupConfiguration conf : Application.scheduler.configurations) {
                        System.out.println(conf.getName() + ": " + conf.getSchedule().getWeeklySchedule().check());
                        if (conf.getSchedule().getScheduleType().equals(ScheduleType.Weekly) &&
                                conf.getSchedule().getWeeklySchedule().check()) {
                            synchronized (BackgroundProcessor.class) {
                                BackgroundProcessor.addBackup(conf.getBackup());
                            }
                        }
                        else if (conf.getSchedule().getScheduleType().equals(ScheduleType.Monthly) &&
                                conf.getSchedule().getMonthlySchedule().check()) {
                            synchronized (BackgroundProcessor.class) {
                                BackgroundProcessor.addBackup(conf.getBackup());
                            }
                        }
                    }
                }
            }
        }
    }
}
