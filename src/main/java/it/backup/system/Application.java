package it.backup.system;

import it.backup.system.scheduler.Schedule;
import it.backup.system.scheduler.Scheduler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    static public final boolean DEBUG = true;

    static public Scheduler scheduler;

    @Override
    public void start(Stage stage) throws IOException {
        // Recupera i dati dello scheduler
        scheduler = new Scheduler();

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Backup System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
