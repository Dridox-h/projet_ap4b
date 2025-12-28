package com.trio.controller;

import com.trio.model.Bot;
import com.trio.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuController {
    private final List<Player> listPlayers;
    private final Scanner scanner;
    private boolean isTeamMode = false;

    public MenuController() {
        this.listPlayers = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        setupGame();
    }

    private void setupGame() {
        System.out.println("=== Configuration de la partie ===");

        // 1. Choix du mode
        System.out.print("Mode équipe ? (1: Oui / 2: Non) : ");
        this.isTeamMode = (readInt() == 1);

        int totalPlayers;
        if (isTeamMode) {
            // 2a. Logique Équipes (2 ou 3 équipes de 2)
            System.out.print("Combien d'équipes ? (2 ou 3) : ");
            int nbTeams = readInt();
            while (nbTeams < 2 || nbTeams > 3) {
                System.out.print("Erreur. Choisissez 2 ou 3 équipes : ");
                nbTeams = readInt();
            }
            totalPlayers = nbTeams * 2;
        } else {
            // 2b. Logique Solo classique
            System.out.print("Combien de joueurs au total ? ");
            totalPlayers = readInt();
        }

        // 3. Nombre d'humains
        System.out.print("Combien de joueurs humains ? (0 à " + totalPlayers + ") : ");
        int nbHuman = readInt();
        while (nbHuman > totalPlayers) {
            System.out.print("Le nombre d'humains ne peut pas dépasser " + totalPlayers + " : ");
            nbHuman = readInt();
        }

        // 4. Création des joueurs
        for (int i = 0; i < totalPlayers; i++) {
            Player p;
            if (i < nbHuman) {
                System.out.print("Joueur " + (i + 1) + " - ");
                p = createHumanPlayer();
            } else {
                p = new Bot("Bot-" + (i - nbHuman + 1));
            }

            // Assigne l'équipe si nécessaire (Joueur 0-1 -> Team 1, 2-3 -> Team 2...)
            if (isTeamMode) {
                int teamNumber = (i / 2) + 1;
                p.setIdTeam(teamNumber); // Assure-toi que cette méthode existe dans Player
            }

            listPlayers.add(p);
        }

        displayStatistics();

        // Lancement du jeu
        new GameController(totalPlayers, nbHuman, listPlayers, isTeamMode);
    }

    private int readInt() {
        try {
            int val = scanner.nextInt();
            scanner.nextLine();
            return val;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    public Player createHumanPlayer() {
        System.out.print("Entrez le pseudo : ");
        String pseudo = scanner.nextLine();
        return new Player(pseudo);
    }

    public void displayStatistics() {
        long nbBots = listPlayers.stream().filter(p -> p instanceof Bot).count();
        int total = listPlayers.size();

        System.out.println("\n--- Statistiques des joueurs ---");
        System.out.println("Mode             : " + (isTeamMode ? "ÉQUIPE (2 joueurs/team)" : "SOLO"));
        System.out.println("Nombre total     : " + total);
        System.out.println("Humains          : " + (total - nbBots));
        System.out.println("Bots             : " + nbBots);

        System.out.println("\nListe des joueurs :");
        for (Player player : listPlayers) {
            String type = (player instanceof Bot) ? "[BOT]" : "[HUMAIN]";
            String teamInfo = isTeamMode ? " | Équipe " + player.getIdTeam() : "";
            System.out.printf("- %-15s %-10s %s%n", player.getPseudo(), type, teamInfo);
        }
        System.out.println("--------------------------------\n");
    }
}