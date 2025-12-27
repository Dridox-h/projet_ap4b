package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Joueur> joueurs;
    private List<Equipe> equipes;
    private Deck cartesAuCentre;
    private int joueurCourantIndex;
    private boolean modeEquipe;

    // Pour la gestion du tour actuel (cartes révélées temporairement)
    private List<Carte> cartesReveleesCeTour;

    public Game() {
        this.joueurs = new ArrayList<>();
        this.equipes = new ArrayList<>();
        this.cartesAuCentre = new Deck();
        this.cartesReveleesCeTour = new ArrayList<>();
        this.joueurCourantIndex = 0;
    }

    // --- Getters & Setters ---
    public List<Joueur> getJoueurs() { return joueurs; }
    public Deck getCartesAuCentre() { return cartesAuCentre; }
    public void setModeEquipe(boolean b) { this.modeEquipe = b; }
    public boolean isModeEquipe() { return modeEquipe; }

    public Joueur getJoueurCourant() {
        return joueurs.get(joueurCourantIndex);
    }

    public void passerAuJoueurSuivant() {
        joueurCourantIndex = (joueurCourantIndex + 1) % joueurs.size();
    }

    public List<Carte> getCartesReveleesCeTour() { return cartesReveleesCeTour; }

    public void ajouterCarteRevelee(Carte c) {
        c.setVisible(true);
        cartesReveleesCeTour.add(c);
    }

    public void resetTour() {
        // Cache les cartes si pas gagnées
        for(Carte c : cartesReveleesCeTour) {
            c.setVisible(false);
        }
        cartesReveleesCeTour.clear();
    }

    public void validerTrioGagne(Joueur gagnant) {
        // Les cartes restent visibles et vont au gagnant
        gagnant.gagnerTrio(new ArrayList<>(cartesReveleesCeTour));
        cartesReveleesCeTour.clear();
    }

    public List<Equipe> getEquipes() { return equipes; }
    public void addEquipe(Equipe e) { equipes.add(e); }
}
