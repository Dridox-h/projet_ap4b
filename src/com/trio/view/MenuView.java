package com.trio.view;

/**
 * Interface définissant les méthodes d'affichage et d'input pour le menu.
 */
public interface MenuView {

    /**
     * Affiche le message de bienvenue
     */
    void displayWelcome();

    /**
     * Demande le pseudo du joueur
     */
    String promptPseudo();

    /**
     * Demande le mode de jeu (Solo, Multijoueur local, etc.)
     */
    int promptGameMode();

    /**
     * Demande le nombre de joueurs
     */
    int promptPlayerCount();

    /**
     * Affiche la liste des joueurs créés
     */
    void displayPlayersList(String[] playerNames, boolean[] isBot);

    /**
     * Affiche un message d'information
     */
    void displayMessage(String message);

    /**
     * Affiche une erreur
     */
    void displayError(String message);
}
