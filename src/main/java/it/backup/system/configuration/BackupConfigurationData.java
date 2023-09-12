package it.backup.system.configuration;

import it.backup.system.configuration.backup.BackupType;
import it.backup.system.configuration.schedule.ScheduleType;
import it.backup.system.configuration.schedule.WeekDay;

import java.time.LocalTime;
import java.util.List;

public class BackupConfigurationData {
    public String name;

    public BackupType backupType;
    public String sourcePath;
    public String destinationPath;

    public ScheduleType scheduleType;
    public List<WeekDay> weekDays;
    public List<LocalTime> weekClock;
    public List<Integer> monthDays;
    public List<LocalTime> monthClock;
}
