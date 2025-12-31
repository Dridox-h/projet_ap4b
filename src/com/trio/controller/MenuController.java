package com.trio.controller;

import com.trio.model.*;
import com.trio.view.MenuView;
import com.trio.view.GameView;

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
     * Lance le processus de configuration et retourne les données nécessaires
     */
    public void configure() {
        // Afficher bienvenue
        menuView.displayWelcome();

        // Demander le pseudo
        String pseudo = menuView.promptPseudo();
        this.currentUser = new User(pseudo);

        // Demander le mode de jeu
        this.gameMode = menuView.promptGameMode();

        // Demander le nombre de joueurs
        this.nbPlayers = menuView.promptPlayerCount();
    }

    /**
     * Crée la liste des joueurs
     */
    public List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(currentUser);

        // Créer les bots
        for (int i = 1; i < nbPlayers; i++) {
            players.add(new Bot("Bot" + i));
        }

        // Afficher la liste des joueurs
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
     * Lance le jeu avec le GameController approprié selon le mode
     */
    public void startGame(GameView gameView) {
        List<Player> players = createPlayers();

        if (gameMode == 2) {
            // Mode Équipe - TeamGame gère son propre affichage console
            List<Team> teams = createTeams(players);
            TeamGame game = new TeamGame(teams, new Deck());
            game.startGame(); // TeamGame a son propre système d'affichage
        } else {
            // Mode Solo
            SoloGame game = new SoloGame(players, new Deck());
            GameController gameController = new GameController(game, gameView);
            gameController.startGame();
        }
    }

    /**
     * Crée les équipes à partir de la liste de joueurs
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

    // Getters
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
