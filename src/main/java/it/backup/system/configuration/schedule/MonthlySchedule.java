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
    public List<Integer> days; // Giorni del mese in cui eseguire un backup (32 = ultimo giorno del mese)
    public List<String> clock; // Orari della giornata in cui eseguire un backup

    /**
     * Costruttore
     */
    public MonthlySchedule() {
        days = new ArrayList<>();
        clock = new ArrayList<>();
    }

    /**
     * Controllo pianificazione
     * @return restituisce true se la pianificazione può essere eseguita in questo momento, false altrimenti
     */
    public boolean check() {
        LocalDateTime now = LocalDateTime.now();
        int lastDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        // Controllo sul giorno
        if (days.contains(now.getDayOfMonth()) || (days.contains(32) && now.getDayOfMonth() == lastDay)) {
            for (String h : clock) {
                // Controllo sull'orario
                if (h.equals(DateTimeFormatter.ofPattern("HH:mm").format(now))) {
                    return true;
                }
            }
        }
        return false;
    }
}
