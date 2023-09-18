package it.backup.system.configuration.schedule;

import it.backup.system.utils.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Informazioni pianificazione mensile
 */
public class MonthlySchedule {
    public List<Integer> days;
    public List<String> clock;

    public MonthlySchedule() {
        days = new ArrayList<>();
        clock = new ArrayList<>();
    }

    /**
     * Controlla se la pianificazione è soddisfatta per il timestamp corrente
     * @return stato
     */
    public boolean check() {
        LocalDateTime now = LocalDateTime.now();
        if (days.contains(now.getDayOfMonth())) {
            for (String h : clock) {
                if (LocalTime.parse(h).equals(now.toLocalTime())) {
                    return true;
                }
            }
        }
        return false;
    }
}
