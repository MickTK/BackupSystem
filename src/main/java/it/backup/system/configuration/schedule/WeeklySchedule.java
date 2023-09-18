package it.backup.system.configuration.schedule;

import it.backup.system.utils.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Informazioni pianificazione settimanale
 */
public class WeeklySchedule {
    public List<WeekDay> weekDays;
    public List<String> clock;

    public WeeklySchedule() {
        weekDays = new ArrayList<>();
        clock = new ArrayList<>();
    }

    /**
     * Controlla se la pianificazione è soddisfatta per il timestamp corrente
     * @return stato
     */
    public boolean check(){
        LocalDateTime now = LocalDateTime.now();
        WeekDay today = Utils.dayOfWeek(now.getDayOfWeek());
        if (weekDays.contains(today)) {
            for (String h : clock) {
                if (h.equals(DateTimeFormatter.ofPattern("HH:mm").format(now)))
                    return true;
            }
        }
        return false;
    }
}
