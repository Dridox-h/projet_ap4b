package com.trio.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Deck {
    private List<Carte> cartes;

    public Deck() {
        this.cartes = new ArrayList<>();
    }

    public void ajouterCarte(Carte c) {
        this.cartes.add(c);
    }

    public Carte retirerCarte(Carte c) {
        if (cartes.remove(c)) return c;
        return null;
    }

    public Carte retirerCarte(int index) {
        if (index >= 0 && index < cartes.size()) return cartes.remove(index);
        return null;
    }

    public List<Carte> getCartes() { return cartes; }

    public void melanger() {
        Collections.shuffle(cartes);
    }

    public void trier() {
        cartes.sort(Comparator.comparingInt(Carte::getValeur));
    }

    public int taille() { return cartes.size(); }

    public boolean estVide() { return cartes.isEmpty(); }

    // Helpers pour le gameplay Trio
    public Carte getPlusPetite() {
        return cartes.isEmpty() ? null : cartes.get(0);
    }

    public Carte getPlusGrande() {
        return cartes.isEmpty() ? null : cartes.get(cartes.size() - 1);
    }
}