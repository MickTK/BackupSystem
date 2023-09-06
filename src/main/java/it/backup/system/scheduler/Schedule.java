package it.backup.system.scheduler;

import it.backup.system.backup.Backup;

public class Schedule {
    String name;
    Backup backup;
    ScheduleType scheduleType;
    WeeklySchedule weeklySchedule;
    MonthlySchedule monthlySchedule;

    public Schedule(){}
    public Schedule(Backup backup, ScheduleType scheduleType){
        this.backup = backup;
        this.scheduleType = scheduleType;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Backup getBackup(){
        return backup;
    }
    public void setBackup(Backup backup){
        this.backup = backup;
    }

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

    public void save() {
        switch (scheduleType) {
            case None:
                try { backup.start(); }
                catch (Exception e) {e.printStackTrace();}
                break;
            case Weekly:
                try { if (weeklySchedule.check()) backup.start(); }
                catch (Exception e) {e.printStackTrace();}
                break;
            case Monthly:
                try { if (monthlySchedule.check()) backup.start(); }
                catch (Exception e) {e.printStackTrace();}
                break;
            default: break;
        }
    }
}
