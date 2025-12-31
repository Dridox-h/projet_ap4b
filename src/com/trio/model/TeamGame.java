package com.trio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Mode de jeu en équipe.
 * Se joue à 4 ou 6 joueurs (2 ou 3 équipes de 2).
 * L'ordre de jeu alterne entre les équipes:
 * J1-Eq1, J1-Eq2, [J1-Eq3], J2-Eq1, J2-Eq2, [J2-Eq3], ...
 */
public class TeamGame implements Game {

    // Attributs
    private List<Team> teams;
    private List<Player> playOrder;
    private Deck centerDeck;
    private int currentPlayerIndex;
    private List<RevealedCard> revealedThisTurn;
    private boolean gameEnded;
    private Scanner scanner;

    // Constructeurs
    public TeamGame(List<Team> teams, Deck centerDeck) {
        if (teams.size() < 2 || teams.size() > 3) {
            throw new IllegalArgumentException("Le mode équipe nécessite 2 ou 3 équipes!");
        }
        this.teams = teams;
        this.centerDeck = centerDeck;
        this.currentPlayerIndex = 0;
        this.revealedThisTurn = new ArrayList<>();
        this.gameEnded = false;
        this.scanner = new Scanner(System.in);
        buildPlayOrder();
    }

    /**
     * Construit l'ordre de jeu en alternant entre les équipes.
     * Pour 2 équipes [A, B] avec joueurs [A1, A2] et [B1, B2]:
     * playOrder = [A1, B1, A2, B2]
     */
    private void buildPlayOrder() {
        this.playOrder = new ArrayList<>();

        // Trouver le nombre max de joueurs par équipe
        int maxPlayersPerTeam = teams.stream()
                .mapToInt(Team::getTeamSize)
                .max()
                .orElse(0);

        // Alterner: pour chaque position de joueur dans l'équipe
        for (int playerIndex = 0; playerIndex < maxPlayersPerTeam; playerIndex++) {
            // Pour chaque équipe
            for (Team team : teams) {
                Player player = team.getPlayer(playerIndex);
                if (player != null) {
                    playOrder.add(player);
                }
            }
        }
    }

    // Getters
    public List<Team> getTeams() {
        return teams;
    }

    public List<Player> getPlayOrder() {
        return playOrder;
    }

    public Deck getCenterDeck() {
        return centerDeck;
    }

    public Player getCurrentPlayer() {
        return playOrder.get(currentPlayerIndex);
    }

    public Team getTeamForPlayer(Player player) {
        for (Team team : teams) {
            if (team.hasPlayer(player)) {
                return team;
            }
        }
        return null;
    }

    public List<RevealedCard> getRevealedCards() {
        return revealedThisTurn;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Retourne tous les joueurs (de toutes les équipes)
     */
    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        for (Team team : teams) {
            allPlayers.addAll(team.getPlayers());
        }
        return allPlayers;
    }

    // Méthodes Game Interface

    @Override
    public void startGame() {
        System.out.println("=== DÉBUT DE LA PARTIE TRIO - MODE ÉQUIPE ===");
        System.out.println("Nombre d'équipes: " + teams.size());
        System.out.println("Équipes:");
        for (Team team : teams) {
            System.out.println("  " + team);
        }
        System.out.println("Objectif: 3 trios pour gagner\n");

        // Afficher l'ordre de jeu
        System.out.println("Ordre de jeu:");
        for (int i = 0; i < playOrder.size(); i++) {
            Player p = playOrder.get(i);
            Team t = getTeamForPlayer(p);
            System.out.println("  " + (i + 1) + ". " + p.getPseudo() + " (" + t.getName() + ")");
        }
        System.out.println();

        // Distribuer les cartes
        distributeCards();

        // Afficher la main du joueur
        displayUserHand();

        // Boucle principale du jeu
        while (!isFinished()) {
            Player currentPlayer = getCurrentPlayer();
            Team currentTeam = getTeamForPlayer(currentPlayer);
            System.out.println("\n═══════════════════════════════════");
            System.out.println("   Tour de " + currentPlayer.getPseudo() + " (" + currentTeam.getName() + ")");
            System.out.println("═══════════════════════════════════");

            // Afficher les scores des équipes
            displayTeamScores();

            // Afficher les cartes visibles
            displayVisibleCards();

            playTurn();

            if (!isFinished()) {
                nextTurn();
            }
        }

        // Afficher le gagnant
        TrioHolder winner = getWinner();
        if (winner instanceof Team) {
            Team winningTeam = (Team) winner;
            System.out.println("\n🎉 " + winningTeam.getName() + " GAGNE avec " + winner.getTrioCount() + " trios!");
            System.out.println("Félicitations à: " + winningTeam);
        }
    }

