package it.backup.system.scheduler;

import java.time.LocalTime;
import java.util.List;

public class WeeklySchedule {
    public List<WeekDay> weekDays;
    public List<LocalTime> clock;

    public boolean check(){
        return false;
    }
}
