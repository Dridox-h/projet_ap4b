package com.trio.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menu principal pour configurer et lancer une partie
 */
public class Menu {

    // Attributs
    private User currentUser;
    private int nbPlayers;
    private int gameMode; // 1 = Solo, 2 = Équipe
    private Scanner scanner;
    // Attributs pour le logger
    private int nbBots = 0;
    private String currentType = "Individual";
    private com.trio.services.GameLogger gameLogger;

    // Constructeurs
    public Menu() {
        this.scanner = new Scanner(System.in);
        this.gameLogger = new com.trio.services.GameLogger();
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public int getNbBots() {
        return nbBots;
    }

    public String getType() {
        return currentType;
    }

    // Setters
    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public void setNbBots(int nbBots) {
        this.nbBots = nbBots;
    }

    public void setType(String currentType) {
        this.currentType = currentType;
    }

    /**
     * Validates if the game can be started with current settings
     */
    public ValidationResult validateGameStart() {
        if (nbPlayers == 0 || nbBots == 0) {
            return new ValidationResult(false, "Please select a number of players and configure bots first.");
        }
        if (currentType == null || currentType.isEmpty()) {
            return new ValidationResult(false, "Please select a type.");
        }
        if (nbPlayers > 6) {
            return new ValidationResult(false, "Please select a number of players equal or less than 6.");
        }

        if (currentUser == null) {
            return new ValidationResult(false, "Please select a user or Create a new user.");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validatePlayerCount(int nbPlayers) {
        if (nbPlayers < 2 || nbPlayers > 6) {
            return new ValidationResult(false, "Invalid number! Please select a number of players between 2 and 6");
        }
        return new ValidationResult(true, "");
    }

    public ValidationResult validateGameType(String type) {
        if (!type.equals("Individual") && !type.equals("Team")) {
            return new ValidationResult(false, "Invalid input. Please enter Individual or Team.");
        }
        return new ValidationResult(true, "");
    }

    /**
     * Completes the game with bots (total players - 1)
     */
    public void completewithBots(int nbPlayers) {
        this.nbBots = nbPlayers - 1;
    }

    /**
     * Sets the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Writes user data to log file
     */
    public void writeLogsUser(String fileName, User currentUser) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(fileName, true))) {
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
        } catch (java.io.IOException e) {
            System.err.println("Error writing logs file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Selects a user by ID from the log file
     */
    public User selectUser(int id) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader("logs/UserLogs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

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
        } catch (java.io.IOException e) {
            System.err.println("Error reading logs file: " + e.getMessage());
            return null;
        }
    }

    private String extractValue(String line, String startMarker, String endMarker) {
        int startIndex = line.indexOf(startMarker);
        if (startIndex == -1)
            return "";
        startIndex += startMarker.length();

        if (endMarker == null) {
            return line.substring(startIndex).trim();
        } else {
            int endIndex = line.indexOf(endMarker, startIndex);
            if (endIndex == -1)
                return line.substring(startIndex).trim();
            return line.substring(startIndex, endIndex).trim();
        }
    }

    public boolean isUserLogsEmpty(String fileName) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty())
                    return false;
            }
            return true;
        } catch (java.io.IOException e) {
            return true;
        }
    }

    /**
     * Logs the game session information to a file
     */
    public void logGameSession(String fileName) {
        if (currentUser == null) {
            System.out.println("Warning: No current user set.");
        }

        java.util.List<User> players = new java.util.ArrayList<>();
        if (currentUser != null) {
            players.add(currentUser);
        }

        gameLogger.writeGameLogs(players, nbPlayers, nbBots, currentType, fileName);
    }

    // Méthodes Métier

    /**
     * Affiche le menu principal et lance le jeu
     */
    public void showMainMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║           BIENVENUE DANS TRIO          ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        createUser();
        chooseGameMode();
        choosePlayerCount();

        if (gameMode == 1) {
            startSoloGame();
        } else {
            startTeamGame();
        }
    }

    /**
     * Crée l'utilisateur (joueur humain)
     */
    private void createUser() {
        System.out.print("Entrez votre pseudo: ");
        String pseudo = scanner.nextLine().trim();
        if (pseudo.isEmpty()) {
            pseudo = "Joueur";
        }
        this.currentUser = new User(pseudo);
        System.out.println("Bienvenue " + pseudo + " !\n");
    }

    /**
     * Choisir le mode de jeu
     */
    private void chooseGameMode() {
        System.out.println("Mode de jeu:");
        System.out.println("1. Solo (3-6 joueurs)");
        System.out.println("2. Équipe (4 ou 6 joueurs, équipes de 2)");
        System.out.print("Votre choix: ");
        this.gameMode = readInt(1, 2);
        System.out.println();
    }

    /**
     * Choisir le nombre de joueurs selon le mode
     */
    private void choosePlayerCount() {
        if (gameMode == 1) {
            // Mode Solo: 3 à 6 joueurs
            System.out.println("Nombre de joueurs (3-6):");
            System.out.print("Votre choix: ");
            this.nbPlayers = readInt(3, 6);
        } else {
            // Mode Équipe: 4 ou 6 joueurs uniquement
            System.out.println("Nombre de joueurs (4 ou 6 pour le mode équipe):");
            System.out.print("Votre choix: ");
            while (true) {
                int choice = readInt(4, 6);
                if (choice == 4 || choice == 6) {
                    this.nbPlayers = choice;
                    break;
                }
                System.out.print("Le mode équipe nécessite 4 ou 6 joueurs: ");
            }
        }
        System.out.println("Nombre de joueurs: " + nbPlayers + "\n");
    }

    /**
     * Lance le jeu Solo avec le pattern MVC
     */
    public Game startSoloGame() {
        List<Player> players = createPlayers();

        // Créer le Model
        SoloGame game = new SoloGame(players, new Deck());

        // Créer la View
        com.trio.view.ConsoleView view = new com.trio.view.ConsoleView();

        // Créer le Controller et lancer le jeu
        com.trio.controller.GameController controller = new com.trio.controller.GameController(game, view);
        controller.startGame();

        return game;
    }

    /**
     * Ancien nom pour compatibilité
     */
    public Game startGame() {
        return startSoloGame();
    }

    /**
     * Lance le jeu en mode Équipe
     */
    public Game startTeamGame() {
        List<Player> players = createPlayers();
        List<Team> teams = createTeams(players);

        // Créer le Model
        TeamGame game = new TeamGame(teams, new Deck());

        // Lancer le jeu directement (TeamGame gère l'affichage)
        game.startGame();

        return game;
    }

    /**
     * Crée les équipes à partir de la liste de joueurs
     */
    private List<Team> createTeams(List<Player> players) {
        List<Team> teams = new ArrayList<>();
        int teamSize = 2;
        int nbTeams = players.size() / teamSize;

        for (int i = 0; i < nbTeams; i++) {
            List<Player> teamPlayers = new ArrayList<>();
            for (int j = 0; j < teamSize; j++) {
                teamPlayers.add(players.get(i * teamSize + j));
            }
            Team team = new Team("Équipe " + (char) ('A' + i), teamPlayers);
            teams.add(team);
        }

        System.out.println("Équipes créées:");
        for (Team team : teams) {
            System.out.println("  - " + team);
        }
        System.out.println();

        return teams;
    }

    /**
     * Crée la liste des joueurs (1 User + Bots)
     */
    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(currentUser);

        for (int i = 1; i < nbPlayers; i++) {
            players.add(new Bot("Bot" + i));
        }

        System.out.println("Joueurs créés:");
        for (Player p : players) {
            String type = (p instanceof User) ? "HUMAIN" : "BOT";
            System.out.println("  - " + p.getPseudo() + " (" + type + ")");
        }
        System.out.println();

        return players;
    }

    /**
     * Lit un entier dans une plage donnée
     */
    private int readInt(int min, int max) {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.print("Entrez un nombre entre " + min + " et " + max + ": ");
            } catch (Exception e) {
                scanner.nextLine();
                System.out.print("Entrée invalide. Réessayez: ");
            }
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
