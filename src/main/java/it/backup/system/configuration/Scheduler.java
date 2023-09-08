package it.backup.system.configuration;

import it.backup.system.configuration.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    public List<BackupConfiguration> configurations;

    public Scheduler() {
        configurations = new ArrayList<>();
    }
}
