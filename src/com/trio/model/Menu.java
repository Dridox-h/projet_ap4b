package com.trio.model;

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

    // Constructeurs
    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
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
}
