package carte.controller;

import model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class logs {

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

    public static void main(String[] args) {
        logs logger = new logs();
        List<User> players = new ArrayList<>();
        players.add(new User("Alice", 25, 10, "/avatars/alice.png"));
        players.add(new User("Bot1", 0, 5, "/avatars/bot1.png"));

        logger.writeLogs(players, "logs/logs.txt");
        logger.displayLogs("logs/logs.txt");
    }
}
