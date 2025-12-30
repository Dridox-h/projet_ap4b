package com.trio;

import com.trio.model.*;
import com.trio.view.SwingGameView;
import com.trio.controller.GameController;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Point d'entrée du jeu Trio avec interface graphique Swing.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Créer la View graphique
            SwingGameView view = new SwingGameView();

            // Demander le pseudo
            String pseudo = view.promptPseudo();
            if (pseudo == null || pseudo.trim().isEmpty()) {
                pseudo = "Joueur";
            }

            // Demander le nombre de joueurs
            int nbPlayers = view.promptPlayerCount();

            // Créer les joueurs
            List<Player> players = new ArrayList<>();
            players.add(new User(pseudo));
            for (int i = 1; i < nbPlayers; i++) {
                players.add(new Bot("Bot" + i));
            }

            // Créer le Model
            SoloGame game = new SoloGame(players, new Deck());

            // Créer le Controller et lancer le jeu dans un thread séparé
            GameController controller = new GameController(game, view);

            // Lancer le jeu en arrière-plan pour ne pas bloquer l'UI
            new Thread(() -> {
                controller.startGame();
            }).start();
        });
    }
}