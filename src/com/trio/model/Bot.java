package com.trio.model;

import java.util.Random;

public class Bot extends Player {
    private Random random = new Random();

    public Bot(String pseudo) {
        super(pseudo);
    }

    @Override
    public String chooseAction(Game game) {
        return "BOT_ACTION";
    }

    public int chooseOrigin() {
        return random.nextInt(2) + 1;
    }

    public int chooseTarget(int nbPlayers) {
        return random.nextInt(nbPlayers);
    }

    public int chooseCardType() {
        return random.nextInt(2) + 1;
    }
}