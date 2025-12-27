package com.trio.model;

import java.util.Random;

public class Bot extends Joueur {
    private Random random = new Random();

    public Bot(String pseudo) {
        super(pseudo);
    }

    // Cette méthode corrige l'erreur de compilation
    @Override
    public String choisirAction(Game game) {
        return "BOT_ACTION";
    }

    public int choisirSource() {
        return random.nextInt(2) + 1;
    }

    public int choisirCible(int nbJoueurs) {
        return random.nextInt(nbJoueurs);
    }

    public int choisirTypeCarte() {
        return random.nextInt(2) + 1;
    }
}