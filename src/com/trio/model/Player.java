package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Player implements TrioHolder {

    // Attributs
    protected final String pseudo;
    protected Deck hand;
    protected List<Deck> trios; // Stocke les trios gagnés (les "stages" trouvés)

    // Constructeurs
    public Player(String pseudo) {
        this.pseudo = pseudo;
        this.hand = new Deck();
        this.trios = new ArrayList<>();
    }

    // Getters
    public String getPseudo() {
        return pseudo;
    }

    public Deck getDeck() {
        return hand;
    }

    /**
     * Retourne la liste des trios gagnés.
     * Utilisé par la Vue pour afficher l'entreprise du stage trouvé.
     */
    public List<Deck> getTrios() {
        return trios;
    }

    // TrioHolder Implementation
    @Override
    public void addTrio(Deck trio) {
        trios.add(trio);
    }

    @Override
    public int getTrioCount() {
        return trios.size();
    }

    // Méthodes Métier
    public String chooseAction(Game game) {
        return "DEFAULT_ACTION";
    }

    // Utile pour le débogage console
    @Override
    public String toString() {
        return pseudo + " (" + getTrioCount() + " trios)";
    }
}
