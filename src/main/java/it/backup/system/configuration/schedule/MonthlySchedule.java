package it.backup.system.configuration.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MonthlySchedule {
    public List<Integer> days;
    public List<LocalTime> clock;

    public MonthlySchedule() {
        days = new ArrayList<>();
        clock = new ArrayList<>();
    }

    public boolean check() {
        return false;
    }
}
