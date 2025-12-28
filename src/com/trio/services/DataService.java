package com.trio.services;

public class DataService {
    public void saveGame(Object game) {
        Logs.getInstance().writeLogs("Sauvegarde de la partie en cours...");
    }

    public void loadGame() {
        Logs.getInstance().writeLogs("Chargement des données...");
    }
}