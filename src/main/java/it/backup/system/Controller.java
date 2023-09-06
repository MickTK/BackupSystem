package it.backup.system;

import it.backup.system.backup.*;
import it.backup.system.scheduler.*;
import it.backup.system.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Controller {

    Schedule currentSchedule;
    BackupType backupType;
    ScheduleType scheduleType;
    int scheduleIndex;

    /** Backup **/
    @FXML private ChoiceBox<String> configurationBox;    // Mostra il nome della configurazione corrente
    @FXML private Button newConfigurationButton; // Aggiunge una configurazione nuova (e vuota)
    @FXML private TextField sourcePath;      // Campo di testo contenente il percorso della cartella da salvare
    @FXML private TextField destinationPath; // Campo di testo contente il percorso della cartella in cui salvara la precedente cartella
    @FXML private RadioButton completeButton;     // Seleziona il tipo di backup completo
    @FXML private RadioButton differentialButton; // Seleziona il tipo di backup differenziale
    @FXML private RadioButton incrementalButton;  // Seleziona il tipo di backup incrementale
    @FXML private Button chooseSourcePath;      // Seleziona il percorso della cartella sorgente
    @FXML private Button chooseDestinationPath; // Seleziona il percorso della cartella di destinazione

    /** Pianificazione **/
    @FXML private RadioButton noneButton;    // Seleziona la pianificazione nulla
    @FXML private RadioButton weeklyButton;  // Seleziona la pianificazione settimanale
    @FXML private RadioButton monthlyButton; // Seleziona la pianificazione mensile
    @FXML private GridPane weeklyGrid;
    @FXML private GridPane monthlyGrid;
    @FXML private AnchorPane weeklyPane;
    @FXML private AnchorPane monthlyPane;
    // Giorni della settimana
    private List<CheckBox> weekDays;
    @FXML private CheckBox sundayCheck;    // Seleziona la domenica come giorno di backup
    @FXML private CheckBox mondayCheck;    // Seleziona il lunedì come giorno di backup
    @FXML private CheckBox tuesdayCheck;   // Seleziona il martedì come giorno di backup
    @FXML private CheckBox wednesdayCheck; // Seleziona il mercoledì come giorno di backup
    @FXML private CheckBox thursdayCheck;  // Seleziona il giovedì come giorno di backup
    @FXML private CheckBox fridayCheck;    // Seleziona il venerdì come giorno di backup
    @FXML private CheckBox saturdayCheck;  // Seleziona il sabato come giorno di backup
    // Giorni del mese
    private List<CheckBox> monthDays;
    @FXML private CheckBox day1Check;
    @FXML private CheckBox day2Check;
    @FXML private CheckBox day3Check;
    @FXML private CheckBox day4Check;
    @FXML private CheckBox day5Check;
    @FXML private CheckBox day6Check;
    @FXML private CheckBox day7Check;
    @FXML private CheckBox day8Check;
    @FXML private CheckBox day9Check;
    @FXML private CheckBox day10Check;
    @FXML private CheckBox day11Check;
    @FXML private CheckBox day12Check;
    @FXML private CheckBox day13Check;
    @FXML private CheckBox day14Check;
    @FXML private CheckBox day15Check;
    @FXML private CheckBox day16Check;
    @FXML private CheckBox day17Check;
    @FXML private CheckBox day18Check;
    @FXML private CheckBox day19Check;
    @FXML private CheckBox day20Check;
    @FXML private CheckBox day21Check;
    @FXML private CheckBox day22Check;
    @FXML private CheckBox day23Check;
    @FXML private CheckBox day24Check;
    @FXML private CheckBox day25Check;
    @FXML private CheckBox day26Check;
    @FXML private CheckBox day27Check;
    @FXML private CheckBox day28Check;
    @FXML private CheckBox day29Check;
    @FXML private CheckBox day30Check;
    @FXML private CheckBox day31Check;
    @FXML private CheckBox day32Check; // Ultimo giorno del mese
    // Orari settimanali
    @FXML private TextField timeHourWeeklyField;
    @FXML private TextField timeMinuteWeeklyField;
    @FXML private Button plusWeeklyButton;
    @FXML private Button minusWeeklyButton;
    @FXML private ListView<String> timesWeeklyField;
    // Orari mensili
    @FXML private TextField timeHourMonthlyField;
    @FXML private TextField timeMinuteMonthlyField;
    @FXML private Button plusMonthlyButton;
    @FXML private Button minusMonthlyButton;
    @FXML private ListView<String> timesMonthlyField;

    /** Altro **/
    @FXML private TextArea consoleLog; // Area di testo che conterrà i messaggi visualizzabili a schermo
    @FXML private ProgressBar progressBar; // Barra che mostra il progresso dell'operazione in corso
    @FXML private Button processButton; // Pulsante che effettua o salva le informazioni correnti
    @FXML private Button saveButton;
    @FXML private Button startButton;
    @FXML private Button deleteButton;

    /**
     * Inizializza il controller
     */
    public void initialize() {
        weekDays = Arrays.asList(sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck);
        monthDays = Arrays.asList(
                day1Check, day2Check, day3Check, day4Check, day5Check, day6Check, day7Check,
                day8Check, day9Check, day10Check, day11Check, day12Check, day13Check, day14Check,
                day15Check, day16Check, day17Check, day18Check, day19Check, day20Check, day21Check,
                day22Check, day23Check, day24Check, day25Check, day26Check, day27Check, day28Check,
                day29Check, day30Check, day31Check, day32Check
        );

        setDefaultInformations();

        /** Backup **/
        // Inizializza la lista delle pianificazioni nel check box
        refreshConfigurationBox();
        configurationBox.setOnAction(this::setConfiguration);
        // Imposta una nuova configurazione per la pianificazione
        newConfigurationButton.setOnAction(event -> {
            setDefaultInformations();
        });
        // Selezione tipo di backup
        completeButton.setOnAction(this::setCompleteBackup);
        differentialButton.setOnAction(this::setDifferentialBackup);
        incrementalButton.setOnAction(this::setIncrementalBackup);
        // Selezione percorso della cartella da salvare
        chooseSourcePath.setOnAction(this::selectSourcePath);
        // Selezione percorso della cartella di destinazione
        chooseDestinationPath.setOnAction(this::selectDestinationPath);

        /** Pianificazione **/
        noneButton.setOnAction(this::setNoneSchedule);
        weeklyButton.setOnAction(this::setWeeklySchedule);
        monthlyButton.setOnAction(this::setMonthlySchedule);

        // Orario settimanale
        timeHourWeeklyField.textProperty().addListener((ov, oldValue, newValue) -> {
            int length = 2;
            String s = newValue;
            // Rimuove tutti i caratteri che non sono dei numeri
            if (!newValue.matches("\\d*")) s = newValue.replaceAll("[^\\d]", "");
            // Tiene solo due caratteri per volta
            if (s.length() > length) s = s.substring(0, length);
            // Imposta 23 come orario massimo
            else if (s.length() == length && Integer.parseInt(s) > 23) s = "23";
            timeHourWeeklyField.setText(s);
        });
        timeMinuteWeeklyField.textProperty().addListener((ov, oldValue, newValue) -> {
            int length = 2;
            String s = newValue;
            // Rimuove tutti i caratteri che non sono dei numeri
            if (!newValue.matches("\\d*")) s = newValue.replaceAll("[^\\d]", "");
            // Tiene solo due caratteri per volta
            if (s.length() > length) s = s.substring(0, length);
            // Imposta 59 come orario massimo
            else if (s.length() == length && Integer.parseInt(s) > 59) s = "59";
            timeMinuteWeeklyField.setText(s);
        });
        plusWeeklyButton.setOnAction(event -> {
            // Aggiunge l'orario alla lista visualizzata
            String s = "";
            if (timeHourWeeklyField.getText().length() < 2) s += "0";
            s += timeHourWeeklyField.getText();
            s += ":";
            if (timeMinuteWeeklyField.getText().length() < 2) s += "0";
            s += timeMinuteWeeklyField.getText();
            // Controlla se esiste già l'orario impostato
            if (!timesWeeklyField.getItems().contains(s))
                timesWeeklyField.getItems().add(s);
        });
        minusWeeklyButton.setOnAction(event -> {
            timesWeeklyField.getItems().removeAll(timesWeeklyField.getSelectionModel().getSelectedItem());
        });

        // Orario mensile
        timeHourMonthlyField.textProperty().addListener((ov, oldValue, newValue) -> {
            String s = newValue;
            // Rimuove tutti i caratteri che non sono dei numeri
            if (!newValue.matches("\\d*")) s = newValue.replaceAll("[^\\d]", "");
            // Tiene solo due caratteri per volta
            if (s.length() > 2) s = s.substring(0, 2);
                // Imposta 23 come orario massimo
            else if (s.length() == 2 && Integer.parseInt(s) > 23) s = "23";
            timeHourMonthlyField.setText(s);
        });
        timeMinuteMonthlyField.textProperty().addListener((ov, oldValue, newValue) -> {
            String s = newValue;
            // Rimuove tutti i caratteri che non sono dei numeri
            if (!newValue.matches("\\d*")) s = newValue.replaceAll("[^\\d]", "");
            // Tiene solo due caratteri per volta
            if (s.length() > 2) s = s.substring(0, 2);
                // Imposta 59 come orario massimo
            else if (s.length() == 2 && Integer.parseInt(s) > 59) s = "59";
            timeMinuteMonthlyField.setText(s);
        });
        plusMonthlyButton.setOnAction(event -> {
            String s = "";
            if (timeHourMonthlyField.getText().length() < 2) s += "0";
            s += timeHourMonthlyField.getText();
            s += ":";
            if (timeMinuteMonthlyField.getText().length() < 2) s += "0";
            s += timeMinuteMonthlyField.getText();
            // Controlla se esiste già l'orario impostato
            if (!timesMonthlyField.getItems().contains(s))
                timesMonthlyField.getItems().add(s);
        });
        minusMonthlyButton.setOnAction(event -> {
            timesMonthlyField.getItems().removeAll(timesMonthlyField.getSelectionModel().getSelectedItem());
        });

        //processButton.setOnAction(this::process);
        saveButton.setOnAction(this::save);
        startButton.setOnAction(this::start);
        deleteButton.setOnAction(this::delete);
    }

    private void setDefaultInformations(){
        currentSchedule = null;
        scheduleIndex = -1;

        configurationBox.setValue(null);
        sourcePath.setText(null);
        destinationPath.setText(null);

        backupType = BackupType.Complete;
        completeButton.setSelected(true);
        differentialButton.setSelected(false);
        incrementalButton.setSelected(false);

        scheduleType = ScheduleType.None;
        noneButton.setSelected(true);
        weeklyButton.setSelected(false);
        monthlyButton.setSelected(false);

        for (CheckBox box : weekDays) {
            box.setSelected(false);
        }
        for (CheckBox box : monthDays) {
            box.setSelected(false);
        }
        timesWeeklyField.getItems().clear();
        timesMonthlyField.getItems().clear();
    }

    /**
     * Recupera e salva il percorso della cartella da salvare
     * @param event
     */
    private void selectSourcePath(ActionEvent event){
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli la cartella che vuoi salvare.");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        sourcePath.setText(directoryChooser.showDialog(primaryStage).getAbsolutePath());
    }

    /**
     * Recupera e salva il percorso della cartella di destinazione
     * @param event
     */
    private void selectDestinationPath(ActionEvent event) {
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli una cartella di destinazione.");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        destinationPath.setText(directoryChooser.showDialog(primaryStage).getAbsolutePath());
    }

    private void refreshConfigurationBox() {
        for (Schedule schedule : Application.scheduler.schedules){
            configurationBox.getItems().add(schedule.getName());
        }
    }

    /**
     * Imposta le informazioni della configurazione selezionata dal box
     * @param event
     */
    private void setConfiguration(ActionEvent event) {
        Schedule s;

        currentSchedule = null;
        for (int i = 0; i < Application.scheduler.schedules.size(); i++) {
            s = Application.scheduler.schedules.get(i);
            if (s.getName().equals(configurationBox.getValue())) {
                currentSchedule = s;
                scheduleIndex = i;
                break;
            }
        }

        if (currentSchedule == null) return;

        // Imposta il tipo di backup
        switch (currentSchedule.getBackup().getBackupType()) {
            default:
            case Complete:
                setCompleteBackup(null);
                break;
            case Differential:
                setDifferentialBackup(null);
                break;
            case Incremental:
                setIncrementalBackup(null);
                break;
        }
        // Imposta i percorsi (sorgente e destinazione)
        sourcePath.setText(currentSchedule.getBackup().getSourceFolder().getAbsolutePath());
        destinationPath.setText(currentSchedule.getBackup().getDestinationFolder().getAbsolutePath());
        // Imposta il tipo di pianificazione
        switch (currentSchedule.getScheduleType()) {
            default:
            case None:
                setNoneSchedule(null);
                break;
            case Weekly:
                setWeeklySchedule(null);
                break;
            case Monthly:
                setMonthlySchedule(null);
                break;
        }
        // Imposta i giorni della settimana della pianificazione
        for (WeekDay weekDay : currentSchedule.getWeeklySchedule().weekDays) {
            switch (weekDay) {
                case Sunday: sundayCheck.setSelected(true); break;
                case Monday: mondayCheck.setSelected(true); break;
                case Tuesday: tuesdayCheck.setSelected(true); break;
                case Wednesday: wednesdayCheck.setSelected(true); break;
                case Thursday: thursdayCheck.setSelected(true); break;
                case Friday: fridayCheck.setSelected(true); break;
                case Saturday: saturdayCheck.setSelected(true); break;
                default: break;
            }
        }
        // Imposta i giorni del mese della pianificazione
        for (int index : currentSchedule.getMonthlySchedule().days) {
            monthDays.get(index-1).setSelected(true);
        }
        // Imposta gli orari della pianificazione settimanale
        for (LocalTime time : currentSchedule.getWeeklySchedule().clock) {
            timesWeeklyField.getItems().add(time.toString());
        }
        // Imposta gli orari della pianificazione mensile
        for (LocalTime time : currentSchedule.getMonthlySchedule().clock) {
            timesMonthlyField.getItems().add(time.toString());
        }
    }

    // Imposta il tipo di backup da effettuare
    private void setCompleteBackup(ActionEvent event) {
        if (completeButton.isSelected()) {
            backupType = BackupType.Complete;
            differentialButton.setSelected(false);
            incrementalButton.setSelected(false);
        }
        else
            completeButton.setSelected(true);
    }
    private void setDifferentialBackup(ActionEvent event) {
        if (differentialButton.isSelected()) {
            backupType = BackupType.Differential;
            completeButton.setSelected(false);
            incrementalButton.setSelected(false);
        }
        else
            differentialButton.setSelected(true);
    }
    private void setIncrementalBackup(ActionEvent event) {
        if (incrementalButton.isSelected()) {
            backupType = BackupType.Incremental;
            differentialButton.setSelected(false);
            completeButton.setSelected(false);
        }
        else
            incrementalButton.setSelected(true);
    }

    // Imposta il tipo di pianificazione
    private void setNoneSchedule(ActionEvent event) {
        if (noneButton.isSelected()) {
            // Imposta il tipo di pianificazione
            scheduleType = ScheduleType.None;
            // Modifica lo stato degli altri bottoni
            weeklyButton.setSelected(false);
            monthlyButton.setSelected(false);
            // Modifica la visibilità dei pannelli di pianificazione
            weeklyGrid.setDisable(true);
            weeklyPane.setDisable(true);
            monthlyGrid.setDisable(true);
            monthlyPane.setDisable(true);
        }
        else {
            // Reimposta lo stesso bottone se già selezionato
            noneButton.setSelected(true);
        }
    }
    private void setWeeklySchedule(ActionEvent event) {
        if (weeklyButton.isSelected()) {
            scheduleType = ScheduleType.Weekly;
            noneButton.setSelected(false);
            monthlyButton.setSelected(false);
            weeklyGrid.setDisable(false);
            weeklyPane.setDisable(false);
            monthlyGrid.setDisable(true);
            monthlyPane.setDisable(true);
        }
        else {
            weeklyButton.setSelected(true);
        }
    }
    private void setMonthlySchedule(ActionEvent event) {
        if (monthlyButton.isSelected()) {
            scheduleType = ScheduleType.Monthly;
            noneButton.setSelected(false);
            weeklyButton.setSelected(false);
            weeklyGrid.setDisable(true);
            weeklyPane.setDisable(true);
            monthlyGrid.setDisable(false);
            monthlyPane.setDisable(false);
        }
        else {
            monthlyButton.setSelected(true);
        }
    }




    /**
     * Inizia il processo di backup
     * @param event
     */
    private void start(ActionEvent event){
        BackupType backupType;
        if (differentialButton.isSelected())
            backupType = BackupType.Differential;
        else if (incrementalButton.isSelected())
            backupType = BackupType.Incremental;
        else
            backupType = BackupType.Complete;

        if (Utils.isSourcePathValid(new File(sourcePath.getText()),consoleLog) &&
            Utils.isDestinationPathValid(new File(destinationPath.getText()),consoleLog)){
            try {
                Backup backup;
                switch (backupType) {
                    default:
                    case Complete:
                        backup = new CompleteBackup(sourcePath.getText(), destinationPath.getText());
                        break;
                    case Differential:
                        backup = new DifferentialBackup(sourcePath.getText(), destinationPath.getText());
                        break;
                    case Incremental:
                        backup = new IncrementalBackup(sourcePath.getText(), destinationPath.getText());
                        break;
                }

                consoleLog.setText("");
                log("Backup in corso.");
                //log("Numero di cartelle trovate: " + Utils.numberOfFolders(source) + ".");
                //log("Numero di file trovati: " + Utils.numberOfFiles(source) + ".");

                long lastDeltaTime = System.nanoTime();
                backup.start();
                long deltaTime = (long) ((System.nanoTime() - lastDeltaTime) / 1_000_000_000.0D);
                log("Backup completato in " + deltaTime + " secondi.");
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void save(ActionEvent event) {
        try {
            // Imposta le informazioni del backup
            Backup backup;
            switch (backupType) {
                default:
                case Complete:
                    backup = new CompleteBackup(sourcePath.getText(), destinationPath.getText());
                    break;
                case Differential:
                    backup = new DifferentialBackup(sourcePath.getText(), destinationPath.getText());
                    break;
                case Incremental:
                    backup = new IncrementalBackup(sourcePath.getText(), destinationPath.getText());
                    break;
            }
            // Imposta le informazioni sulla pianificazione
            Schedule schedule = new Schedule(backup, scheduleType);
            schedule.setName(backup.name());
            // Imposta i giorni della settimana
            WeeklySchedule weeklySchedule = new WeeklySchedule();
            if (sundayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Sunday);
            if (mondayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Monday);
            if (tuesdayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Tuesday);
            if (wednesdayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Wednesday);
            if (thursdayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Thursday);
            if (fridayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Friday);
            if (saturdayCheck.isSelected()) weeklySchedule.weekDays.add(WeekDay.Saturday);
            schedule.setWeeklySchedule(weeklySchedule);
            // Imposta gli orari dei giorni della settimana
            for (String hour : timesWeeklyField.getItems()) {
                weeklySchedule.clock.add(LocalTime.parse(hour));
            }
            // Imposta i giorni del mese
            MonthlySchedule monthlySchedule = new MonthlySchedule();
            for (int i = 0; i < monthDays.size(); i++) {
                if (monthDays.get(i).isSelected()) {
                    monthlySchedule.days.add(i+1);
                }
            }
            // Imposta gli orari dei giorni del mese
            for (String hour : timesMonthlyField.getItems()) {
                monthlySchedule.clock.add(LocalTime.parse(hour));
            }
            schedule.setMonthlySchedule(monthlySchedule);

            if (scheduleIndex > -1) {
                Application.scheduler.schedules.set(scheduleIndex, schedule);
            }
            else {
                Application.scheduler.schedules.add(schedule);
                scheduleIndex = Application.scheduler.schedules.size() - 1;
            }
            int s = scheduleIndex;
            setDefaultInformations();
            refreshConfigurationBox();
            configurationBox.setValue(Application.scheduler.schedules.get(s).getName());
            setConfiguration(null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void delete(ActionEvent event) {
        if (scheduleIndex > -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Eliminazione pianificazione.");
            alert.setContentText(
                    "Sei sicuro di voler eliminare la pianificazione per il backup: " /*+ backup.name()*/ + "?"
            );
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Application.scheduler.schedules.remove(scheduleIndex);
                }
            });
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Nessuna pianificazione.");
            alert.setContentText("Non è stata selezionata nessuna pianificazione da eliminare.");
            alert.showAndWait();
        }
    }

    /**
     * Aggiunge del testo in coda al campo di log
     * @param text testo da aggiungere
     */
    private void log(String text){
        Utils.addLog(consoleLog, text);
    }
}
