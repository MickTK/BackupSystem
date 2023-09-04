package it.backup.system.scheduler;

import java.time.LocalTime;
import java.util.List;

public class MonthlySchedule {
    List<Integer> days;
    List<LocalTime> clock;

    public boolean check() {
        return false;
    }
}
