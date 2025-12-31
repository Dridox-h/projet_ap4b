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
     * Demande le nombre de joueurs selon le mode de jeu
     * 
     * @param gameMode 1 = Solo (3-6), 2 = Équipe (4 ou 6 seulement)
     */
    int promptPlayerCount(int gameMode);

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
