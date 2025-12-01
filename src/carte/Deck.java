package carte;

import java.util.*;

public class Deck {

    private ArrayList<Carte> cartes = new ArrayList<>();

    public Deck() {
        // constructeur vide OK
    }

    public void addCarte(Carte carte) {
        cartes.add(carte);
    }

    public Carte getCarte(int index) {
        return cartes.get(index);
    }

    public int size() {
        return cartes.size();
    }

    public void removeCarte(int index) {
        cartes.remove(index);
    }

    public void afficherDeck() {
        for (Carte c : cartes) {
            System.out.println(c);
        }
    }

    public Carte getMaxCarte(){
        Carte max_cartes = cartes.getFirst();
        for(int i =0; i<cartes.size();i++){
            if (max_cartes.getValeur() < cartes.get(i).getValeur()){
                max_cartes = cartes.get(i);
            }
        }
        return max_cartes;
    }

    public Carte getMinCarte(){
        Carte min_cartes = cartes.getFirst();
        for(int i =0; i<cartes.size();i++){
            if (min_cartes.getValeur() > cartes.get(i).getValeur()){
                min_cartes = cartes.get(i);
            }
        }
        return min_cartes;    }

    public Deck getDeck(){
        return this;
    }

    public int getDeckTaille(){
        return cartes.size();
    }

    public void trierDeck() {
        for (int i = 0; i < cartes.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < cartes.size(); j++) {
                if (cartes.get(j).getId_carte() < cartes.get(minIndex).getId_carte()) {
                    minIndex = j;
                }
            }
            Collections.swap(cartes, i, minIndex);
        }
    }

}
