package com.trio.model;

import java.util.List;

public class Player {
    protected final String pseudo;
    protected Deck deck;
    protected Deck triosWon;
    protected int teamId;

    public Player(String pseudo) {
        this.pseudo = pseudo;
        this.deck = new Deck();
        this.triosWon = new Deck();
        this.teamId = -1;
    }

    public String getPseudo() { 
        return pseudo; 
    }

    public Deck getDeck() { 
        return deck; 
    }

    public Deck getTriosWon() { 
        return triosWon; 
    }

    public void winTrio(List<Card> trio) {
        for (Card card : trio) {
            triosWon.addCard(card);
        }
    }

    public int getTeamId() { 
        return teamId; 
    }

    public void setTeamId(int teamId) { 
        this.teamId = teamId; 
    }

    public int getScore() { 
        return triosWon.getSize(); 
    }

    public String chooseAction(Game game) {
        return "DEFAULT_ACTION";
    }
}