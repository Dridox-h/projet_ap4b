package com.trio.controller;

import com.trio.model.Bot;
import com.trio.model.Player;
import com.trio.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur responsable de la configuration initiale du jeu
 * Gère l'interface de menu pour créer une nouvelle partie
 */
public class MenuController {
    private final List<Player> players;
    private final Scanner scanner;
    private boolean isTeamMode;

    /**
     * Constructeur principal qui initialise le menu et lance la configuration
     */
    public MenuController() {
        this.players = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.isTeamMode = false;
        setupGame();
    }

    /**
     * Méthode principale de configuration du jeu
     * Coordonne toutes les étapes de création d'une partie :
     * 1. Choix du mode de jeu (équipe ou solo)
     * 2. Détermination du nombre de joueurs
     * 3. Création des joueurs (humains et bots)
     * 4. Attribution des équipes si nécessaire
     * 5. Lancement du contrôleur de jeu
     */
    private void setupGame() {
        System.out.println("=== Configuration de la partie Trio ===");

        // Étape 1: Choix du mode de jeu
        configureGameMode();

        // Étape 2: Détermination du nombre total de joueurs
        int totalPlayers = configureTotalPlayers();

        // Étape 3: Configuration du nombre de joueurs humains
        int humanPlayers = configureHumanPlayers(totalPlayers);

        // Étape 4: Création de tous les joueurs
        createAllPlayers(totalPlayers, humanPlayers);

        // Étape 5: Attribution des équipes en mode équipe
        if (isTeamMode) {
            assignTeams();
        }

        // Étape 6: Affichage du résumé et lancement du jeu
        displayGameSummary();
        launchGame(totalPlayers);
    }

    /**
     * Configure le mode de jeu (équipe ou individuel)
     * Demande à l'utilisateur de choisir entre mode équipe et mode solo
     */
    private void configureGameMode() {
        System.out.print("Mode équipe ? (1: Oui / 2: Non) : ");
        int choice = readValidInt(1, 2);
        this.isTeamMode = (choice == 1);
        
        System.out.println("Mode sélectionné : " + (isTeamMode ? "ÉQUIPE" : "INDIVIDUEL"));
    }

    /**
     * Détermine le nombre total de joueurs selon le mode choisi
     * En mode équipe : 4 ou 6 joueurs (2 ou 3 équipes de 2)
     * En mode solo : 3 à 6 joueurs
     * @return le nombre total de joueurs
     */
    private int configureTotalPlayers() {
        int totalPlayers;
        
        if (isTeamMode) {
            System.out.print("Combien d'équipes ? (2 ou 3) : ");
            int nbTeams = readValidInt(2, 3);
            totalPlayers = nbTeams * 2;
            System.out.println("Nombre de joueurs : " + totalPlayers + " (" + nbTeams + " équipes de 2)");
        } else {
            System.out.print("Combien de joueurs au total ? (3 à 6) : ");
            totalPlayers = readValidInt(3, 6);
        }
        
        return totalPlayers;
    }

    /**
     * Configure le nombre de joueurs humains
     * Valide que le nombre ne dépasse pas le nombre total de joueurs
     * @param totalPlayers nombre total de joueurs dans la partie
     * @return le nombre de joueurs humains
     */
    private int configureHumanPlayers(int totalPlayers) {
        System.out.print("Combien de joueurs humains ? (0 à " + totalPlayers + ") : ");
        return readValidInt(0, totalPlayers);
    }

    /**
     * Crée tous les joueurs pour la partie
     * Les premiers sont des joueurs humains, les suivants sont des bots
     * @param totalPlayers nombre total de joueurs à créer
     * @param humanPlayers nombre de joueurs humains
     */
    private void createAllPlayers(int totalPlayers, int humanPlayers) {
        players.clear();
        
        // Création des joueurs humains
        for (int i = 0; i < humanPlayers; i++) {
            System.out.printf("=== Joueur humain %d/%d ===\n", i + 1, humanPlayers);
            Player humanPlayer = createHumanPlayer();
            players.add(humanPlayer);
        }
        
        // Création des bots
        int botCount = totalPlayers - humanPlayers;
        for (int i = 0; i < botCount; i++) {
            Player bot = new Bot("Bot-" + (i + 1));
            players.add(bot);
        }
        
        System.out.println("Tous les joueurs ont été créés avec succès !");
    }

