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

    // Pour la version console (si utilisée sans GUI)
    private Scanner scanner;

    // Constructeur
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

    // === Getters ===

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

    // === Méthodes Game Interface (Logique principale) ===

    @Override
    public void startGame() {
        // Cette méthode est principalement utilisée pour le mode Console.
        // En mode Swing, le Controller gère la boucle principale.
        System.out.println("=== DÉBUT DE LA PARTIE TRIO - MODE ÉQUIPE ===");
        distributeCards();
        playTurn(); // Exemple simplifié
    }

    /**
     * Distribue TOUTES les cartes aux joueurs (pas de centre en mode équipe par défaut,
     * sauf variante spécifique, ici on suit la règle standard où tout est distribué).
     * 4 joueurs = 9 cartes chacun, 6 joueurs = 6 cartes chacun.
     */
    public void distributeCards() {
        DrawPile drawPile = new DrawPile();
        drawPile.createDefaultCards();
        drawPile.shuffle();

        List<Player> allPlayers = getAllPlayers();
        // Si 4 joueurs (36 cartes) -> 9 cartes/joueur
        // Si 6 joueurs (36 cartes) -> 6 cartes/joueur
        int cardsPerPlayer = 36 / allPlayers.size();

        for (Player player : allPlayers) {
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!drawPile.getDeck().isEmpty()) {
                    Card card = drawPile.getDeck().removeCard(0);
                    player.getDeck().addCard(card);
                }
            }
            player.getDeck().sort();
        }
    }

    @Override
    public void playTurn() {
        // En architecture MVC, cette méthode est souvent pilotée par le Controller.
        // Voir TeamGameController.playTurn() pour la logique d'orchestration.
    }

    @Override
    public boolean isFinished() {
        for (Team team : teams) {
            if (team.getTrioCount() >= 3) { // Condition de victoire standard (3 trios)
                // Note: La règle du "7" (trio de 7 gagne immédiatement) peut être ajoutée ici
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

    // === Actions de jeu (Model) ===

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
        if (centerDeck == null || centerDeck.isEmpty() || index < 0 || index >= centerDeck.getSize()) {
            return null;
        }
        Card card = centerDeck.getCard(index);
        if (card != null && !card.isVisible()) {
            card.setVisible();
            revealedThisTurn.add(new RevealedCard(card, null, index));
        }
        return card;
    }

    /**
     * Effectue un échange de cartes physique entre deux decks.
     * Cette méthode est agnostique de l'UI et gère uniquement les données.
     *
     * @param playerA Premier joueur
     * @param indexA Index de la carte dans le deck de A
     * @param playerB Second joueur
     * @param indexB Index de la carte dans le deck de B
     * @return true si l'échange a réussi
     */
    public boolean performExchange(Player playerA, int indexA, Player playerB, int indexB) {
        List<Card> deckA = playerA.getDeck().getCards();
        List<Card> deckB = playerB.getDeck().getCards();

        if (indexA < 0 || indexA >= deckA.size() || indexB < 0 || indexB >= deckB.size()) {
            return false;
        }

        Card cardA = deckA.get(indexA);
        Card cardB = deckB.get(indexB);

        // Swap
        deckA.set(indexA, cardB);
        deckB.set(indexB, cardA);

        // Re-trier les mains est souvent nécessaire après un échange pour maintenir l'ordre
        // Mais attention : cela changerait les index des cartes.
        // Dans Trio, l'échange se fait "à l'aveugle" ou "en place" ?
        // Si on veut maintenir l'ordre croissant strict de Trio :
        playerA.getDeck().sort();
        playerB.getDeck().sort();

        return true;
    }

    // === Validation et Fin de tour ===

    public boolean isValidTrio() {
        if (revealedThisTurn.size() != 3) {
            return false;
        }
        int value = revealedThisTurn.get(0).getValue();
        return revealedThisTurn.stream().allMatch(rc -> rc.getValue() == value);
    }

    /**
     * Attribue le trio à l'équipe gagnante, retire les cartes des mains/centre.
     */
    public void awardTrioToTeam(Team winner) {
        Deck trio = new Deck();

        for (RevealedCard rc : revealedThisTurn) {
            Card card = rc.getCard();

            // Retirer la carte de son origine
            if (rc.getOwner() != null) {
                rc.getOwner().getDeck().removeCard(card);
            } else if (centerDeck != null) {
                centerDeck.removeCard(card);
            }

            // Ajouter au trio gagné (la carte redevient invisible ou visible selon choix d'affichage)
            // Généralement on les garde visibles dans la pile des gains
            card.setVisible();
            trio.addCard(card);
        }

        winner.addTrio(trio);
        revealedThisTurn.clear();
    }

    /**
     * Échec du tour : on remet tout face cachée.
     */
    public void failTurn() {
        for (RevealedCard rc : revealedThisTurn) {
            rc.getCard().setInvisible();
        }
        revealedThisTurn.clear();
    }

    /**
     * Passe au joueur suivant.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playOrder.size();
    }
}
