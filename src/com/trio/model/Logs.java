package com.trio.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    private static Logs instance;
    private static final String LOG_FILE = "logs/logs.txt";

    private Logs() {
    }

    public static Logs getInstance() {
        if (instance == null)
            instance = new Logs();
        return instance;
    }

    public void writeLogs(String message) {
        // Créer le dossier logs s'il n'existe pas
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}