    /**
     * Assigne les joueurs aux équipes en mode équipe
     * Les joueurs sont répartis par paires : 0-1 → Équipe 1, 2-3 → Équipe 2, etc.
     */
    private void assignTeams() {
        for (int i = 0; i < players.size(); i++) {
            int teamId = (i / 2) + 1; // Équipe 1, 2 ou 3
            players.get(i).setTeamId(teamId);
        }
        System.out.println("Équipes assignées automatiquement.");
    }

    /**
     * Crée un joueur humain en demandant son pseudonyme
     * Valide que le pseudo n'est pas vide et n'est pas déjà utilisé
     * @return un nouveau joueur User avec le pseudo saisi
     */
    private Player createHumanPlayer() {
        String pseudo;
        do {
            System.out.print("Entrez le pseudo du joueur : ");
            pseudo = scanner.nextLine().trim();
            
            if (pseudo.isEmpty()) {
                System.out.println("Le pseudo ne peut pas être vide.");
                continue;
            }
            
            // Vérification de l'unicité du pseudo
            String finalPseudo = pseudo;
            boolean pseudoExists = players.stream()
                    .anyMatch(p -> p.getPseudo().equalsIgnoreCase(finalPseudo));
            
            if (pseudoExists) {
                System.out.println("Ce pseudo est déjà utilisé. Choisissez-en un autre.");
                pseudo = ""; // Force une nouvelle itération
            }
            
        } while (pseudo.isEmpty());
        
        return new User(pseudo);
    }

    /**
     * Lit un entier valide dans une plage donnée
     * Redemande la saisie tant que l'entrée n'est pas valide
     * @param min valeur minimale acceptée
     * @param max valeur maximale acceptée
     * @return l'entier saisi et validé
     */
    private int readValidInt(int min, int max) {
        int value;
        do {
            value = readInt();
            if (value < min || value > max) {
                System.out.printf("Veuillez saisir un nombre entre %d et %d : ", min, max);
            }
        } while (value < min || value > max);
        
        return value;
    }

    /**
     * Lit un entier depuis l'entrée standard
     * Gère les erreurs de saisie et nettoie le buffer
     * @return l'entier lu ou -1 en cas d'erreur
     */
    private int readInt() {
        try {
            int value = scanner.nextInt();
            scanner.nextLine(); // Nettoie le buffer
            return value;
        } catch (Exception e) {
            scanner.nextLine(); // Nettoie le buffer en cas d'erreur
            System.out.print("Saisie invalide. Veuillez entrer un nombre : ");
            return -1; // Valeur d'erreur
        }
    }

    /**
     * Affiche un résumé complet de la configuration de la partie
     * Montre le mode, le nombre de joueurs, et la liste détaillée des participants
     */
    private void displayGameSummary() {
        long botCount = players.stream().filter(p -> p instanceof Bot).count();
        int humanCount = players.size() - (int) botCount;

        System.out.println("\n=============== RÉSUMÉ DE LA PARTIE ===============");
        System.out.println("Mode de jeu      : " + (isTeamMode ? "ÉQUIPE" : "INDIVIDUEL"));
        System.out.println("Nombre total     : " + players.size() + " joueurs");
        System.out.println("Joueurs humains  : " + humanCount);
        System.out.println("Bots             : " + botCount);

        System.out.println("\nListe des participants :");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String type = (player instanceof Bot) ? "[BOT]" : "[HUMAIN]";
            String teamInfo = isTeamMode ? " | Équipe " + player.getTeamId() : "";
            System.out.printf("%d. %-15s %-8s %s%n", 
                    i + 1, player.getPseudo(), type, teamInfo);
        }
        System.out.println("==================================================\n");
    }

    /**
     * Lance le contrôleur de jeu avec la configuration établie
     * Transfère le contrôle au GameController pour commencer la partie
     * @param totalPlayers nombre total de joueurs (pour validation)
     */
    private void launchGame(int totalPlayers) {
        System.out.println("Lancement de la partie...\n");
        new GameController(totalPlayers, players, isTeamMode);
    }
}