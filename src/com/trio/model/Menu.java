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
        choosePlayerCount();
        startGame();
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
     * Choisir le nombre de joueurs
     */
    private void choosePlayerCount() {
        System.out.println("Nombre de joueurs (3-6):");
        System.out.print("Votre choix: ");
        this.nbPlayers = readInt(3, 6);
        System.out.println("Nombre de joueurs: " + nbPlayers + "\n");
    }

    /**
     * Lance le jeu avec le pattern MVC
     */
    public Game startGame() {
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
