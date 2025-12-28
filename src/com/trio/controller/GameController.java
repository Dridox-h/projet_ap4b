package com.trio.controller;

import com.trio.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur principal du jeu Trio
 * Implémente les règles officielles du jeu Trio
 */
public class GameController {
    private final List<Player> players;
    private final int totalPlayers;
    private final boolean isTeamMode;
    private final int triosToWin; // 2 pour mode rapide, 3 pour mode classique
    private final Game game;
    private Scanner scanner;

    // Cartes révélées durant le tour actuel (avec leur propriétaire)
    private final List<RevealedCard> currentTurnRevealed;

    /**
     * Constructeur principal du contrôleur de jeu
     * @param totalPlayers nombre de joueurs
     * @param players liste des joueurs
     * @param isTeamMode true si mode équipe
     */
    public GameController(int totalPlayers, List<Player> players, boolean isTeamMode) {
        this.totalPlayers = totalPlayers;
        this.players = players;
        this.isTeamMode = isTeamMode;
        this.triosToWin = 3; // Toujours 3 trios pour gagner
        this.game = new Game(players, isTeamMode);
        this.currentTurnRevealed = new ArrayList<>();

        setupGameAndDistribute();
    }

    /**
     * Configure le jeu et distribue les cartes selon les règles du Trio
     */
    private void setupGameAndDistribute() {
        System.out.println("=== Préparation de la partie ===");
        System.out.printf("Mode : %s | Objectif : 3 trios pour gagner\n",
                (isTeamMode ? "ÉQUIPE" : "CLASSIQUE"));

        Deck masterDeck = createCompleteDeck();
        distributeAllCards(masterDeck);
        sortAllPlayerHands();
        displayGameState();

        System.out.println("La partie peut commencer !\n");
        System.out.println("RAPPELS IMPORTANTS :");
        System.out.println("- Ne jamais réorganiser vos cartes après le tri initial");
        System.out.println("- Pas de bluff verbal autorisé");
        System.out.println("- Utilisez votre mémoire et votre déduction !\n");

        playGame();
    }

    /**
     * Crée le deck complet de 36 cartes pour le jeu Trio
     */
    private Deck createCompleteDeck() {
        Deck deck = new Deck();

        for (int value = 1; value <= 12; value++) {
            for (int copy = 0; copy < 3; copy++) {
                deck.addCard(new Card(value));
            }
        }

        deck.shuffle();
        System.out.println("Deck de 36 cartes créé et mélangé.");
        return deck;
    }

    /**
     * Distribue TOUTES les cartes aux joueurs (règle officielle)
     */
    private void distributeAllCards(Deck masterDeck) {
        System.out.println("Distribution de toutes les cartes...");

        int cardsPerPlayer = 36 / totalPlayers;
        int remainingCards = 36 % totalPlayers;

        System.out.printf("- %d cartes par joueur", cardsPerPlayer);
        if (remainingCards > 0) {
            System.out.printf(" (+ 1 carte pour les %d premiers joueurs)", remainingCards);
        }
        System.out.println();

        // Distribution équitable
        int playerIndex = 0;
        while (!masterDeck.isEmpty()) {
            Card card = masterDeck.removeCard(0);
            players.get(playerIndex).getDeck().addCard(card);
            playerIndex = (playerIndex + 1) % totalPlayers;
        }

        System.out.println("Distribution terminée - Toutes les cartes sont distribuées.");
    }

    /**
     * Trie les cartes dans la main de tous les joueurs
     * TRI INITIAL OBLIGATOIRE - ne peut plus être modifié après
     */
    private void sortAllPlayerHands() {
        for (Player player : players) {
            player.getDeck().sort();
        }
        System.out.println("Mains de tous les joueurs triées par ordre croissant.");
        System.out.println("⚠️  ATTENTION : L'ordre ne peut plus être changé !");
    }

    /**
     * Affiche l'état complet du jeu
     */
    public void displayGameState() {
        System.out.println("\n=============== ÉTAT DU JEU ===============");
        System.out.printf("Mode : %s | Objectif : 3 trios pour gagner\n",
                (isTeamMode ? "ÉQUIPE" : "CLASSIQUE"));
        System.out.printf("Joueur actuel : %s\n", game.getCurrentPlayer().getPseudo());

        System.out.println("\n--- État des joueurs ---");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            displayPlayerState(player, i + 1);
        }

