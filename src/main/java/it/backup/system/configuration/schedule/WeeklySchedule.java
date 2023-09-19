package it.backup.system.configuration.schedule;

import it.backup.system.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Informazioni pianificazione settimanale
 */
public class WeeklySchedule {
    public List<WeekDay> weekDays; // Giorni della settimana in cui eseguire un backup
    public List<String> clock;     // Orari della giornata in cui effettuare un backup

    /**
     * Costruttore
     */
    public WeeklySchedule() {
        weekDays = new ArrayList<>();
        clock = new ArrayList<>();
    }

    /**
     * Controllo pianificazione
     * @return restituisce true se la pianificazione può essere eseguita in questo momento, false altrimenti
     */
    public boolean check(){
        LocalDateTime now = LocalDateTime.now();
        WeekDay today = Utils.dayOfWeek(now.getDayOfWeek());
        // Controllo sul giorno
        if (weekDays.contains(today)) {
            for (String h : clock) {
                // Controllo sull'orario
                if (h.equals(DateTimeFormatter.ofPattern("HH:mm").format(now)))
                    return true;
            }
        }
        return false;
    }
}
