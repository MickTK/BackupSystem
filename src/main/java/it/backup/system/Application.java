package it.backup.system;

import it.backup.system.configuration.Scheduler;
import it.backup.system.routine.BackgroundProcessor;
import it.backup.system.routine.Routine;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Applicazione principale
 */
public class Application extends javafx.application.Application {

    static public final boolean DEBUG = false;

    static public Scheduler scheduler; // Informazioni sulle configurazioni di backup

    // Operazioni in background
    static public Thread routine;   // Controlli sulle pianificazioni
    static public Thread processor; // Esecuzione dei backup

    @Override
    public void start(Stage stage) throws IOException {

        // Recupera i dati dello scheduler
        scheduler = new Scheduler();

        Test.test();

        // Inizializza e mostra l'interfaccia utente
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Backup System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            synchronized (Routine.class) {
                Routine.canRun = false;
            }
            synchronized (BackgroundProcessor.class) {
                BackgroundProcessor.canRun = false;
            }
        });
        stage.show();

        // Threads
        processor = new Thread(new BackgroundProcessor());
        processor.start();
        routine = new Thread(new Routine());
        routine.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
