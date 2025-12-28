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
    private boolean isTeamMode;

    public GameController(int nbPlayerTotal, int nbPlayerHuman, List<Player> listPlayers, boolean isTeamMode) {
        this.nbPlayerTotal = nbPlayerTotal;
        this.nbPlayerHuman = nbPlayerHuman;
        this.nbPlayerAlive = nbPlayerTotal - nbPlayerHuman;
        this.listPlayers = listPlayers;
        this.isTeamMode = isTeamMode;
    }

    public GameController() {
        this.game = new Game();
        this.random = new Random();
    }

    public void start() {



    }

}