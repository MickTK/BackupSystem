package it.backup.system.configuration;

import it.backup.system.configuration.backup.Backup;
import it.backup.system.configuration.schedule.Schedule;

public class BackupConfiguration {
    private String name;
    private Backup backup;
    private Schedule schedule;

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
