package com.trio;

import com.trio.controller.MenuController;
import com.trio.view.SwingGameView;
import com.trio.view.SwingTeamGameView;
import com.trio.view.SwingMenuView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // === MENU ===
            SwingMenuView menuView = new SwingMenuView();
            MenuController menuController = new MenuController(menuView);

            // Cette méthode affiche le menu et attend la configuration
            menuController.configure();

            // Une fois configuré, on ferme le menu
            menuView.close();

            // === LANCEMENT DU JEU ===
            new Thread(() -> {
                // Le contrôleur de menu sait quel mode a été choisi.
                // On doit lui passer la bonne vue selon le mode.

                if (menuController.isTeamMode()) {
                    // Si mode équipe sélectionné
                    SwingTeamGameView teamView = new SwingTeamGameView();
                    menuController.startTeamGame(teamView);
                } else {
                    // Sinon mode standard
                    SwingGameView gameView = new SwingGameView();
                    menuController.startGame(gameView);
                }
            }).start();
        });
    }
}
