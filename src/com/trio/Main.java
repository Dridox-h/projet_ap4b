package com.trio;

import com.trio.controller.MenuController;
import com.trio.view.SwingMenuView;
import com.trio.view.SwingGameView;

import javax.swing.SwingUtilities;

/**
 * Point d'entrée du jeu Trio avec interface graphique Swing.
 * Utilise le pattern MVC pour le menu et le jeu.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // === MENU MVC ===
            // Créer la View du menu
            SwingMenuView menuView = new SwingMenuView();

            // Créer le Controller du menu
            MenuController menuController = new MenuController(menuView);

            // Configurer la partie (pseudo, mode, joueurs)
            menuController.configure();

            // Fermer le menu
            menuView.close();

            // === JEU MVC ===
            // Créer la View du jeu
            SwingGameView gameView = new SwingGameView();

            // Lancer le jeu dans un thread séparé pour ne pas bloquer l'UI
            new Thread(() -> {
                menuController.startGame(gameView);
            }).start();
        });
    }
}