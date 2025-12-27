package com.trio.model;

import java.util.List;

public abstract class Joueur {
    protected String pseudo;
    protected Deck main; // Deck personnel
    protected Deck triosGagnes; // Cartes gagnées
    protected int idEquipe; // 0 si individuel

    public Joueur(String pseudo) {
        this.pseudo = pseudo;
        this.main = new Deck();
        this.triosGagnes = new Deck();
        this.idEquipe = -1;
    }

    public void recevoirCarte(Carte c) {
        main.ajouterCarte(c);
        main.trier(); // Toujours trié dans Trio
    }

    public Deck getMain() { return main; }
    public String getPseudo() { return pseudo; }
    public Deck getTriosGagnes() { return triosGagnes; }

    public void gagnerTrio(List<Carte> trio) {
        for(Carte c : trio) triosGagnes.ajouterCarte(c);
    }

    public int getIdEquipe() { return idEquipe; }
    public void setIdEquipe(int id) { this.idEquipe = id; }

    // Méthodes abstraites pour différencier Bot et User
    public abstract String choisirAction(Game game);
}