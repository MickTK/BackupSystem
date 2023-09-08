package it.backup.system;

import it.backup.system.configuration.BackupConfiguration;
import it.backup.system.configuration.backup.*;
import it.backup.system.configuration.schedule.*;
import it.backup.system.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.lang.module.Configuration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Controller {

    BackupType backupType;
    ScheduleType scheduleType;
    int configurationIndex;

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
    @FXML private AnchorPane weeklyPanel;
    @FXML private AnchorPane monthlyPanel;
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
     * Inizializzazione dei comandi del controller
     */
    public void initialize() {
        // Attributi
        weekDays = Arrays.asList(sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck);
        monthDays = Arrays.asList(
                day1Check, day2Check, day3Check, day4Check, day5Check, day6Check, day7Check,
                day8Check, day9Check, day10Check, day11Check, day12Check, day13Check, day14Check,
                day15Check, day16Check, day17Check, day18Check, day19Check, day20Check, day21Check,
                day22Check, day23Check, day24Check, day25Check, day26Check, day27Check, day28Check,
                day29Check, day30Check, day31Check, day32Check
        );
        // Configurazione
        cleanConfiguration();
        initializeBackupController();
        initializeScheduleController();
        // Operazioni
        saveButton.setOnAction(this::save);
        startButton.setOnAction(this::start);
        deleteButton.setOnAction(this::delete);
    }

    //*********************************************************************************************
    /** Comandi **/
    //*********************************************************************************************
    /**/

    //*************************************************************************
    /** Backup **/
    //*************************************************************************
    /**
     * Inizializza i comandi relativi al backup
     */
    private void initializeBackupController() {
        // Inizializza la lista delle configurazioni nel check box
        refreshConfigurationBox();
        configurationBox.setOnAction(this::loadConfiguration);
        // Imposta una nuova configurazione per la pianificazione
        newConfigurationButton.setOnAction(event -> {
            cleanConfiguration();
        });
        // Tipo di backup
        completeButton.setOnAction(event -> {
            setBackupRadioButton(BackupType.Complete);
        });
        differentialButton.setOnAction(event -> {
            setBackupRadioButton(BackupType.Differential);
        });
        incrementalButton.setOnAction(event -> {
            setBackupRadioButton(BackupType.Incremental);
        });
        // Selezione percorso della cartella da salvare
        chooseSourcePath.setOnAction(event -> {
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Scegli la cartella che vuoi salvare.");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            sourcePath.setText(directoryChooser.showDialog(primaryStage).getAbsolutePath());
        });
        // Selezione percorso della cartella di destinazione
        chooseDestinationPath.setOnAction(event -> {
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Scegli una cartella di destinazione.");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            destinationPath.setText(directoryChooser.showDialog(primaryStage).getAbsolutePath());
        });
    }
    /**
     * Aggiorna l'interfaccia con le informazioni della configurazione selezionata dal box
     * @param event
     */
    private void loadConfiguration(ActionEvent event) {
        BackupConfiguration configuration = null;
        Backup backup = null;
        Schedule schedule = null;

        // Impedisce di eseguire il metodo se la configurazione è vuota
        if (configurationBox.getValue() == null || configurationBox.getValue().isBlank()) return;

        // Recupera la configurazione selezionata nel box dalla lista di configurazioni
        for (int i = 0; i < Application.scheduler.configurations.size(); i++) {
            configuration = Application.scheduler.configurations.get(i);
            if (configuration.getName().equals(configurationBox.getValue())) {
                schedule = configuration.getSchedule();
                backup = configuration.getBackup();
                configurationIndex = i;
                break;
            }
        }
        if (schedule == null || backup == null) return;

        loadBackupConfiguration(backup);
        loadScheduleConfiguration(schedule);
    }
    /**
     * Aggiorna l'interfaccia del backup
     * @param backup
     */
    private void loadBackupConfiguration(Backup backup) {
        // Imposta il tipo di backup
        setBackupRadioButton(backup.getBackupType());
        // Imposta i percorsi (sorgente e destinazione)
        sourcePath.setText(backup.getSourceFolder().getAbsolutePath());
        destinationPath.setText(backup.getDestinationFolder().getAbsolutePath());
    }
    /**
     * Aggiorna l'interfaccia della pianificazione
     * @param schedule
     */
    private void loadScheduleConfiguration(Schedule schedule) {
        // Imposta il tipo di pianificazione
        setScheduleRadioButton(schedule.getScheduleType());
        // Imposta i giorni della settimana della pianificazione
        for (WeekDay weekDay : schedule.getWeeklySchedule().weekDays) {
            switch (weekDay) {
                case Sunday: sundayCheck.setSelected(true); break;
                case Monday: mondayCheck.setSelected(true); break;
                case Tuesday: tuesdayCheck.setSelected(true); break;
                case Wednesday: wednesdayCheck.setSelected(true); break;
                case Thursday: thursdayCheck.setSelected(true); break;
                case Friday: fridayCheck.setSelected(true); break;
                case Saturday: saturdayCheck.setSelected(true); break;
            }
        }
        // Imposta gli orari della pianificazione settimanale
        for (LocalTime time : schedule.getWeeklySchedule().clock) {
            timesWeeklyField.getItems().add(time.toString());
        }
        // Imposta i giorni del mese della pianificazione
        for (int day : schedule.getMonthlySchedule().days) {
            monthDays.get(day - 1).setSelected(true);
        }
        // Imposta gli orari della pianificazione mensile
        for (LocalTime time : schedule.getMonthlySchedule().clock) {
            timesMonthlyField.getItems().add(time.toString());
        }
    }
    /**
     * Ricarica le configurazioni nel box
     */
    private void refreshConfigurationBox() {
        configurationBox.getItems().clear();
        for (BackupConfiguration configuration : Application.scheduler.configurations){
            configurationBox.getItems().add(configuration.getName());
        }
    }
    /**
     * Seleziona il pulsante del tipo di backup
     * @param backupType tipo di backup selezionato
     */
    private void setBackupRadioButton(BackupType backupType) {
        if (this.backupType != backupType) this.backupType = backupType;
        completeButton.setSelected(this.backupType.equals(BackupType.Complete));
        differentialButton.setSelected(this.backupType.equals(BackupType.Differential));
        incrementalButton.setSelected(this.backupType.equals(BackupType.Incremental));
    }

    //*************************************************************************
    /** Pianificazione **/
    //*************************************************************************
    private void initializeScheduleController() {
        // Tipo di pianificazione
        noneButton.setOnAction(event -> {
            setScheduleRadioButton(ScheduleType.None);
        });
        weeklyButton.setOnAction(event -> {
            setScheduleRadioButton(ScheduleType.Weekly);
        });
        monthlyButton.setOnAction(event -> {
            setScheduleRadioButton(ScheduleType.Monthly);
        });
        // Pianificazione settimanale
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
        // Pianificazione mensile
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
    }
    private void setScheduleRadioButton(ScheduleType scheduleType) {
        if (this.scheduleType != scheduleType) this.scheduleType = scheduleType;
        noneButton.setSelected(
                this.scheduleType.equals(ScheduleType.None)
        );
        weeklyButton.setSelected(this.scheduleType.equals(ScheduleType.Weekly));
        monthlyButton.setSelected(this.scheduleType.equals(ScheduleType.Monthly));

        weeklyPanel.setDisable(!this.scheduleType.equals(ScheduleType.Weekly));
        monthlyPanel.setDisable(!this.scheduleType.equals(ScheduleType.Monthly));
    }

    //*********************************************************************************************
    /** Metodi **/
    //*********************************************************************************************
    /**/

    //*************************************************************************
    /** Ripristino configurazione **/
    //*************************************************************************
    /**
     * Ripristina la configurazione mostrata a schermo
     */
    private void cleanConfiguration(){
        configurationIndex = -1;
        refreshConfigurationBox();
        cleanBackupConfiguration();
        cleanScheduleConfiguration();
    }
    /**
     * Ripristina la configurazione del backup mostrata a schermo
     */
    private void cleanBackupConfiguration() {
        // Box di configurazione
        configurationBox.setValue(null);
        // Tipo di backup
        setBackupRadioButton(BackupType.Complete);
        // Percorsi
        sourcePath.setText(null);
        destinationPath.setText(null);
    }
    /**
     * Ripristina la configurazione sulla pianificazione mostrata a schermo
     */
    private void cleanScheduleConfiguration() {
        // Tipo di pianificazione
        setScheduleRadioButton(ScheduleType.None);
        // Pannello di pianificazione settimanale
        for (CheckBox box : weekDays) {
            box.setSelected(false);
        }
        timesWeeklyField.getItems().clear();
        // Pannello di pianificazione mensile
        for (CheckBox box : monthDays) {
            box.setSelected(false);
        }
        timesMonthlyField.getItems().clear();
    }

    //*************************************************************************
    /** Operazioni **/
    //*************************************************************************
    /**
     * Esegue la configurazione corrente
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
    /**
     * Salva la configurazione corrente
     * @param event
     */
    private void save(ActionEvent event) {
        try {
            BackupConfiguration configuration = new BackupConfiguration();
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
            configuration.setName(backup.name());
            configuration.setBackup(backup);
            // Imposta le informazioni sulla pianificazione
            Schedule schedule = new Schedule();
            schedule.setScheduleType(scheduleType);
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
            configuration.setSchedule(schedule);

            if (configurationIndex > -1) {
                Application.scheduler.configurations.set(configurationIndex, configuration);
            }
            else {
                Application.scheduler.configurations.add(configuration);
                configurationIndex = Application.scheduler.configurations.size() - 1;
            }
            int s = configurationIndex;
            cleanConfiguration();
            configurationBox.setValue(Application.scheduler.configurations.get(s).getName());
        } catch (Exception e) { e.printStackTrace(); }
    }
    /**
     * Elimina la configurazione corrente
     * @param event
     */
    private void delete(ActionEvent event) {
        if (configurationIndex > -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Eliminazione pianificazione.");
            alert.setContentText(
                    "Sei sicuro di voler eliminare la pianificazione per il backup: " /*+ backup.name()*/ + "?"
            );
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Application.scheduler.configurations.remove(configurationIndex);
                    cleanConfiguration();
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
