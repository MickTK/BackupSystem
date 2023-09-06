package it.backup.system.scheduler;

import java.time.LocalTime;
import java.util.List;

public class MonthlySchedule {
    public List<Integer> days;
    public List<LocalTime> clock;

    public boolean check() {
        return false;
    }
}
