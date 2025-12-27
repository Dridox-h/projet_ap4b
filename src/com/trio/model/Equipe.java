package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Equipe {
    private List<Joueur> joueurs;
    private int idEquipe;
    private String nom;

    public Equipe(int id, String nom) {
        this.idEquipe = id;
        this.nom = nom;
        this.joueurs = new ArrayList<>();
    }

    public void ajouterJoueur(Joueur j) {
        joueurs.add(j);
        j.setIdEquipe(this.idEquipe);
    }

    public List<Joueur> getJoueurs() { return joueurs; }
    public String getNom() { return nom; }

    // Calcule le score total de l'équipe
    public int getNombreTrios() {
        int count = 0;
        for(Joueur j : joueurs) {
            count += j.getTriosGagnes().taille() / 3;
        }
        return count;
    }

    public boolean aTrioDeSept() {
        for(Joueur j : joueurs) {
            int septCount = 0;
            for(Carte c : j.getTriosGagnes().getCartes()) {
                if(c.getValeur() == 7) septCount++;
            }
            if(septCount >= 3) return true;
        }
        return false;
    }
}
