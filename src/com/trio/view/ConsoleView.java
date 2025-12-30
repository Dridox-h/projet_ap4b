package com.trio.view;

import com.trio.model.*;
import java.util.List;
import java.util.Scanner;

/**
 * Implémentation console de GameView.
 * Gère l'affichage et les entrées utilisateur via la console.
 */
public class ConsoleView implements GameView {

    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    // === AFFICHAGE ===

    @Override
    public void displayWelcome(int nbPlayers) {
        System.out.println("=== DÉBUT DE LA PARTIE TRIO ===");
        System.out.println("Nombre de joueurs: " + nbPlayers);
        System.out.println("Objectif: 3 trios pour gagner\n");
    }

    @Override
    public void displayTurnStart(Player player) {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("   Tour de " + player.getPseudo());
        System.out.println("═══════════════════════════════════");
    }

    @Override
    public void displayPlayerHand(Player player) {
        System.out.print("\n🃏 Votre main: ");
        for (Card c : player.getDeck().getCards()) {
            System.out.print("[" + c.getValue() + "] ");
        }
        System.out.println("(" + player.getDeck().getSize() + " cartes)");
    }

    @Override
    public void displayVisibleCards(List<Player> players, Deck centerDeck) {
        System.out.println("\n👁️ Cartes visibles:");
        boolean anyVisible = false;

        for (Player p : players) {
            for (Card c : p.getDeck().getCards()) {
                if (c.isVisible()) {
                    System.out.println("  " + p.getPseudo() + ": [" + c.getValue() + "]");
                    anyVisible = true;
                }
            }
        }

        if (centerDeck != null) {
            for (Card c : centerDeck.getCards()) {
                if (c.isVisible()) {
                    System.out.println("  Centre: [" + c.getValue() + "]");
                    anyVisible = true;
                }
            }
        }

        if (!anyVisible) {
            System.out.println("  (Aucune)");
        }
    }

    @Override
    public void displayRevealedCards(List<RevealedCard> revealedCards) {
        if (!revealedCards.isEmpty()) {
            System.out.print("Cartes révélées: ");
            for (RevealedCard rc : revealedCards) {
                System.out.print("[" + rc.getValue() + "] ");
            }
            System.out.println();
        }
    }

    @Override
    public void displayCardRevealed(Card card, boolean isFirst, boolean isCorrect, int expectedValue) {
        if (isFirst) {
            System.out.println("✓ Première carte révélée: [" + card.getValue() + "]");
        } else if (isCorrect) {
            System.out.println("✓ Bonne carte! [" + card.getValue() + "]");
        } else {
            System.out.println("❌ Mauvaise carte! Attendu: " + expectedValue + ", Reçu: " + card.getValue());
        }
    }

    @Override
    public void displayTrioSuccess(Player winner, int trioCount) {
        System.out.println("\n🎉 TRIO COMPLÉTÉ!");
        System.out.println(winner.getPseudo() + " a maintenant " + trioCount + " trio(s)");
    }

    @Override
    public void displayTurnFailed() {
        System.out.println("\n❌ Échec du tour. Les cartes sont remises face cachée.");
    }

    @Override
    public void displayGameWinner(Player winner) {
        System.out.println("\n🎉 " + winner.getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!");
    }

    @Override
    public void displayError(String message) {
        System.out.println("⚠️ " + message);
    }

    @Override
    public void displayBotAction(Bot bot, String action, Player target) {
        if (target != null) {
            System.out.println(bot.getPseudo() + " " + action + " " + target.getPseudo());
        } else {
            System.out.println(bot.getPseudo() + " " + action);
        }
    }

    // === INPUT ===

    @Override
    public int promptAction() {
        System.out.println("\nActions disponibles:");
        System.out.println("1. Révéler votre carte MIN");
        System.out.println("2. Révéler votre carte MAX");
        System.out.println("3. Révéler la carte MIN d'un autre joueur");
        System.out.println("4. Révéler la carte MAX d'un autre joueur");
        System.out.println("5. Révéler une carte du centre");
        System.out.println("0. Arrêter le tour");

        System.out.print("Votre choix: ");
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    @Override
    public Player promptSelectPlayer(List<Player> availablePlayers) {
        System.out.println("Choisir un joueur:");
        int index = 1;
        for (Player p : availablePlayers) {
            System.out.println(index + ". " + p.getPseudo() + " (" + p.getDeck().getSize() + " cartes)");
            index++;
        }

        System.out.print("Votre choix: ");
        try {
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= availablePlayers.size()) {
                return availablePlayers.get(choice - 1);
            }
        } catch (Exception e) {
            scanner.nextLine();
        }
        return null;
    }

    @Override
    public int promptSelectCenterCard(Deck centerDeck) {
        System.out.println("Cartes au centre (" + centerDeck.getSize() + "):");
        for (int i = 0; i < centerDeck.getSize(); i++) {
            Card c = centerDeck.getCard(i);
            if (c.isVisible()) {
                System.out.print("  " + (i + 1) + ". [" + c.getValue() + "]");
            } else {
                System.out.print("  " + (i + 1) + ". [?]");
            }
        }
        System.out.println();

        System.out.print("Choisir un index (1-" + centerDeck.getSize() + "): ");
        try {
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= centerDeck.getSize()) {
                return choice - 1;
            }
        } catch (Exception e) {
            scanner.nextLine();
        }
        return -1;
    }
}
