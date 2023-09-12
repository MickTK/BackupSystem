package it.backup.system.configuration;

import it.backup.system.configuration.backup.Backup;
import it.backup.system.configuration.backup.CompleteBackup;
import it.backup.system.configuration.backup.DifferentialBackup;
import it.backup.system.configuration.backup.IncrementalBackup;
import it.backup.system.configuration.schedule.MonthlySchedule;
import it.backup.system.configuration.schedule.Schedule;
import it.backup.system.configuration.schedule.WeeklySchedule;

public class BackupConfiguration {

    String name;       // Nome della configurazione (nome della cartella di backup)
    Backup backup;     // Informazioni sul backup
    Schedule schedule; // Informazioni sulla pianificazione

    public BackupConfiguration() { }
    public BackupConfiguration (BackupConfigurationData data) {
        name = data.name;
        try {
            switch (data.backupType) {
                default:
                case Complete:
                    backup = new CompleteBackup(data.sourcePath, data.destinationPath);
                    break;
                case Differential:
                    backup = new DifferentialBackup(data.sourcePath, data.destinationPath);
                    break;
                case Incremental:
                    backup = new IncrementalBackup(data.sourcePath, data.destinationPath);
                    break;
            }
        } catch (Exception e) {e.printStackTrace();}
        schedule = new Schedule();
        schedule.setScheduleType(data.scheduleType);
        WeeklySchedule weeklySchedule = new WeeklySchedule();
        weeklySchedule.weekDays = data.weekDays;
        weeklySchedule.clock = data.weekClock;
        schedule.setWeeklySchedule(weeklySchedule);
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        monthlySchedule.days = data.monthDays;
        monthlySchedule.clock = data.monthClock;
        schedule.setMonthlySchedule(monthlySchedule);
    }

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

    public BackupConfigurationData toData() {
        BackupConfigurationData data = new BackupConfigurationData();
        data.name = name;
        data.backupType = backup.getBackupType();
        data.sourcePath = backup.getSourceFolder().getAbsolutePath();
        data.destinationPath = backup.getDestinationFolder().getAbsolutePath();;
        data.scheduleType = schedule.getScheduleType();
        data.weekDays = schedule.getWeeklySchedule().weekDays;
        data.weekClock = schedule.getWeeklySchedule().clock;
        data.monthDays = schedule.getMonthlySchedule().days;
        data.monthClock = schedule.getMonthlySchedule().clock;
        return data;
    }
}
