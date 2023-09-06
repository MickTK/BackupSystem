package it.backup.system.scheduler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WeeklySchedule {
    public List<WeekDay> weekDays;
    public List<LocalTime> clock;

    public WeeklySchedule() {
        weekDays = new ArrayList<>();
        clock = new ArrayList<>();
    }

    public boolean check(){
        return false;
    }
}
