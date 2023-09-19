package it.backup.system.configuration.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
        int lastDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        if (days.contains(now.getDayOfMonth()) || (days.contains(32) && now.getDayOfMonth() == lastDay)) {
            for (String h : clock) {
                if (h.equals(DateTimeFormatter.ofPattern("HH:mm").format(now))) {
                    return true;
                }
            }
        }
        return false;

    }
}
