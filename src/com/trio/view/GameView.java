package com.trio.view;

import com.trio.model.*;
import java.util.List;

/**
 * Interface définissant les méthodes d'affichage et d'input pour le jeu Trio.
 * Permet de découpler la logique du jeu de l'interface utilisateur.
 */
public interface GameView {

    // === AFFICHAGE ===

    /**
     * Affiche un message de bienvenue au début du jeu
     */
    void displayWelcome(int nbPlayers);

    /**
     * Affiche le début d'un nouveau tour
     */
    void displayTurnStart(Player player);

    /**
     * Affiche la main du joueur humain
     */
    void displayPlayerHand(Player player);

    /**
     * Affiche les cartes visibles de tous les joueurs et du centre
     */
    void displayVisibleCards(List<Player> players, Deck centerDeck);

    /**
     * Affiche les cartes révélées pendant ce tour
     */
    void displayRevealedCards(List<RevealedCard> revealedCards);

    /**
     * Affiche le résultat d'une révélation de carte
     */
    void displayCardRevealed(Card card, boolean isFirst, boolean isCorrect, int expectedValue);

    /**
     * Affiche un trio réussi
     */
    void displayTrioSuccess(Player winner, int trioCount);

    /**
     * Affiche un échec de tour
     */
    void displayTurnFailed();

    /**
     * Affiche le gagnant final
     */
    void displayGameWinner(Player winner);

    /**
     * Affiche un message d'erreur
     */
    void displayError(String message);

    /**
     * Affiche une action du bot
     */
    void displayBotAction(Bot bot, String action, Player target);

    // === INPUT ===

    /**
     * Demande au joueur de choisir une action (1-5, 0 pour arrêter)
     */
    int promptAction();

    /**
     * Demande au joueur de choisir un autre joueur parmi la liste
     */
    Player promptSelectPlayer(List<Player> availablePlayers);

    /**
     * Demande au joueur de choisir une carte du centre
     */
    int promptSelectCenterCard(Deck centerDeck);
}
