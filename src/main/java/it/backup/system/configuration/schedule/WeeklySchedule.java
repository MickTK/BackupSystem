package it.backup.system.configuration.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Informazioni pianificazione settimanale
 */
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
