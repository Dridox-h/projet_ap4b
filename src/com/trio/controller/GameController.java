package com.trio.controller;

import com.trio.model.*;
import java.util.List;

/**
 * Contrôleur principal du jeu Trio
 * Gère la logique de jeu, la distribution des cartes et l'affichage
 */
public class GameController {
    private final List<Player> players;
    private final int totalPlayers;
    private final boolean isTeamMode;
    private final Game game;

    /**
     * Constructeur principal du contrôleur de jeu
     * Initialise la partie avec les paramètres fournis et lance la distribution
     * @param totalPlayers nombre total de joueurs
     * @param players liste des joueurs participants
     * @param isTeamMode true si le jeu est en mode équipe, false sinon
     */
    public GameController(int totalPlayers, List<Player> players, boolean isTeamMode) {
        this.totalPlayers = totalPlayers;
        this.players = players;
        this.isTeamMode = isTeamMode;
        this.game = new Game(players, isTeamMode);
        
        setupGameAndDistribute();
    }

    /**
     * Configure le jeu et distribue les cartes selon les règles du Trio
     * Crée le deck de 36 cartes (1 à 12 en triple exemplaire)
     * Distribue selon le mode de jeu et le nombre de joueurs
     */
    private void setupGameAndDistribute() {
        System.out.println("=== Préparation de la partie ===");
        
        // Création du deck complet : 36 cartes (1 à 12, chacune en 3 exemplaires)
        Deck masterDeck = createCompleteDeck();
        
        // Distribution selon le mode de jeu
        if (isTeamMode) {
            distributeForTeamMode(masterDeck);
        } else {
            distributeForSoloMode(masterDeck);
        }
        
        // Tri obligatoire des mains de tous les joueurs
        sortAllPlayerHands();
        
        // Affichage de l'état initial du jeu
        displayGameState();
        
        System.out.println("La partie peut commencer !\n");
    }

    /**
     * Crée le deck complet de 36 cartes pour le jeu Trio
     * Chaque valeur de 1 à 12 est présente en 3 exemplaires
     * Le deck est mélangé après création
     * @return le deck mélangé prêt pour la distribution
     */
    private Deck createCompleteDeck() {
        Deck deck = new Deck();
        
        // Création de 3 exemplaires de chaque carte (1 à 12)
        for (int value = 1; value <= 12; value++) {
            for (int copy = 0; copy < 3; copy++) {
                deck.addCard(new Card(value));
            }
        }
        
        // Mélange du deck pour une distribution aléatoire
        deck.shuffle();
        
        System.out.println("Deck de 36 cartes créé et mélangé.");
        return deck;
    }

    /**
     * Gère la distribution des cartes en mode équipe
     * En mode équipe, toutes les cartes sont distribuées aux joueurs
     * Aucune carte ne reste au centre
     * @param masterDeck le deck principal à distribuer
     */
    private void distributeForTeamMode(Deck masterDeck) {
        System.out.println("Distribution en mode ÉQUIPE :");
        
        // Calcul du nombre de cartes par joueur
        int cardsPerPlayer = calculateCardsPerPlayerTeamMode();
        
        System.out.printf("- %d cartes par joueur\n", cardsPerPlayer);
        System.out.println("- 0 carte au centre");
        
        // Distribution de toutes les cartes aux joueurs
        distributeCardsToPlayers(masterDeck, cardsPerPlayer);
        
        System.out.println("Distribution terminée (mode équipe).");
    }

    /**
     * Calcule le nombre de cartes par joueur en mode équipe
     * 4 joueurs : 9 cartes chacun (36 cartes au total)
     * 6 joueurs : 6 cartes chacun (36 cartes au total)
     * @return le nombre de cartes à distribuer par joueur
     */
    private int calculateCardsPerPlayerTeamMode() {
        return switch (totalPlayers) {
            case 4 -> 9; // 4 joueurs × 9 cartes = 36 cartes
            case 6 -> 6; // 6 joueurs × 6 cartes = 36 cartes
            default -> throw new IllegalStateException("Mode équipe invalide : " + totalPlayers + " joueurs");
        };
    }

