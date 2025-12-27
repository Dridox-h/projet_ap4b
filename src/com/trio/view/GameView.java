package com.trio.view;

import com.trio.model.*;
import javax.swing.JOptionPane;

public class GameView {
    private GameGUI gameGUI;

    // On passe les paramètres nécessaires au constructeur de GameGUI
    public void initialiserPlateau(int nbTotal, int nbBots) {
        this.gameGUI = new GameGUI(nbTotal, nbBots);
        this.gameGUI.setVisible(true);
    }

    public void afficherMessage(String msg) {
        if (gameGUI != null) {
            // Appel de la méthode créée ci-dessus
            gameGUI.ajouterLog(msg);
        } else {
            System.out.println(msg);
        }
    }

    // Cette méthode appelle les fonctions de mise à jour du GUI
    public void rafraichir(Game game) {
        if (gameGUI != null) {
            gameGUI.afficherPlateau(game);
            gameGUI.setTourLabel("Tour de : " + game.getJoueurCourant().getPseudo());
        }
    }

    public int demanderEntier(String message, int min, int max) {
        String res = JOptionPane.showInputDialog(gameGUI, message);
        try {
            int val = Integer.parseInt(res);
            if (val >= min && val <= max) return val;
        } catch (Exception e) { }
        return min; // Valeur par défaut si erreur
    }
}