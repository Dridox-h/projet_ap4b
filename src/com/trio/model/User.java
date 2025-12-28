package com.trio.model;

public class User extends Player {
    public User(String pseudo) {
        super(pseudo);
    }

    public String chooseAction(Game game) {
        return "HUMAN_INPUT";
    }
}