    /**
     * Gère la distribution des cartes en mode solo (individuel)
     * Une partie des cartes va aux joueurs, le reste au centre
     * La répartition dépend du nombre de joueurs
     * @param masterDeck le deck principal à distribuer
     */
    private void distributeForSoloMode(Deck masterDeck) {
        System.out.println("Distribution en mode SOLO :");
        
        // Détermination de la distribution selon le nombre de joueurs
        int cardsPerPlayer, cardsForCenter;
        
        switch (totalPlayers) {
            case 3 -> {
                cardsPerPlayer = 9;  // 3 × 9 = 27 cartes
                cardsForCenter = 9;  // 36 - 27 = 9 cartes
            }
            case 4 -> {
                cardsPerPlayer = 7;  // 4 × 7 = 28 cartes
                cardsForCenter = 8;  // 36 - 28 = 8 cartes
            }
            case 5 -> {
                cardsPerPlayer = 6;  // 5 × 6 = 30 cartes
                cardsForCenter = 6;  // 36 - 30 = 6 cartes
            }
            case 6 -> {
                cardsPerPlayer = 5;  // 6 × 5 = 30 cartes
                cardsForCenter = 6;  // 36 - 30 = 6 cartes
            }
            default -> throw new IllegalStateException("Nombre de joueurs invalide : " + totalPlayers);
        }
        
        System.out.printf("- %d cartes par joueur\n", cardsPerPlayer);
        System.out.printf("- %d cartes au centre\n", cardsForCenter);
        
        // Distribution aux joueurs puis au centre
        distributeCardsToPlayers(masterDeck, cardsPerPlayer);
        distributeCardsToCenter(masterDeck, cardsForCenter);
        
        System.out.println("Distribution terminée (mode solo).");
    }

    /**
     * Distribue le nombre spécifié de cartes à chaque joueur
     * Retire les cartes du deck principal et les ajoute aux mains des joueurs
     * @param masterDeck le deck source des cartes
     * @param cardsPerPlayer nombre de cartes à donner à chaque joueur
     */
    private void distributeCardsToPlayers(Deck masterDeck, int cardsPerPlayer) {
        for (Player player : players) {
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!masterDeck.isEmpty()) {
                    Card card = masterDeck.removeCard(0);
                    player.getDeck().addCard(card);
                }
            }
        }
    }

    /**
     * Place les cartes restantes au centre du jeu (mode solo uniquement)
     * Ces cartes pourront être utilisées pendant la partie
     * @param masterDeck le deck source des cartes
     * @param cardsForCenter nombre de cartes à placer au centre
     */
    private void distributeCardsToCenter(Deck masterDeck, int cardsForCenter) {
        for (int i = 0; i < cardsForCenter; i++) {
            if (!masterDeck.isEmpty()) {
                Card card = masterDeck.removeCard(0);
                game.getCenterCards().addCard(card);
            }
        }
    }

    /**
     * Trie les cartes dans la main de tous les joueurs
     * Le tri est obligatoire dans le jeu Trio pour faciliter la visualisation
     */
    private void sortAllPlayerHands() {
        for (Player player : players) {
            player.getDeck().sort();
        }
        System.out.println("Mains de tous les joueurs triées.");
    }

    /**
     * Affiche l'état complet du jeu après la distribution
     * Montre les informations de chaque joueur et l'état du centre
     * Différencie l'affichage pour les humains (cartes visibles) et les bots (cartes cachées)
     */
    public void displayGameState() {
        System.out.println("\n=============== ÉTAT INITIAL DU JEU ===============");
        System.out.printf("Mode : %s | Joueurs : %d\n", 
                (isTeamMode ? "ÉQUIPE" : "SOLO"), totalPlayers);
        System.out.printf("Joueur actuel : %s\n", game.getCurrentPlayer().getPseudo());

        // Affichage des cartes au centre (mode solo uniquement)
        if (!isTeamMode && game.getCenterCards().getSize() > 0) {
            System.out.printf("\nCartes au centre (%d) : ", game.getCenterCards().getSize());
            displayCards(game.getCenterCards().getCards(), true);
        }

        // Affichage de l'état de chaque joueur
        System.out.println("\n--- État des joueurs ---");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            displayPlayerState(player, i + 1);
        }

        System.out.println("==================================================\n");
    }

    /**
     * Affiche l'état détaillé d'un joueur
     * @param player le joueur à afficher
     * @param playerNumber numéro du joueur pour l'affichage
     */
    private void displayPlayerState(Player player, int playerNumber) {
        String playerType = (player instanceof Bot) ? "BOT" : "HUMAIN";
        String currentPlayerMarker = (game.getCurrentPlayer().equals(player)) ? " [TOUR ACTUEL]" : "";
        String teamInfo = isTeamMode ? " | Équipe " + player.getTeamId() : "";

        System.out.printf("%d. %s (%s)%s%s\n", 
                playerNumber, player.getPseudo(), playerType, teamInfo, currentPlayerMarker);

        // Affichage des cartes selon le type de joueur
        System.out.printf("   Cartes (%d) : ", player.getDeck().getSize());
        if (player instanceof User) {
            // Joueur humain : cartes visibles
            displayCards(player.getDeck().getCards(), true);
        } else {
            // Bot : cartes cachées
            System.out.println("[Cartes cachées]");
            displayCards(player.getDeck().getCards(), true);
        }

        System.out.printf("   Score : %d\n", player.getScore());
    }

    /**
     * Affiche une liste de cartes
     * @param cards la liste des cartes à afficher
     * @param showValues true pour afficher les valeurs, false pour cacher
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
}