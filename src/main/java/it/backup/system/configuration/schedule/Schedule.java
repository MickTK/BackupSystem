package it.backup.system.configuration.schedule;

/**
 * Informazioni pianificazione
 */
public class Schedule {
    ScheduleType scheduleType;       // Tipo di pianificazione: nessuna, settimanale, mensile
    WeeklySchedule weeklySchedule;   // Informazioni pianificazione settimanale
    MonthlySchedule monthlySchedule; // Informazioni pianificazione mensile

    public ScheduleType getScheduleType() {
        return scheduleType;
    }
    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public WeeklySchedule getWeeklySchedule() {
        return weeklySchedule;
    }
    public void setWeeklySchedule(WeeklySchedule weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }

    public MonthlySchedule getMonthlySchedule() {
        return monthlySchedule;
    }
    public void setMonthlySchedule(MonthlySchedule monthlySchedule) {
        this.monthlySchedule = monthlySchedule;
    }

    /**
     * Controllo pianificazione
     * @return restituisce true se la pianificazione può essere eseguita in questo momento, false altrimenti
     */
    public boolean check() {
        switch (scheduleType) {
            default:
            case None: return false;
            case Weekly: return weeklySchedule.check();
            case Monthly: return monthlySchedule.check();
        }
    }
}
