package com.trio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de données pour la configuration du menu.
 * Stocke les préférences de la partie avant son lancement.
 * La logique d'affichage et de contrôle est désormais dans MenuController/MenuView.
 */
public class Menu {

    // Attributs de configuration
    private User currentUser;
    private int nbPlayers;
    private int gameMode; // 1 = Solo, 2 = Équipe

    // Constructeur vide
    public Menu() {
    }

    // === Getters & Setters ===

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    // === Helpers ===

    public boolean isTeamMode() {
        return gameMode == 2;
    }
}
