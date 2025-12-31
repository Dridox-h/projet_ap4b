package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Menu Model - Contains game state and business logic
 * Follows MVC pattern: This is the MODEL layer
 */
public class Menu {

    // Game state
    private User currentUser;
    private int nbplayers = 0;
    private int currentBots = 0;
    private String currentType = "Individual";

    // Logger for game sessions
    private GameLogger gameLogger;

    // Constructor
    public Menu() {
        this.gameLogger = new GameLogger();
    }

    // Getters
    public int getNbPlayers() {
        return nbplayers;
    }

    public int getNbBots() {
        return currentBots;
    }

    public String getType() {
        return currentType;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Setters
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setNbPlayers(int nbplayers) {
        this.nbplayers = nbplayers;
    }

    public void setNbBots(int currentBots) {
        this.currentBots = currentBots;
    }

    public void setType(String currentType) {
        this.currentType = currentType;
    }

    // Business Logic Methods

    /**
     * Validates if the game can be started with current settings
     * 
     * @return ValidationResult containing success status and error message if any
     */
    public ValidationResult validateGameStart() {
        if (nbplayers == 0 || currentBots == 0) {
            return new ValidationResult(false, "Please select a number of players and configure bots first.");
        }

        if (currentType == null || currentType.isEmpty()) {
            return new ValidationResult(false, "Please select a type.");
        }

        if (nbplayers > 6) {
            return new ValidationResult(false, "Please select a number of players equal or less than 6.");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validates player count
     * 
     * @param nbplayers number of players to validate
     * @return ValidationResult
     */
    public ValidationResult validatePlayerCount(int nbplayers) {
        if (nbplayers < 2 || nbplayers > 6) {
            return new ValidationResult(false, "Invalid number! Please select a number of players between 2 and 6");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Validates game type
     * 
     * @param type game type to validate
     * @return ValidationResult
     */
    public ValidationResult validateGameType(String type) {
        if (!type.equals("Individual") && !type.equals("Team")) {
            return new ValidationResult(false, "Invalid input. Please enter Individual or Team.");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Completes the game with bots (total players - 1)
     * 
     * @param nbplayers total number of players
     */
    public void completewithBots(int nbplayers) {
        this.currentBots = nbplayers - 1;
    }

    /**
     * Gets the current game settings as a formatted string
     * 
     * @return formatted settings string
     */
    public String getSettingsString() {
        return String.format("Players: %d | Bots: %d | Type: %s", nbplayers, currentBots, currentType);
    }

    /**
     * Logs the game session information to a file
     * Should be called when a game is started
     * 
     * @param fileName Path to the game log file
     */
    public void logGameSession(String fileName) {
        if (currentUser == null) {
            System.out.println("Warning: No current user set. Game session will be logged without user information.");
        }

        // Create a list with the current user (if exists)
        java.util.List<User> players = new java.util.ArrayList<>();
        if (currentUser != null) {
            players.add(currentUser);
        }

        // Log the game session with all information
        gameLogger.writeGameLogs(players, nbplayers, currentBots, currentType, fileName);
    }

    // File I/O Methods (Data Persistence)

    /**
     * Writes user data to log file
     * 
     * @param fileName    path to log file
     * @param currentUser user to write
     */
    public void writeLogsUser(String fileName, User currentUser) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            writer.newLine();
            writer.write("[" + timestamp + "]");
            writer.write(" ID: " + currentUser.getId());
            writer.write("| User: " + currentUser.getName());
            writer.write("| Age: " + currentUser.getAge());
            writer.write("| Victories: " + currentUser.getNBVictoire());
            writer.write("| Avatar: " + currentUser.getPathAvatar());

        } catch (IOException e) {
            System.err.println("Error writing logs file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays user logs to console
     * 
     * @param fileName path to log file
     */
    public void displayLogsUser(String fileName) {
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

    /**
     * Selects a user by ID from the log file
     * 
     * @param id user ID to search for
     * @return User object if found, null otherwise
     */
    public User selectUser(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("logs/UserLogs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (line.contains(" ID: " + id)) {
                    String userName = extractValue(line, " User: ", "|");
                    int age = Integer.parseInt(extractValue(line, " Age: ", "|"));
                    int victories = Integer.parseInt(extractValue(line, " Victories: ", "|"));
                    String avatar = extractValue(line, " Avatar: ", null);
                    User user = new User(id, userName, age, victories, avatar);
                    setCurrentUser(user);
                    System.out.println("User '" + userName + "' loaded successfully with ID: " + id);
                    return user;
                }
            }
            System.out.println("User with ID: " + id + " not found in logs.");
            return null;
        } catch (IOException e) {
            System.err.println("Error reading logs file: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing user data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to extract values from log lines
     * 
     * @param line        log line to parse
     * @param startMarker start marker string
     * @param endMarker   end marker string (null for end of line)
     * @return extracted value
     */
    private String extractValue(String line, String startMarker, String endMarker) {
        int startIndex = line.indexOf(startMarker);
        if (startIndex == -1) {
            return "";
        }
        startIndex += startMarker.length();

        if (endMarker == null) {
            return line.substring(startIndex).trim();
        } else {
            int endIndex = line.indexOf(endMarker, startIndex);
            if (endIndex == -1) {
                return line.substring(startIndex).trim();
            }
            return line.substring(startIndex, endIndex).trim();
        }
    }

    /**
     * Checks if user logs file is empty
     * 
     * @param fileName path to log file
     * @return true if empty or doesn't exist, false otherwise
     */
    public boolean isUserLogsEmpty(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Inner class to represent validation results
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
