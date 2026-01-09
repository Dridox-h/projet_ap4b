package com.trio.controller;

import com.trio.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des données persistantes.
 * Gère la sauvegarde des résultats de partie et des statistiques des joueurs.
 */
public class DataService {

    private static DataService instance;
    private static final String USER_LOGS_PATH = "logs/UserLogs.txt";
    private static final String GAME_RESULTS_PATH = "logs/GameResults.txt";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DataService() {
        // Créer le dossier logs s'il n'existe pas
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    // ===== SAUVEGARDE DES RÉSULTATS DE PARTIE =====

    /**
     * Enregistre le résultat d'une partie Solo
     */
    public void saveGameResult(Player winner, List<Player> players, String gameMode) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        StringBuilder result = new StringBuilder();

        result.append("=== RÉSULTAT DE PARTIE ===\n");
        result.append("Date: ").append(timestamp).append("\n");
        result.append("Mode: ").append(gameMode).append("\n");
        result.append("Vainqueur: ").append(winner.getPseudo()).append("\n");
        result.append("Trios: ").append(winner.getTrioCount()).append("\n");
        result.append("Joueurs: ");

        for (int i = 0; i < players.size(); i++) {
            result.append(players.get(i).getPseudo());
            if (i < players.size() - 1)
                result.append(", ");
        }
        result.append("\n");
        result.append("========================\n");

        writeToFile(GAME_RESULTS_PATH, result.toString());
        Logs.getInstance().writeLogs("Résultat de partie enregistré pour " + winner.getPseudo());
    }

    /**
     * Enregistre le résultat d'une partie Équipe
     */
    public void saveTeamGameResult(Team winningTeam, List<Team> teams) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        StringBuilder result = new StringBuilder();

        result.append("=== RÉSULTAT DE PARTIE ÉQUIPE ===\n");
        result.append("Date: ").append(timestamp).append("\n");
        result.append("Mode: Équipe\n");
        result.append("Équipe gagnante: ").append(winningTeam.getName()).append("\n");
        result.append("Trios: ").append(winningTeam.getTrioCount()).append("\n");
        result.append("Membres: ");

        List<Player> members = winningTeam.getPlayers();
        for (int i = 0; i < members.size(); i++) {
            result.append(members.get(i).getPseudo());
            if (i < members.size() - 1)
                result.append(", ");
        }
        result.append("\n");

        result.append("Toutes les équipes:\n");
        for (Team team : teams) {
            result.append("  - ").append(team.getName()).append(": ").append(team.getTrioCount()).append(" trios\n");
        }
        result.append("=================================\n");

        writeToFile(GAME_RESULTS_PATH, result.toString());
        Logs.getInstance().writeLogs("Résultat de partie équipe enregistré pour " + winningTeam.getName());
    }

    // ===== GESTION DES VICTOIRES =====

    /**
     * Incrémente les victoires d'un joueur User et met à jour le fichier
     */
    public void incrementVictory(Player winner) {
        if (!(winner instanceof User)) {
            Logs.getInstance().writeLogs("Le gagnant n'est pas un User enregistré, pas de mise à jour.");
            return;
        }

        User user = (User) winner;
        user.addVictory();

        Logs.getInstance()
                .writeLogs("Victoire ajoutée pour " + user.getName() + " (Total: " + user.getNBVictoire() + ")");

        // Mettre à jour le fichier UserLogs.txt
        updateUserInFile(user);
    }

    /**
     * Incrémente les victoires de tous les membres d'une équipe gagnante
     */
    public void incrementTeamVictories(Team winningTeam) {
        Logs.getInstance().writeLogs("Attribution des victoires à l'équipe " + winningTeam.getName());

        for (Player player : winningTeam.getPlayers()) {
            if (player instanceof User) {
                User user = (User) player;
                user.addVictory();
                Logs.getInstance().writeLogs(
                        "Victoire ajoutée pour " + user.getName() + " (Total: " + user.getNBVictoire() + ")");
                updateUserInFile(user);
            }
        }
    }

    /**
     * Met à jour les informations d'un User dans le fichier UserLogs.txt
     */
    private void updateUserInFile(User user) {
        File file = new File(USER_LOGS_PATH);
        if (!file.exists()) {
            // Si le fichier n'existe pas, créer une nouvelle entrée
            saveNewUser(user);
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID: " + user.getId() + " |")) {
                    // Mettre à jour cette ligne
                    line = String.format("ID: %d | Name: %s | Age: %d | Victories: %d",
                            user.getId(), user.getName(), user.getAge(), user.getNBVictoire());
                    found = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            Logs.getInstance().writeLogs("Erreur lecture UserLogs: " + e.getMessage());
            return;
        }

        if (!found) {
            // L'utilisateur n'existe pas dans le fichier, l'ajouter
            lines.add(String.format("ID: %d | Name: %s | Age: %d | Victories: %d",
                    user.getId(), user.getName(), user.getAge(), user.getNBVictoire()));
        }

        // Réécrire le fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            Logs.getInstance().writeLogs("Erreur écriture UserLogs: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde un nouvel utilisateur
     */
    public void saveNewUser(User user) {
        String logLine = String.format("ID: %d | Name: %s | Age: %d | Victories: %d",
                user.getId(), user.getName(), user.getAge(), user.getNBVictoire());
        writeToFile(USER_LOGS_PATH, logLine);
        Logs.getInstance().writeLogs("Nouvel utilisateur enregistré: " + user.getName());
    }

    /**
     * Charge tous les utilisateurs depuis le fichier
     */
    public List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USER_LOGS_PATH);

        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                User user = parseUserFromLog(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            Logs.getInstance().writeLogs("Erreur chargement utilisateurs: " + e.getMessage());
        }

        return users;
    }

    /**
     * Parse une ligne de log pour créer un User
     */
    private User parseUserFromLog(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 4)
                return null;

            int id = Integer.parseInt(parts[0].replace("ID:", "").trim());
            String name = parts[1].replace("Name:", "").trim();
            int age = Integer.parseInt(parts[2].replace("Age:", "").trim());
            int victories = Integer.parseInt(parts[3].replace("Victories:", "").trim());

            return new User(id, name, age, victories);
        } catch (Exception e) {
            return null;
        }
    }

    // ===== MÉTHODES LEGACY =====

    public void saveGame(Object game) {
        Logs.getInstance().writeLogs("Sauvegarde de la partie en cours...");
    }

    public void loadGame() {
        Logs.getInstance().writeLogs("Chargement des données...");
    }

    // ===== UTILITAIRES =====

    private void writeToFile(String filePath, String content) {
        try (FileWriter fw = new FileWriter(filePath, true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
            bw.newLine();
        } catch (IOException e) {
            Logs.getInstance().writeLogs("Erreur écriture fichier " + filePath + ": " + e.getMessage());
        }
    }
}