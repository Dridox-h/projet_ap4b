package com.trio.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    private static Logs instance;
    private String lastLogs;

    // Nom du fichier de log
    private static final String LOG_FILE_PATH = "src/trio_game.log";
    // Formatteur pour l'heure (ex: 2024-05-20 14:30:05)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logs() {
        // Optionnel : On peut écrire une ligne de séparation au démarrage de l'instance
        writeToFile("\n=== NOUVELLE SESSION DE JEU : " + LocalDateTime.now().format(dateFormatter) + " ===\n");
    }

    public static Logs getInstance() {
        if (instance == null) {
            instance = new Logs();
        }
        return instance;
    }

    /**
     * Écrit le log dans la console ET dans le fichier.
     */
    public void writeLogs(String message) {
        this.lastLogs = message;

        // 1. Affichage Console
        System.out.println("[LOG] " + message);

        // 2. Écriture Fichier avec horodatage
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String fileLine = String.format("[%s] %s", timestamp, message);

        writeToFile(fileLine);
    }

    public String getLastLogs() {
        return lastLogs;
    }

    /**
     * Méthode privée pour gérer l'écriture physique dans le fichier
     */
    private void writeToFile(String line) {
        // Le paramètre 'true' dans FileWriter permet d'ajouter à la fin du fichier (append)
        // sans écraser le contenu précédent.
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(line);
            bw.newLine(); // Saut de ligne

        } catch (IOException e) {
            System.err.println("ERREUR CRITIQUE : Impossible d'écrire dans le fichier de logs ! " + e.getMessage());
        }
    }
}
