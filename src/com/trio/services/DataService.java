package com.trio.services;

public class DataService {
    public void sauvegarderPartie(Object game) {
        Logs.getInstance().ecrireLog("Sauvegarde de la partie en cours...");
    }

    public void chargerPartie() {
        Logs.getInstance().ecrireLog("Chargement des données...");
    }
}