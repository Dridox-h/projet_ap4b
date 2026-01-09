package com.trio.controller;

import com.trio.model.*;
import com.trio.services.Logs;

import java.io.*;

/**
 * Contrôleur du menu GUI.
 * Gère la configuration de la partie avant de la lancer.
 */
public class MenuController {

    private Menu menu;

    public MenuController(Menu menu) {
        this.menu = menu;
    }

    /**
     * Valide la configuration et prépare le lancement du jeu
     */
    public Menu.ValidationResult startGame() {
        // Vérifier qu'un utilisateur est sélectionné
        if (menu.getCurrentUser() == null) {
            return Menu.ValidationResult.error("Please select or create a user first.");
        }

        // Vérifier le nombre de joueurs
        if (menu.getNbPlayers() < 2) {
            return Menu.ValidationResult.error("At least 2 players are required.");
        }

        // Vérifier le mode équipe (nombre pair de joueurs requis)
        if (menu.isTeamMode() && menu.getNbPlayers() % 2 != 0) {
            return Menu.ValidationResult.error("Team mode requires an even number of players.");
        }

        Logs.getInstance().writeLogs("Game starting with " + menu.getNbPlayers() + " players, mode: " + menu.getType());
        return Menu.ValidationResult.success();
    }

    /**
     * Configure le nombre de bots pour compléter la partie
     */
    public void configureBots(int totalPlayers) {
        // Si un utilisateur existe, on compte 1 humain
        int humanPlayers = menu.getCurrentUser() != null ? 1 : 0;
        int botsNeeded = totalPlayers - humanPlayers;

        menu.setNbBots(botsNeeded);
        menu.setNbPlayers(totalPlayers);

        Logs.getInstance().writeLogs("Configured " + botsNeeded + " bots for the game.");
    }

    /**
     * Vérifie si le fichier UserLogs.txt est vide ou n'existe pas
     */
    public boolean isUserLogsEmpty() {
        File file = new File("logs/UserLogs.txt");
        if (!file.exists()) {
            return true;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    return false;
                }
            }
        } catch (IOException e) {
            return true;
        }

        return true;
    }

    /**
     * Sélectionne un utilisateur existant par son ID
     */
    public User selectExistingUser(int userId) {
        File file = new File("logs/UserLogs.txt");
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Parse format: "ID: X | Name: Y | Age: Z | Victories: W"
                User user = parseUserFromLog(line);
                if (user != null && user.getId() == userId) {
                    menu.setCurrentUser(user);
                    Logs.getInstance().writeLogs("User selected: " + user.getName());
                    return user;
                }
            }
        } catch (IOException e) {
            Logs.getInstance().writeLogs("Error reading user logs: " + e.getMessage());
        }

        return null;
    }

    /**
     * Parse une ligne de log pour créer un User
     */
    private User parseUserFromLog(String line) {
        try {
            // Format attendu: "ID: X | Name: Y | Age: Z | Victories: W"
            String[] parts = line.split("\\|");
            if (parts.length < 4) {
                return null;
            }

            int id = Integer.parseInt(parts[0].replace("ID:", "").trim());
            String name = parts[1].replace("Name:", "").trim();
            int age = Integer.parseInt(parts[2].replace("Age:", "").trim());
            int victories = Integer.parseInt(parts[3].replace("Victories:", "").trim());

            return new User(id, name, age, victories);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Crée un nouvel utilisateur et le sauvegarde
     */
    public User createNewUser(String name, int age, String avatarPath) {
        User user = new User(name, age, avatarPath);

        // Sauvegarder dans le fichier logs
        saveUserToLogs(user);

        // Définir comme utilisateur courant
        menu.setCurrentUser(user);

        Logs.getInstance().writeLogs("New user created: " + user.getName());
        return user;
    }

    /**
     * Sauvegarde un utilisateur dans le fichier de logs
     * Vérifie que l'ID n'existe pas déjà, sinon attribue un nouvel ID unique
     */
    private void saveUserToLogs(User user) {
        // Créer le dossier logs s'il n'existe pas
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        File logFile = new File("logs/UserLogs.txt");
        int maxId = 0;
        boolean idExists = false;

        // Lire le fichier pour trouver l'ID max et vérifier les doublons
        if (logFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    User existingUser = parseUserFromLog(line);
                    if (existingUser != null) {
                        int existingId = existingUser.getId();
                        if (existingId > maxId) {
                            maxId = existingId;
                        }
                        if (existingId == user.getId()) {
                            idExists = true;
                        }
                    }
                }
            } catch (IOException e) {
                Logs.getInstance().writeLogs("Error reading user logs: " + e.getMessage());
            }
        }

        // Si l'ID existe déjà, attribuer un nouvel ID unique
        if (idExists) {
            int newId = maxId + 1;
            user.setId(newId);
            Logs.getInstance().writeLogs("User ID conflict detected. Assigned new ID: " + newId);
        }

        // Écrire l'utilisateur dans le fichier
        try (FileWriter fw = new FileWriter(logFile, true);
                BufferedWriter bw = new BufferedWriter(fw)) {

            String logLine = String.format("ID: %d | Name: %s | Age: %d | Victories: %d",
                    user.getId(), user.getName(), user.getAge(), user.getNBVictoire());
            bw.write(logLine);
            bw.newLine();

        } catch (IOException e) {
            Logs.getInstance().writeLogs("Error saving user: " + e.getMessage());
        }
    }

    /**
     * Quitte l'application
     */
    public void exitGame() {
        Logs.getInstance().writeLogs("Exiting game from menu.");
        System.exit(0);
    }

    // === Getters ===

    public User getCurrentUser() {
        return menu.getCurrentUser();
    }

    public int getNbPlayers() {
        return menu.getNbPlayers();
    }

    public boolean isTeamMode() {
        return menu.isTeamMode();
    }
}
