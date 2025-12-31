package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GameLogger - Handles logging of game sessions
 * Part of the Model layer - handles data persistence for game logs
 */
public class GameLogger {

    /**
     * Writes game session logs to file
     * 
     * @param users     List of users/players in the game
     * @param nbPlayers Total number of players
     * @param nbBots    Number of bots
     * @param gameType  Type of game (Individual/Team)
     * @param fileName  Path to log file
     */
    public void writeGameLogs(List<User> users, int nbPlayers, int nbBots, String gameType, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("Game Session Log - " + timestamp);
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            // Game Configuration
            writer.write("Game Configuration:");
            writer.newLine();
            writer.write("-".repeat(60));
            writer.newLine();
            writer.write(String.format("  Total Players: %d", nbPlayers));
            writer.newLine();
            writer.write(String.format("  Number of Bots: %d", nbBots));
            writer.newLine();
            writer.write(String.format("  Game Type: %s", gameType));
            writer.newLine();
            writer.newLine();

            // Players Information
            writer.write("Players:");
            writer.newLine();
            writer.write("-".repeat(60));
            writer.newLine();

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                writer.write(String.format("Player %d:", i + 1));
                writer.newLine();
                writer.write(String.format("  ID: %d", user.getId()));
                writer.newLine();
                writer.write(String.format("  Name: %s", user.getName()));
                writer.newLine();
                writer.write(String.format("  Age: %d", user.getAge()));
                writer.newLine();
                writer.write(String.format("  Victories: %d", user.getNBVictoire()));
                writer.newLine();
                writer.write(String.format("  Avatar: %s", user.getPathAvatar()));
                writer.newLine();
                writer.newLine();
            }

            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            System.out.println("Game logs successfully written to " + fileName);

        } catch (IOException e) {
            System.err.println("Error writing game logs to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Writes simplified game logs with just basic info
     * 
     * @param users    List of users/players in the game
     * @param fileName Path to log file
     */
    public void writeLogs(List<User> users, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("Game Log - " + timestamp);
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            writer.write("Players:");
            writer.newLine();
            writer.write("-".repeat(60));
            writer.newLine();

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                writer.write(String.format("Player %d:", i + 1));
                writer.newLine();
                writer.write(String.format("  Name: %s", user.getName()));
                writer.newLine();
                writer.write(String.format("  Age: %d", user.getAge()));
                writer.newLine();
                writer.write(String.format("  Victories: %d", user.getNBVictoire()));
                writer.newLine();
                writer.write(String.format("  Avatar: %s", user.getPathAvatar()));
                writer.newLine();
                writer.newLine();
            }

            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            System.out.println("Logs successfully written to " + fileName);

        } catch (IOException e) {
            System.err.println("Error writing logs to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays logs from file to console
     * 
     * @param fileName Path to log file
     */
    public void displayLogs(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading logs file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
