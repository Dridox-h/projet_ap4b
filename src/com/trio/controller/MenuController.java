package com.trio.controller;

import com.trio.model.*;
import com.trio.view.MenuView;
import com.trio.view.GameView;
import com.trio.view.TeamGameView;
import com.trio.view.SwingTeamGameView;
import com.trio.view.SwingGameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur du menu principal.
 * Gère la configuration de la partie avant de la lancer.
 */
public class MenuController {

    private MenuView menuView;
    private User currentUser;
    private int nbPlayers;
    private int gameMode;

    public MenuController(MenuView menuView) {
        this.menuView = menuView;
    }

    /**
     * Lance le processus de configuration.
     * Cette méthode est généralement bloquante dans une architecture simple,
     * car les boîtes de dialogue Swing (modales) suspendent l'exécution ici.
     */
    public void configure() {
        // Afficher bienvenue (titre)
        menuView.displayWelcome();

        // 1. Demander le pseudo
        String pseudo = menuView.promptPseudo();
        this.currentUser = new User(pseudo);

        // 2. Demander le mode de jeu (1=Solo, 2=Équipe)
        this.gameMode = menuView.promptGameMode();

        // 3. Demander le nombre de joueurs selon le mode
        this.nbPlayers = menuView.promptPlayerCount(gameMode);
    }

    /**
     * Crée la liste des joueurs (1 Humain + N-1 Bots)
     */
    public List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(currentUser);

        // Créer les bots
        for (int i = 1; i < nbPlayers; i++) {
            players.add(new Bot("Bot" + i));
        }

        // Afficher la liste des joueurs pour confirmation visuelle
        String[] names = new String[players.size()];
        boolean[] isBot = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            names[i] = players.get(i).getPseudo();
            isBot[i] = players.get(i) instanceof Bot;
        }
        menuView.displayPlayersList(names, isBot);

        return players;
    }

    /**
     * Lance le jeu en fonction du mode choisi.
     * Si le paramètre gameView est générique, on caste ou on en crée un nouveau si nécessaire.
     */
    public void startGame(GameView gameView) {
        List<Player> players = createPlayers();

        if (gameMode == 2) {
            // === MODE ÉQUIPE ===
            // Nécessite une TeamGameView spécifique
            TeamGameView teamView;
            if (gameView instanceof TeamGameView) {
                teamView = (TeamGameView) gameView;
            } else {
                // Fallback si la vue passée n'est pas compatible
                teamView = new SwingTeamGameView();
            }

            List<Team> teams = createTeams(players);

            // Création du Model et Controller Équipe
            TeamGame game = new TeamGame(teams, new Deck());
            TeamGameController teamController = new TeamGameController(game, teamView);

            teamController.startGame();

        } else {
            // === MODE SOLO ===
            // Création du Model et Controller Standard
            SoloGame game = new SoloGame(players, new Deck());
            GameController gameController = new GameController(game, gameView);

            gameController.startGame();
        }
    }

    /**
     * Surcharge pour faciliter l'appel depuis le Main si on veut lancer un TeamGame spécifiquement
     */
    public void startTeamGame(TeamGameView teamView) {
        this.gameMode = 2; // Force le mode équipe
        startGame(teamView);
    }

    /**
     * Crée les équipes automatiquement : (J1, J2), (J3, J4)...
     */
    private List<Team> createTeams(List<Player> players) {
        List<Team> teams = new ArrayList<>();
        int teamSize = 2;
        int nbTeams = players.size() / teamSize;

        for (int i = 0; i < nbTeams; i++) {
            List<Player> teamPlayers = new ArrayList<>();
            for (int j = 0; j < teamSize; j++) {
                teamPlayers.add(players.get(i * teamSize + j));
            }
            Team team = new Team("Équipe " + (char) ('A' + i), teamPlayers);
            teams.add(team);
        }
        return teams;
    }

    // Getters pour que le Main puisse savoir quel mode a été choisi
    public boolean isTeamMode() {
        return gameMode == 2;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public int getGameMode() {
        return gameMode;
    }
}
