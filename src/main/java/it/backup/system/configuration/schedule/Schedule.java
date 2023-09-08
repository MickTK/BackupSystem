package it.backup.system.configuration.schedule;

import it.backup.system.configuration.backup.Backup;

public class Schedule {
    ScheduleType scheduleType;
    WeeklySchedule weeklySchedule;
    MonthlySchedule monthlySchedule;

    public ScheduleType getScheduleType() {
        return scheduleType;
    }
    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public WeeklySchedule getWeeklySchedule() {
        return weeklySchedule;
    }
    public void setWeeklySchedule(WeeklySchedule weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }

    public MonthlySchedule getMonthlySchedule() {
        return monthlySchedule;
    }
    public void setMonthlySchedule(MonthlySchedule monthlySchedule) {
        this.monthlySchedule = monthlySchedule;
    }

    public boolean check() {
        return false;
    }
}
