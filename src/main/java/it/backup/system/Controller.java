package it.backup.system;

import it.backup.system.backup.*;
import it.backup.system.scheduler.Schedule;
import it.backup.system.scheduler.ScheduleType;
import it.backup.system.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Controller {

    Schedule currentSchedule;
    ScheduleType scheduleType;

    /** Backup **/
    @FXML private ChoiceBox<String> configurationBox;    // Mostra il nome della configurazione corrente
    @FXML private Button newConfigurationButton; // Aggiunge una configurazione nuova (e vuota)
    @FXML private TextField sourceInput;      // Campo di testo contenente il percorso della cartella da salvare
    @FXML private TextField destinationInput; // Campo di testo contente il percorso della cartella in cui salvara la precedente cartella
    @FXML private RadioButton completeButton;     // Seleziona il tipo di backup completo
    @FXML private RadioButton differentialButton; // Seleziona il tipo di backup differenziale
    @FXML private RadioButton incrementalButton;  // Seleziona il tipo di backup incrementale
    @FXML private Button chooseSourcePath;      // Seleziona il percorso della cartella sorgente
    @FXML private Button chooseDestinationPath; // Seleziona il percorso della cartella di destinazione

    /** Pianificazione **/
    @FXML private RadioButton noneButton;    // Seleziona la pianificazione nulla
    @FXML private RadioButton weeklyButton;  // Seleziona la pianificazione settimanale
    @FXML private RadioButton monthlyButton; // Seleziona la pianificazione mensile
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

    Backup backup;
    private File source;
    private File destination;

    /**
     * Inizializza il controller
     */
    public void initialize() {
        /** Backup **/
        // Inizializza la lista delle pianificazioni nel check box
        for (Schedule schedule : Application.scheduler.schedules){
            configurationBox.getItems().add(schedule.getBackup().name());
        }
        // Imposta una nuova configurazione per la pianificazione
        newConfigurationButton.setOnAction(event -> {
            clearPage();
        });
        // Selezione tipo di backup
        completeButton.setOnAction(event -> {
            differentialButton.setSelected(false);
            incrementalButton.setSelected(false);
        });
        differentialButton.setOnAction(event -> {
            completeButton.setSelected(false);
            incrementalButton.setSelected(false);
        });
        incrementalButton.setOnAction(event -> {
            differentialButton.setSelected(false);
            completeButton.setSelected(false);
        });
        // Selezione percorso della cartella da salvare
        chooseSourcePath.setOnAction(this::selectSourcePath);
        // Selezione percorso della cartella di destinazione
        chooseDestinationPath.setOnAction(this::selectDestinationPath);

        /** Pianificazione **/
        noneButton.setOnAction(event -> {
            weeklyButton.setSelected(false);
            monthlyButton.setSelected(false);
            scheduleType = ScheduleType.None;
        });
        weeklyButton.setOnAction(event -> {
            noneButton.setSelected(false);
            monthlyButton.setSelected(false);
            scheduleType = ScheduleType.Weekly;
        });
        monthlyButton.setOnAction(event -> {
            noneButton.setSelected(false);
            weeklyButton.setSelected(false);
            scheduleType = ScheduleType.Monthly;
        });
        weekDays = Arrays.asList(sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck);
        monthDays = Arrays.asList(
                day1Check, day2Check, day3Check, day4Check, day5Check, day6Check, day7Check,
                day8Check, day9Check, day10Check, day11Check, day12Check, day13Check, day14Check,
                day15Check, day16Check, day17Check, day18Check, day19Check, day20Check, day21Check,
                day22Check, day23Check, day24Check, day25Check, day26Check, day27Check, day28Check,
                day29Check, day30Check, day31Check, day32Check
        );

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
            timeHourWeeklyField.setText(s);
        });
        plusWeeklyButton.setOnAction(event -> {
            String s = "";
            if (timeHourWeeklyField.getText().length() < 2) s += "0";
            s += timeHourWeeklyField.getText();
            s += ":";
            if (timeMinuteWeeklyField.getText().length() < 2) s += "0";
            s += timeMinuteWeeklyField.getText();

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
            timeHourWeeklyField.setText(s);
        });
        timeMinuteMonthlyField.textProperty().addListener((ov, oldValue, newValue) -> {
            String s = newValue;
            // Rimuove tutti i caratteri che non sono dei numeri
            if (!newValue.matches("\\d*")) s = newValue.replaceAll("[^\\d]", "");
            // Tiene solo due caratteri per volta
            if (s.length() > 2) s = s.substring(0, 2);
                // Imposta 59 come orario massimo
            else if (s.length() == 2 && Integer.parseInt(s) > 59) s = "59";
            timeHourWeeklyField.setText(s);
        });
        plusMonthlyButton.setOnAction(event -> {
            String s = "";
            if (timeHourMonthlyField.getText().length() < 2) s += "0";
            s += timeHourMonthlyField.getText();
            s += ":";
            if (timeMinuteMonthlyField.getText().length() < 2) s += "0";
            s += timeMinuteMonthlyField.getText();

            timesMonthlyField.getItems().add(s);
        });
        minusMonthlyButton.setOnAction(event -> {
            timesMonthlyField.getItems().removeAll(timesMonthlyField.getSelectionModel().getSelectedItem());
        });

        processButton.setOnAction(this::process);
        clearPage();
    }

    private void clearPage(){
        currentSchedule = null;
        scheduleType = ScheduleType.None;

        configurationBox.setValue(null);
        sourceInput.setText(null);
        destinationInput.setText(null);
        completeButton.setSelected(true);
        differentialButton.setSelected(false);
        incrementalButton.setSelected(false);

        noneButton.setSelected(true);
        weeklyButton.setSelected(false);
        monthlyButton.setSelected(false);
    }

    /**
     * Recupera e salva il percorso della cartella da salvare
     * @param event
     */
    private void selectSourcePath(ActionEvent event){
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli la cartella che vuoi salvare.");

        source = directoryChooser.showDialog(primaryStage);

        if (Utils.isSourcePathValid(source, consoleLog)){
            sourceInput.setText(source.getAbsolutePath());
        }
        else {
            source = null;
            log("La cartella di origine non è valida.");
        }
    }

    /**
     * Recupera e salva il percorso della cartella di destinazione
     * @param event
     */
    private void selectDestinationPath(ActionEvent event) {
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Scegli una cartella di destinazione.");

        destination = directoryChooser.showDialog(primaryStage);

        if (Utils.isDestinationPathValid(destination, consoleLog)) {
            destinationInput.setText(destination.getAbsolutePath());
        }
        else {
            destination = null;
            log("La cartella di destinazione non è valida.");
        }
    }

    /**
     * Inizia il processo di backup
     * @param event
     */
    private void process(ActionEvent event){
        BackupType backupType;
        if (differentialButton.isSelected())
            backupType = BackupType.Differential;
        else if (incrementalButton.isSelected())
            backupType = BackupType.Incremental;
        else
            backupType = BackupType.Complete;

        if (Application.DEBUG){
            source = new File("/home/michele/Desktop/Cartella da salvare");
            destination = new File("/home/michele/Desktop/Destinazione");
        }

        if (Utils.isSourcePathValid(source,consoleLog) && Utils.isDestinationPathValid(destination,consoleLog)){
            try {
                switch (backupType) {
                    case Complete:
                        backup = new CompleteBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    case Differential:
                        backup = new DifferentialBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    case Incremental:
                        backup = new IncrementalBackup(
                                source.getAbsolutePath(),
                                destination.getAbsolutePath()
                        );
                        break;
                    default: break;
                }

                consoleLog.setText("");
                log("Backup in corso.");
                log("Numero di cartelle trovate: " + Utils.numberOfFolders(source) + ".");
                log("Numero di file trovati: " + Utils.numberOfFiles(source) + ".");

                long lastDeltaTime = System.nanoTime();
                backup.start();
                long deltaTime = (long) ((System.nanoTime() - lastDeltaTime) / 1_000_000_000.0D);
                log("Backup completato in " + deltaTime + " secondi.");
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void save(ActionEvent event) {
        switch (scheduleType) {
            case None:
                if (currentSchedule != null)
                    currentSchedule.
                backup.start();
                break;
            case Weekly:
                break;
            case Monthly:
                break;
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
