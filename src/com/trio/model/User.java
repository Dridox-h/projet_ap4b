package com.trio.model;

public class User extends Joueur {
    public User(String pseudo) {
        super(pseudo);
    }

    @Override
    public String choisirAction(Game game) {
        return "HUMAN_INPUT";
    }
}