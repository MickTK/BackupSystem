package it.backup.system.configuration;

import it.backup.system.configuration.backup.Backup;
import it.backup.system.configuration.schedule.Schedule;

public class BackupConfiguration {

    String name;       // Nome della configurazione (nome della cartella di backup)
    Backup backup;     // Informazioni sul backup
    Schedule schedule; // Informazioni sulla pianificazione

    // Getters and setters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Backup getBackup() {
        return backup;
    }
    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    public Schedule getSchedule() {
        return schedule;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
