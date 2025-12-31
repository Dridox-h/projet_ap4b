package com.trio.view;

import com.trio.model.*;
import java.util.List;

/**
 * Interface spécifique pour l'affichage du mode Team Game.
 * Étend GameView avec des méthodes pour les équipes et les actions spécifiques (échange).
 */
public interface TeamGameView extends GameView {

    // === AFFICHAGE ÉQUIPES ===

    /**
     * Affiche le message de bienvenue en mode équipe
     */
    void displayTeamWelcome(List<Team> teams);

    /**
     * Affiche le début d'un tour avec info équipe
     */
    void displayTeamTurnStart(Player player, Team team);

    /**
     * Affiche les scores des équipes
     */
    void displayTeamScores(List<Team> teams);

    /**
     * Affiche un trio réussi pour une équipe
     */
    void displayTeamTrioSuccess(Team team, int trioCount);

    /**
     * Affiche l'équipe gagnante
     */
    void displayTeamWinner(Team winner);

    /**
     * Affiche l'ordre de jeu des équipes
     */
    void displayPlayOrder(List<Player> playOrder, List<Team> teams);

    /**
     * Met à jour les infos d'un joueur avec son équipe
     */
    void displayPlayerWithTeam(Player player, Team team);

    // === INPUT SPÉCIFIQUE ===

    /**
     * Demande de sélectionner une carte spécifique dans la main d'un joueur.
     * Utilisé pour l'échange de cartes entre coéquipiers.
     * @param player Le joueur dont on affiche la main
     * @return L'index de la carte choisie
     */
    int promptSelectHandCard(Player player);
}
