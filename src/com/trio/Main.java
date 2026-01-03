package com.trio;

import com.trio.view.MenuGUI;
import javax.swing.SwingUtilities;

/**
 * Point d'entrée du jeu Trio avec interface graphique Swing.
 * Lance le nouveau menu GUI moderne.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGUI menuGUI = new MenuGUI();
            menuGUI.setVisible(true);
        });
    }
}