        System.out.println("==========================================\n");
    }

    /**
     * Affiche l'état détaillé d'un joueur
     */
    private void displayPlayerState(Player player, int playerNumber) {
        String playerType = (player instanceof Bot) ? "BOT" : "HUMAIN";
        String currentPlayerMarker = (game.getCurrentPlayer().equals(player)) ? " [TOUR ACTUEL]" : "";
        String teamInfo = isTeamMode ? " | Équipe " + player.getTeamId() : "";

        System.out.printf("%d. %s (%s)%s%s\n",
                playerNumber, player.getPseudo(), playerType, teamInfo, currentPlayerMarker);

        System.out.printf("   Cartes en main (%d) : ", player.getDeck().getSize());
        if (player instanceof User && game.getCurrentPlayer().equals(player)) {
            displayCards(player.getDeck().getCards(), true);
        } else {
            System.out.println("[Cartes cachées]");
        }

        System.out.printf("   Trios complétés : %d/3\n", player.getTriosWon().getSize() / 3);
    }

    /**
     * Affiche une liste de cartes
     */
    private void displayCards(List<Card> cards, boolean showValues) {
        if (cards.isEmpty()) {
            System.out.println("Aucune carte");
            return;
        }

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (showValues) {
                System.out.printf("[%d]", card.getValue());
            } else {
                System.out.print("[?]");
            }

            if (i < cards.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    /**
     * Boucle principale du jeu
     */
    private void playGame() {
        boolean gameIsRunning = true;

        while (gameIsRunning) {
            Player currentPlayer = game.getCurrentPlayer();

            // Nettoyer les cartes révélées du tour précédent
            currentTurnRevealed.clear();

            // Tour du joueur actuel
            System.out.println("\n" + "=".repeat(50));
            System.out.println("TOUR DE " + currentPlayer.getPseudo());
            System.out.println("=".repeat(50));

            boolean turnSuccess = playTurn(currentPlayer);

            if (!turnSuccess) {
                System.out.println("\n❌ Échec ! Toutes les cartes révélées ce tour sont remises face cachée.");
                hideCurrentTurnCards();
            }

            // Vérifier si le joueur/équipe a gagné
            if (checkVictory(currentPlayer)) {
                gameIsRunning = false;
                displayFinalResults(currentPlayer);
            } else {
                // Passer au joueur suivant
                game.nextPlayer();

                // Petite pause pour la lisibilité
                if (!(currentPlayer instanceof User)) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * Joue le tour d'un joueur
     * Le joueur continue tant qu'il trouve les bonnes cartes
     * IMPORTANT : Il faut au moins 2 cartes révélées pour pouvoir déclarer un échec
     * @return true si le tour s'est bien terminé (trio formé), false sinon
     */
    private boolean playTurn(Player player) {
        boolean continueTurn = true;
        boolean turnSuccess = true;

        while (continueTurn && turnSuccess) {
            if (player instanceof User) {
                System.out.print("\nVos cartes : ");
                displayCards(player.getDeck().getCards(), true);
            }

            // Afficher les cartes déjà révélées ce tour
            if (!currentTurnRevealed.isEmpty()) {
                System.out.print("Cartes révélées ce tour : ");
                for (RevealedCard rc : currentTurnRevealed) {
                    System.out.printf("[%d] ", rc.card.getValue());
                }
                System.out.println();
            }

            // Choisir une action
            TurnAction action = chooseAction(player);

            if (action == null) {
                // Joueur annule
                if (currentTurnRevealed.size() < 2) {
                    // Si moins de 2 cartes, on ne peut pas comparer = pas d'échec
                    System.out.println("⚠️  Impossible d'arrêter maintenant, il faut au moins 2 cartes pour comparer !");
                    continue;
                } else {
                    // Arrêt volontaire avec au moins 2 cartes = échec
                    turnSuccess = false;
                    continueTurn = false;
                    continue;
                }
            }

            // Exécuter l'action
            Card revealedCard = executeAction(action, player);

            if (revealedCard == null) {
                // Carte non disponible
                if (currentTurnRevealed.size() < 2) {
                    // Si moins de 2 cartes, on doit continuer obligatoirement
                    System.out.println("❌ Carte non disponible ! Vous devez révéler au moins 2 cartes.");
                    System.out.println("Choisissez une autre action...");
                    continue;
                } else {
                    // Si 2+ cartes déjà révélées, c'est un échec
                    System.out.println("❌ Carte non disponible !");
                    turnSuccess = false;
                    continueTurn = false;
                    continue;
                }
            }

            // Vérifier si c'est la bonne carte attendue
            if (currentTurnRevealed.isEmpty()) {
                // Première carte : toujours acceptée
                addRevealedCard(revealedCard, action.targetPlayer != null ? action.targetPlayer : player);
                System.out.println("✓ Première carte révélée : [" + revealedCard.getValue() + "]");
                System.out.println("⚠️  Vous devez révéler au moins une deuxième carte pour pouvoir comparer.");

                // Continuer obligatoirement
                continueTurn = true;

            } else {
                // Vérifier si la carte correspond aux cartes précédentes
                int expectedValue = currentTurnRevealed.get(0).card.getValue();

                if (revealedCard.getValue() == expectedValue) {
                    addRevealedCard(revealedCard, action.targetPlayer != null ? action.targetPlayer : player);
                    System.out.println("✓ Bonne carte ! [" + revealedCard.getValue() + "]");

                    // Vérifier si un trio est formé
                    if (currentTurnRevealed.size() == 3) {
                        System.out.println("\n🎉 TRIO COMPLÉTÉ ! 🎉");
                        completeTrio(player);
                        turnSuccess = true;
                        continueTurn = false;
                    } else {
                        // Continuer le tour
                        continueTurn = askContinue(player);
                    }
                } else {
                    System.out.println("❌ Mauvaise carte ! Attendu: [" + expectedValue + "], Reçu: [" + revealedCard.getValue() + "]");
                    // Remettre la carte dans la liste pour être retournée face cachée
                    Player cardOwner = action.targetPlayer != null ? action.targetPlayer : player;
                    addRevealedCard(revealedCard, cardOwner);

                    turnSuccess = false;
                    continueTurn = false;
                }
            }
        }

        return turnSuccess;
    }

    /**
     * Classe interne pour représenter une action de tour
     */
    private static class TurnAction {
        enum Type { ASK_CARD, REVEAL_SPECIFIC, REVEAL_MIN, REVEAL_MAX }
        Type type;
        Player targetPlayer;
        int cardValue;
        int cardIndex; // Pour révéler une carte spécifique

        TurnAction(Type type) {
            this.type = type;
        }
    }

    /**
     * Classe pour suivre les cartes révélées durant le tour
     */
    private static class RevealedCard {
        Card card;
        Player owner;

        RevealedCard(Card card, Player owner) {
            this.card = card;
            this.owner = owner;
        }
    }

    /**
     * Ajoute une carte à la liste des cartes révélées ce tour
     */
    private void addRevealedCard(Card card, Player owner) {
        currentTurnRevealed.add(new RevealedCard(card, owner));
        card.setVisible(true);
        game.addVisibleCard(card);
    }

    /**
     * Remet face cachée toutes les cartes révélées ce tour
     */
    private void hideCurrentTurnCards() {
        for (RevealedCard rc : currentTurnRevealed) {
            rc.card.setVisible(false);
            rc.owner.getDeck().addCard(rc.card);
            rc.owner.getDeck().sort();
        }
        game.clearVisibleCards();
        currentTurnRevealed.clear();
    }

    /**
     * Complète un trio pour le joueur
     */
    private void completeTrio(Player player) {
        // Les 3 cartes du trio sont gagnées par le joueur
        List<Card> trioCards = new ArrayList<>();
        for (RevealedCard rc : currentTurnRevealed) {
            trioCards.add(rc.card);
        }

        player.winTrio(trioCards);

        int value = currentTurnRevealed.get(0).card.getValue();
        System.out.printf("Trio de [%d] complété par %s !\n", value, player.getPseudo());
        System.out.printf("Score actuel : %d/3 trio(s)\n", player.getTriosWon().getSize() / 3);

        game.clearVisibleCards();
        currentTurnRevealed.clear();
    }

    /**
     * Demande au joueur s'il veut continuer son tour
     */
    private boolean askContinue(Player player) {
        // Si seulement 1 carte révélée, on doit continuer obligatoirement
        if (currentTurnRevealed.size() == 1) {
            return true; // Pas de choix, on doit continuer
        }

        if (player instanceof User) {
            System.out.print("\nVoulez-vous continuer votre tour ? (O/N) : ");
            String response = getScanner().nextLine().trim().toUpperCase();
            return "O".equals(response) || "OUI".equals(response);
        } else {
            // Bot décide selon une stratégie simple
            if (currentTurnRevealed.size() == 2) {
                // Si 2 cartes révélées, toujours essayer de compléter le trio
                System.out.println(player.getPseudo() + " décide de continuer pour compléter le trio.");
                return true;
            }
            // Ne devrait pas arriver ici
            return false;
        }
    }

    /**
     * Demande au joueur de choisir une action
     */
    private TurnAction chooseAction(Player player) {
        if (player instanceof User) {
            return chooseActionHuman();
        } else {
            return chooseActionBot((Bot) player);
        }
    }

    /**
     * Interface de choix pour un joueur humain
     */
    private TurnAction chooseActionHuman() {
        System.out.println("\nQue voulez-vous faire ?");
        System.out.println("1. Demander une carte précise à un joueur");
        System.out.println("2. Révéler une de vos cartes");
        System.out.println("0. Arrêter le tour");

        System.out.print("Votre choix : ");
        int choice = readValidInt(0, 2);
        getScanner().nextLine();

        if (choice == 0) {
            return null;
        }

        return switch (choice) {
            case 1 -> createAskCardAction();
            case 2 -> createRevealOwnCardAction();
            default -> null;
        };
    }

    /**
     * Crée une action "révéler une de ses cartes"
     */
    private TurnAction createRevealOwnCardAction() {
        Player currentPlayer = game.getCurrentPlayer();

        if (currentPlayer.getDeck().isEmpty()) {
            System.out.println("Vous n'avez plus de cartes !");
            return null;
        }

        List<Card> cards = currentPlayer.getDeck().getCards();

        System.out.println("\nVos cartes :");
        for (int i = 0; i < cards.size(); i++) {
            System.out.printf("%d. [%d]\n", i + 1, cards.get(i).getValue());
        }

        System.out.print("Quelle carte voulez-vous révéler ? (numéro) : ");
        int choice = readValidInt(1, cards.size());
        getScanner().nextLine();

        TurnAction action = new TurnAction(TurnAction.Type.REVEAL_SPECIFIC);
        action.cardIndex = choice - 1;

        return action;
    }

    /**
     * Crée une action "demander une carte"
     */
    private TurnAction createAskCardAction() {
        // Sélectionner un joueur cible
        Player target = selectTargetPlayer(game.getCurrentPlayer());
        if (target == null) {
            return null;
        }

        // Demander quelle valeur
        System.out.print("Quelle valeur de carte demandez-vous (1-12) ? : ");
        int value = readValidInt(1, 12);
        getScanner().nextLine();

        TurnAction action = new TurnAction(TurnAction.Type.ASK_CARD);
        action.targetPlayer = target;
        action.cardValue = value;

        return action;
    }

    /**
     * Choix automatique pour un bot
     */
    private TurnAction chooseActionBot(Bot bot) {
        System.out.println(bot.getPseudo() + " réfléchit...");

        // Stratégie du bot
        double rand = Math.random();

        if (rand < 0.5 && !currentTurnRevealed.isEmpty()) {
            // 50% : essayer de demander la même carte que celle révélée
            Player target = selectRandomTarget(bot);
            if (target != null) {
                TurnAction action = new TurnAction(TurnAction.Type.ASK_CARD);
                action.targetPlayer = target;
                action.cardValue = currentTurnRevealed.get(0).card.getValue();

                System.out.printf("%s demande un [%d] à %s\n",
                        bot.getPseudo(), action.cardValue, target.getPseudo());
                return action;
            }
        } else if (rand < 0.7 && !bot.getDeck().isEmpty()) {
            // 20% : demander une carte de sa main
            Player target = selectRandomTarget(bot);
            if (target != null) {
                TurnAction action = new TurnAction(TurnAction.Type.ASK_CARD);
                action.targetPlayer = target;

                int randomIndex = (int) (Math.random() * bot.getDeck().getSize());
                action.cardValue = bot.getDeck().getCards().get(randomIndex).getValue();

                System.out.printf("%s demande un [%d] à %s\n",
                        bot.getPseudo(), action.cardValue, target.getPseudo());
                return action;
            }
        }

        // Sinon révéler une carte aléatoire de sa main
        if (!bot.getDeck().isEmpty()) {
            int randomIndex = (int) (Math.random() * bot.getDeck().getSize());
            TurnAction action = new TurnAction(TurnAction.Type.REVEAL_SPECIFIC);
            action.cardIndex = randomIndex;

            System.out.println(bot.getPseudo() + " révèle une de ses cartes");
            return action;
        }

        // Cas d'erreur : plus de cartes
        return null;
    }

    /**
     * Sélectionne une cible aléatoire pour le bot
     */
    private Player selectRandomTarget(Player bot) {
        List<Player> availablePlayers = new ArrayList<>();

        for (Player p : players) {
            if (!p.equals(bot) && !p.getDeck().isEmpty()) {
                availablePlayers.add(p);
            }
        }

        if (availablePlayers.isEmpty()) {
            return null;
        }

        int randomIndex = (int) (Math.random() * availablePlayers.size());
        return availablePlayers.get(randomIndex);
    }

    /**
     * Exécute l'action choisie et retourne la carte révélée
     */
    private Card executeAction(TurnAction action, Player currentPlayer) {
        return switch (action.type) {
            case ASK_CARD -> askSpecificCard(action.targetPlayer, action.cardValue);
            case REVEAL_SPECIFIC -> revealSpecificCard(currentPlayer, action.cardIndex);
            case REVEAL_MIN -> revealMinCard(currentPlayer);
            case REVEAL_MAX -> revealMaxCard(currentPlayer);
        };
    }

    /**
     * Révèle une carte spécifique du joueur (par index)
     */
    private Card revealSpecificCard(Player player, int cardIndex) {
        if (player.getDeck().isEmpty() || cardIndex < 0 || cardIndex >= player.getDeck().getSize()) {
            System.out.println(player.getPseudo() + " : Index de carte invalide !");
            return null;
        }

        Card card = player.getDeck().getCards().get(cardIndex);
        player.getDeck().getCards().remove(cardIndex);

        System.out.println("→ " + player.getPseudo() + " révèle la carte : [" + card.getValue() + "]");
        return card;
    }

    /**
     * Demande une carte spécifique à un joueur
     */
    private Card askSpecificCard(Player target, int value) {
        System.out.printf("→ Demande de [%d] à %s...\n", value, target.getPseudo());

        // Chercher si le joueur a cette carte
        for (Card card : target.getDeck().getCards()) {
            if (card.getValue() == value) {
                target.getDeck().getCards().remove(card);
                System.out.println("✓ " + target.getPseudo() + " a la carte [" + value + "] !");
                return card;
            }
        }

        System.out.println("✗ " + target.getPseudo() + " n'a pas de carte [" + value + "]");
        return null;
    }

    /**
     * Révèle la carte minimum du joueur
     */
    private Card revealMinCard(Player player) {
        if (player.getDeck().isEmpty()) {
            System.out.println(player.getPseudo() + " n'a plus de cartes !");
            return null;
        }

        // Les cartes sont triées, donc la première est la plus petite
        Card minCard = player.getDeck().getCards().get(0);
        player.getDeck().getCards().remove(0);

        System.out.println("→ " + player.getPseudo() + " révèle sa plus PETITE carte : [" + minCard.getValue() + "]");
        return minCard;
    }

    /**
     * Révèle la carte maximum du joueur
     */
    private Card revealMaxCard(Player player) {
        if (player.getDeck().isEmpty()) {
            System.out.println(player.getPseudo() + " n'a plus de cartes !");
            return null;
        }

        // Les cartes sont triées, donc la dernière est la plus grande
        List<Card> cards = player.getDeck().getCards();
        Card maxCard = cards.get(cards.size() - 1);
        cards.remove(cards.size() - 1);

        System.out.println("→ " + player.getPseudo() + " révèle sa plus GRANDE carte : [" + maxCard.getValue() + "]");
        return maxCard;
    }

    /**
     * Sélectionne un joueur cible
     */
    private Player selectTargetPlayer(Player currentPlayer) {
        List<Player> availablePlayers = new ArrayList<>();

        for (Player p : players) {
            if (!p.equals(currentPlayer)) {
                availablePlayers.add(p);
            }
        }

        if (availablePlayers.isEmpty()) {
            System.out.println("Aucun autre joueur disponible !");
            return null;
        }

        System.out.println("\nChoisissez un joueur cible :");
        for (int i = 0; i < availablePlayers.size(); i++) {
            Player p = availablePlayers.get(i);
            System.out.printf("%d. %s (%d cartes en main)\n",
                    i + 1, p.getPseudo(), p.getDeck().getSize());
        }

        System.out.print("Votre choix : ");
        int choice = readValidInt(1, availablePlayers.size());
        getScanner().nextLine();

        return availablePlayers.get(choice - 1);
    }

    /**
     * Vérifie si un joueur ou son équipe a gagné
     */
    private boolean checkVictory(Player player) {
        int playerTrios = player.getTriosWon().getSize() / 3;

        if (isTeamMode) {
            // En mode équipe, compter tous les trios de l'équipe
            int teamTrios = 0;
            for (Player p : players) {
                if (p.getTeamId() == player.getTeamId()) {
                    teamTrios += p.getTriosWon().getSize() / 3;
                }
            }
            return teamTrios >= 3;
        } else {
            return playerTrios >= 3;
        }
    }

    /**
     * Affiche les résultats finaux
     */
    private void displayFinalResults(Player winner) {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         FIN DE LA PARTIE !             ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (isTeamMode) {
            int teamId = winner.getTeamId();
            int teamTrios = 0;

            System.out.println("\n🏆 ÉQUIPE " + teamId + " GAGNE ! 🏆");
            System.out.println("\nMembres de l'équipe gagnante :");
            for (Player p : players) {
                if (p.getTeamId() == teamId) {
                    int trios = p.getTriosWon().getSize() / 3;
                    teamTrios += trios;
                    System.out.printf("  • %s : %d trio(s)\n", p.getPseudo(), trios);
                }
            }
            System.out.printf("\nTotal équipe : %d trio(s)\n", teamTrios);
        } else {
            int winnerTrios = winner.getTriosWon().getSize() / 3;
            System.out.println("\n🏆 " + winner.getPseudo() + " GAGNE ! 🏆");
            System.out.println("Avec " + winnerTrios + " trio(s) complété(s) !");

            System.out.println("\nClassement final :");
            List<Player> sorted = new ArrayList<>(players);
            sorted.sort((p1, p2) -> Integer.compare(
                    p2.getTriosWon().getSize(), p1.getTriosWon().getSize()));

            int rank = 1;
            for (Player p : sorted) {
                int trios = p.getTriosWon().getSize() / 3;
                System.out.printf("%d. %s : %d trio(s)\n", rank++, p.getPseudo(), trios);
            }
        }

        System.out.println("\nMerci d'avoir joué à Trio ! 🎲");
    }

    /**
     * Lit un entier valide dans une plage donnée
     */
    private int readValidInt(int min, int max) {
        int value;
        while (true) {
            try {
                value = getScanner().nextInt();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Veuillez entrer un nombre entre %d et %d : ", min, max);
            } catch (Exception e) {
                System.out.printf("Entrée invalide. Veuillez entrer un nombre entre %d et %d : ", min, max);
                getScanner().nextLine();
            }
        }
    }

    /**
     * Obtient le scanner pour la saisie utilisateur
     */
    private Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }
}