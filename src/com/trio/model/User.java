package com.trio.model;

public class User extends Player {

    // Constructeurs
    public User(String pseudo) {
        super(pseudo);
    }

    // Getters

    // Setters

    // Méthodes Métier
    @Override
    public String chooseAction(Game game) {
        return "HUMAN_INPUT";
    }
}