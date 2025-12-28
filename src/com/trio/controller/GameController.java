package com.trio.controller;

import com.trio.model.*;
//import com.trio.view.*;

import java.util.List;
import java.util.Random;

public class GameController {
    private Game game;
    private Random random;
    private List<Player> listPlayers;
    private int nbPlayerTotal;
    private int nbPlayerHuman;
    private int nbPlayerAlive;

    public GameController(int nbPlayerTotal, int nbPlayerHuman, List<Player> listPlayers) {
        this.nbPlayerTotal = nbPlayerTotal;
        this.nbPlayerHuman = nbPlayerHuman;
        this.nbPlayerAlive = nbPlayerTotal - nbPlayerHuman;
        this.listPlayers = listPlayers;
    }

    public GameController() {
        this.game = new Game();
        this.random = new Random();
    }

    public void start() {
        // 1. Affiche le menu et attend la validation


        // 2. Récupère les deux infos du menu

        // 3. Crée l'interface de jeu avec les bons arguments

        // 4. Configure la logique métier
    }

    private void configurerPartie(int nbTotal, int nbHumains) {
    }
}