package com.trio.model;

public class Carte {
    private int valeur;
    private String cheminImage;
    private boolean estVisible;

    public Carte(int valeur) {
        this.valeur = valeur;
        // Format demandé: Images[i]
        this.cheminImage = "Image" + valeur + ".jpg";
        this.estVisible = false;
    }

    public int getValeur() { return valeur; }

    public String getCheminImage() { return cheminImage; }

    public boolean estVisible() { return estVisible; }
    public void setVisible(boolean visible) { this.estVisible = visible; }

    @Override
    public String toString() {
        return estVisible ? "[" + valeur + "]" : "[?]";
    }
}