    /**
     * Affiche les scores des équipes
     */
    private void displayTeamScores() {
        System.out.println("\n📊 Scores des équipes:");
        for (Team team : teams) {
            System.out.println("  " + team.getName() + ": " + team.getTrioCount() + " trio(s)");
        }
    }

    /**
     * Distribue TOUTES les cartes aux joueurs (pas de centre en mode équipe)
     * 4 joueurs = 9 cartes chacun, 6 joueurs = 6 cartes chacun
     */
    public void distributeCards() {
        DrawPile drawPile = new DrawPile();
        drawPile.createDefaultCards();
        drawPile.shuffle();

        List<Player> allPlayers = getAllPlayers();
        int cardsPerPlayer = 36 / allPlayers.size(); // 9 pour 4 joueurs, 6 pour 6 joueurs

        for (Player player : allPlayers) {
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!drawPile.getDeck().isEmpty()) {
                    Card card = drawPile.getDeck().removeCard(0);
                    player.getDeck().addCard(card);
                }
            }
            player.getDeck().sort();
        }

        System.out.println("📋 Distribution: " + cardsPerPlayer + " cartes/joueur (pas de centre en mode équipe)");
    }

    /**
     * Affiche uniquement la main du joueur humain (User)
     */
    public void displayUserHand() {
        for (Player p : getAllPlayers()) {
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

        for (Player p : getAllPlayers()) {
            for (Card c : p.getDeck().getCards()) {
                if (c.isVisible()) {
                    Team team = getTeamForPlayer(p);
                    System.out.println("  " + p.getPseudo() + " (" + team.getName() + "): [" + c.getValue() + "]");
                    anyVisible = true;
                }
            }
        }

        // Pas de cartes au centre en mode équipe

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
            Team currentTeam = getTeamForPlayer(currentPlayer);
            awardTrioToTeam(currentTeam);
            System.out.println(currentTeam.getName() + " a maintenant " + currentTeam.getTrioCount() + " trio(s)");
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
        // Pas d'option centre en mode équipe
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
        List<Player> allPlayers = getAllPlayers();

        switch (action) {
            case 1: // Ma carte MIN
                return revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                return revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot ? bot.chooseTargetPlayer(allPlayers) : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        System.out.println(bot.getPseudo() + " cible " + target3.getPseudo() + " (MIN)");
                    }
                    return revealLowestCardFromPlayer(target3);
                }
                return null;

            case 4: // Carte MAX d'un autre joueur
                Player target4 = isBot ? bot.chooseTargetPlayer(allPlayers) : selectOtherPlayer(currentPlayer);
                if (target4 != null) {
                    if (isBot) {
                        System.out.println(bot.getPseudo() + " cible " + target4.getPseudo() + " (MAX)");
                    }
                    return revealHighestCardFromPlayer(target4);
                }
                return null;

            // Pas de case 5 (centre) en mode équipe

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
        for (Player p : getAllPlayers()) {
            if (!p.equals(currentPlayer) && !p.getDeck().isEmpty()) {
                Team team = getTeamForPlayer(p);
                System.out.println(index + ". " + p.getPseudo() + " (" + team.getName() + ", " + p.getDeck().getSize()
                        + " cartes)");
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
     * Action automatique pour un Bot
     */
    private int chooseBotAction(Bot bot) {
        return bot.chooseBotAction(revealedThisTurn, getAllPlayers(), centerDeck);
    }

    @Override
    public boolean isFinished() {
        for (Team team : teams) {
            if (team.getTrioCount() >= 3) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TrioHolder getWinner() {
        for (Team team : teams) {
            if (team.getTrioCount() >= 3) {
                return team;
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
            revealedThisTurn.add(new RevealedCard(card, null, index));
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

    /**
     * Attribue le trio à l'équipe gagnante
     */
    public void awardTrioToTeam(Team winner) {
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
        currentPlayerIndex = (currentPlayerIndex + 1) % playOrder.size();
    }
}
