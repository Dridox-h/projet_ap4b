package com.trio.controller;

import com.trio.model.Bot;
import com.trio.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuController {
    private final List<Player> listPlayers;
    private final Scanner scanner;

    public MenuController() {
        this.listPlayers = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        setupGame();
    }

    private void setupGame() {
        System.out.println("=== Configuration de la partie ===");

        System.out.print("Combien de joueurs au total ? ");
        int totalPlayers = readInt();

        System.out.print("Combien de joueurs humains ? ");
        int nbHuman = readInt();

        // Ajout des humains
        for (int i = 0; i < nbHuman; i++) {
            System.out.print("Joueur " + (i + 1) + " - ");
            addPlayer();
        }

        // Compléter avec des bots si nécessaire
        int botsNeeded = totalPlayers - nbHuman;
        if (botsNeeded > 0) {
            completeWithBot(botsNeeded);
        }

        displayStatistics();

        // Lancement du contrôleur de jeu
        GameController gameController = new GameController(totalPlayers, nbHuman,listPlayers);
    }

    /**
     * Méthode utilitaire pour lire un entier et nettoyer le buffer du scanner
     */
    private int readInt() {
        int val = scanner.nextInt();
        scanner.nextLine(); // Consomme le retour à la ligne restant
        return val;
    }

    public void addPlayer() {
        System.out.print("Entrez le pseudo : ");
        String pseudo = scanner.nextLine();
        listPlayers.add(new Player(pseudo));
    }

    public void completeWithBot(int nb) {
        for (int i = 1; i <= nb; i++) {
            listPlayers.add(new Bot("Bot-" + i));
        }
    }

    public void displayStatistics() {
        // Calcul dynamique via la liste
        long nbBots = listPlayers.stream().filter(p -> p instanceof Bot).count();
        int total = listPlayers.size();

        System.out.println("\n--- Statistiques des joueurs ---");
        System.out.println("Nombre total : " + total);
        System.out.println("Humains      : " + (total - nbBots));
        System.out.println("Bots         : " + nbBots);

        System.out.println("\nListe des pseudos :");
        for (Player player : listPlayers) {
            String type = (player instanceof Bot) ? "[BOT]" : "[HUMAIN]";
            System.out.printf("- %-15s %s%n", player.getPseudo(), type);
        }
        System.out.println("--------------------------------\n");
    }
}