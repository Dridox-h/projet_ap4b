package com.trio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SoloGame implements Game {

    // Attributs
    private List<Player> players;
    private Deck centerDeck;
    private int currentPlayerIndex;
    private List<RevealedCard> revealedThisTurn;
    private boolean gameEnded;
    private Scanner scanner;

    // Constructeurs
    public SoloGame(List<Player> players, Deck centerDeck) {
        this.players = players;
        this.centerDeck = centerDeck;
        this.currentPlayerIndex = 0;
        this.revealedThisTurn = new ArrayList<>();
        this.gameEnded = false;
        this.scanner = new Scanner(System.in);
    }

    // Getters
    public List<Player> getPlayers() {
        return players;
    }

    public Deck getCenterDeck() {
        return centerDeck;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<RevealedCard> getRevealedCards() {
        return revealedThisTurn;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    // Méthodes Game Interface

    @Override
    public void startGame() {
        System.out.println("=== DÉBUT DE LA PARTIE TRIO ===");
        System.out.println("Nombre de joueurs: " + players.size());
        System.out.println("Objectif: 3 trios pour gagner\n");

        // Distribuer les cartes
        distributeCards();

        // Afficher la main du joueur
        displayUserHand();

        // Boucle principale du jeu
        while (!isFinished()) {
            Player currentPlayer = getCurrentPlayer();
            System.out.println("\n═══════════════════════════════════");
            System.out.println("   Tour de " + currentPlayer.getPseudo());
            System.out.println("═══════════════════════════════════");

            // Afficher les cartes visibles
            displayVisibleCards();

            playTurn();

            if (!isFinished()) {
                nextTurn();
            }
        }

        // Afficher le gagnant
        TrioHolder winner = getWinner();
        if (winner instanceof Player) {
            System.out.println(
                    "\n🎉 " + ((Player) winner).getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!");
        }
    }

    /**
     * Distribue les cartes aux joueurs et au centre
     */
    public void distributeCards() {
        DrawPile drawPile = new DrawPile();
        drawPile.createDefaultCards();
        drawPile.distributeToPlayers(players, centerDeck);
        System.out.println("📦 Cartes distribuées!");
    }

    /**
     * Affiche uniquement la main du joueur humain (User)
     */
    public void displayUserHand() {
        for (Player p : players) {
            if (p instanceof User) {
                System.out.print("\n🃏 Votre main: ");
                for (Card c : p.getDeck().getCards()) {
                    System.out.print("[" + c.getValue() + "] ");
                }
                System.out.println("(" + p.getDeck().getSize() + " cartes)");
                break;
            }
        }
    }

    /**
     * Affiche les cartes visibles (isVisible = true) de tous les joueurs
     */
    public void displayVisibleCards() {
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
    public void playTurn() {
        Player currentPlayer = getCurrentPlayer();
        boolean turnContinues = true;
        boolean turnSuccess = true;

        while (turnContinues && turnSuccess && revealedThisTurn.size() < 3) {
            // Afficher les cartes déjà révélées
            displayRevealedCards();

            // Choisir une action
            int action = chooseAction(currentPlayer);

            if (action == 0) {
                // Arrêter le tour
                if (revealedThisTurn.size() < 2) {
                    System.out.println("Vous devez révéler au moins 2 cartes avant d'arrêter!");
                    continue;
                }
                turnSuccess = false;
                turnContinues = false;
            } else {
                // Exécuter l'action et vérifier
                Card revealedCard = executeAction(action, currentPlayer);

                if (revealedCard == null) {
                    System.out.println("Action invalide!");
                    continue;
                }

                // Vérifier si la carte correspond
                if (revealedThisTurn.size() > 1) {
                    int expectedValue = revealedThisTurn.get(0).getValue();
                    if (revealedCard.getValue() != expectedValue) {
                        System.out.println(
                                "❌ Mauvaise carte! Attendu: " + expectedValue + ", Reçu: " + revealedCard.getValue());
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        System.out.println("✓ Bonne carte! [" + revealedCard.getValue() + "]");
                    }
                } else {
                    System.out.println("✓ Première carte révélée: [" + revealedCard.getValue() + "]");
                }
            }
        }

        // Fin du tour
        if (revealedThisTurn.size() == 3 && isValidTrio()) {
            System.out.println("\n🎉 TRIO COMPLÉTÉ!");
            awardTrioToWinner(currentPlayer);
            System.out
                    .println(currentPlayer.getPseudo() + " a maintenant " + currentPlayer.getTrioCount() + " trio(s)");
        } else if (!turnSuccess || revealedThisTurn.size() > 0) {
            System.out.println("\n❌ Échec du tour. Les cartes sont remises face cachée.");
            failTurn();
            // Réafficher l'état après échec
            displayUserHand();
            displayVisibleCards();
        }
    }

    /**
     * Affiche les cartes révélées ce tour
     */
    private void displayRevealedCards() {
        if (!revealedThisTurn.isEmpty()) {
            System.out.print("Cartes révélées: ");
            for (RevealedCard rc : revealedThisTurn) {
                System.out.print("[" + rc.getValue() + "] ");
            }
            System.out.println();
        }
    }

    /**
     * Demande au joueur de choisir une action
     */
    private int chooseAction(Player player) {
        if (player instanceof Bot) {
            return chooseBotAction((Bot) player);
        }

        // Afficher l'état actuel avant de choisir
        displayUserHand();
        displayVisibleCards();
        displayRevealedCards();

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

    /**
     * Exécute l'action choisie et retourne la carte révélée
     */
    private Card executeAction(int action, Player currentPlayer) {
        boolean isBot = currentPlayer instanceof Bot;
        Bot bot = isBot ? (Bot) currentPlayer : null;

        switch (action) {
            case 1: // Ma carte MIN
                return revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                return revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot ? bot.chooseTargetPlayer(players) : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        System.out.println(bot.getPseudo() + " cible " + target3.getPseudo() + " (MIN)");
                    }
                    return revealLowestCardFromPlayer(target3);
                }
                return null;

            case 4: // Carte MAX d'un autre joueur
                Player target4 = isBot ? bot.chooseTargetPlayer(players) : selectOtherPlayer(currentPlayer);
                if (target4 != null) {
                    if (isBot) {
                        System.out.println(bot.getPseudo() + " cible " + target4.getPseudo() + " (MAX)");
                    }
                    return revealHighestCardFromPlayer(target4);
                }
                return null;

            case 5: // Carte du centre
                if (isBot) {
                    int centerIndex = bot.chooseCenterCardIndex(centerDeck);
                    if (centerIndex >= 0) {
                        System.out.println(bot.getPseudo() + " révèle une carte du centre");
                        return revealCardFromCenter(centerIndex);
                    }
                    return null;
                } else {
                    return selectAndRevealCenterCard();
                }

            default:
                return null;
        }
    }

    /**
     * Affiche les cartes du centre et permet de choisir par index
     */
    private Card selectAndRevealCenterCard() {
        if (centerDeck.isEmpty()) {
            System.out.println("Aucune carte au centre!");
            return null;
        }

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
                return revealCardFromCenter(choice - 1);
            }
        } catch (Exception e) {
            scanner.nextLine();
        }
        System.out.println("Index invalide!");
        return null;
    }

    /**
     * Sélectionne un autre joueur
     */
    private Player selectOtherPlayer(Player currentPlayer) {
        System.out.println("Choisir un joueur:");
        int index = 1;
        List<Player> others = new ArrayList<>();
        for (Player p : players) {
            if (!p.equals(currentPlayer) && !p.getDeck().isEmpty()) {
                System.out.println(index + ". " + p.getPseudo() + " (" + p.getDeck().getSize() + " cartes)");
                others.add(p);
                index++;
            }
        }

        if (others.isEmpty()) {
            System.out.println("Aucun autre joueur disponible!");
            return null;
        }

        System.out.print("Votre choix: ");
        try {
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= others.size()) {
                return others.get(choice - 1);
            }
        } catch (Exception e) {
            scanner.nextLine();
        }
        return null;
    }

    /**
     * Action automatique pour un Bot - délègue à la classe Bot
     */
    private int chooseBotAction(Bot bot) {
        return bot.chooseBotAction(revealedThisTurn, players, centerDeck);
    }

    @Override
    public boolean isFinished() {
        for (Player p : players) {
            if (p.getTrioCount() >= 3) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TrioHolder getWinner() {
        for (Player p : players) {
            if (p.getTrioCount() >= 3) {
                return p;
            }
        }
        return null;
    }

    // Méthodes Métier - Gestion des cartes

    public Card revealHighestCardFromPlayer(Player owner) {
        Card card = owner.getDeck().getHighCard();
        if (card != null) {
            card.setVisible();
            revealedThisTurn.add(new RevealedCard(card, owner));
        }
        return card;
    }

    public Card revealLowestCardFromPlayer(Player owner) {
        Card card = owner.getDeck().getLowCard();
        if (card != null) {
            card.setVisible();
            revealedThisTurn.add(new RevealedCard(card, owner));
        }
        return card;
    }

    public Card revealCardFromCenter(int index) {
        if (centerDeck.isEmpty() || index < 0 || index >= centerDeck.getSize()) {
            return null;
        }
        Card card = centerDeck.getCard(index);
        if (card != null && !card.isVisible()) {
            card.setVisible();
            revealedThisTurn.add(new RevealedCard(card, null));
        }
        return card;
    }

    public boolean isValidTrio() {
        if (revealedThisTurn.size() != 3) {
            return false;
        }
        int value = revealedThisTurn.get(0).getValue();
        return revealedThisTurn.stream().allMatch(rc -> rc.getValue() == value);
    }

    public void awardTrioToWinner(Player winner) {
        Deck trio = new Deck();

        for (RevealedCard rc : revealedThisTurn) {
            Card card = rc.getCard();
            if (rc.getOwner() != null) {
                rc.getOwner().getDeck().removeCard(card);
            } else {
                centerDeck.removeCard(card);
            }
            trio.addCard(card);
        }

        winner.addTrio(trio);
        revealedThisTurn.clear();
    }

    public void failTurn() {
        for (RevealedCard rc : revealedThisTurn) {
            rc.getCard().setInvisible();
        }
        revealedThisTurn.clear();
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}
