package com.trio.model;

import java.util.List;

public class Player {
    protected String pseudo;
    protected Deck main; // Deck personnel
    protected Deck trioWins; // Cartes gagnées
    protected int idEquipe; // 0 si individuel

    public Player(String pseudo) {
        this.pseudo = pseudo;
        this.main = new Deck();
        this.trioWins = new Deck();
        this.idEquipe = -1;
    }

    public void receiveCard(Card c) {
        main.addCard(c);
        main.sort(); // Toujours trié dans Trio
    }

    public Deck getMain() { return main; }
    public String getPseudo() { return pseudo; }
    public Deck getTrioWins() { return trioWins; }

    public void winTrio(List<Card> trio) {
        for(Card c : trio) trioWins.addCard(c);
    }

    public int getIdTeam() { return idEquipe; }
    public void setIdTeam(int id) { this.idEquipe = id; }

    // Méthodes abstraites pour différencier Bot et User
    //public void String chooseAction(Game game);
}