package com.trio.controller;

import com.trio.model.*;
import com.trio.view.TeamGameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contrôleur du jeu Trio en mode Équipe.
 * Orchestre les interactions entre la TeamGameView et le TeamGame model.
 * Utilise le service Logs pour tracer l'exécution.
 */
public class TeamGameController {

    private TeamGame game;
    private TeamGameView view;

    // Flag pour limiter l'échange à 1 fois par tour
    private boolean exchangeUsedThisTurn;

    public TeamGameController(TeamGame game, TeamGameView view) {
        this.game = game;
        this.view = view;
        this.exchangeUsedThisTurn = false;
    }

    /**
     * Lance le jeu complet en mode équipe
     */
    public void startGame() {
        Logs.getInstance().writeLogs("=== Démarrage d'une nouvelle partie (Mode Équipe) ===");

        // Afficher bienvenue avec les équipes
        view.displayTeamWelcome(game.getTeams());

        // Afficher l'ordre de jeu
        view.displayPlayOrder(game.getPlayOrder(), game.getTeams());

        // Distribuer les cartes
        game.distributeCards();
        Logs.getInstance().writeLogs("Distribution des cartes terminée.");

        // Afficher la main du joueur humain
        refreshHumanView();

        // Afficher les scores initiaux
        view.displayTeamScores(game.getTeams());

        // Boucle principale du jeu
        while (!game.isFinished()) {
            Player currentPlayer = game.getCurrentPlayer();
            Team currentTeam = game.getTeamForPlayer(currentPlayer);

            Logs.getInstance()
                    .writeLogs("Début du tour de : " + currentPlayer.getPseudo() + " (" + currentTeam.getName() + ")");

            // Afficher le début du tour
            view.displayTeamTurnStart(currentPlayer, currentTeam);

            // Afficher les cartes visibles
            view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());

            // Jouer le tour
            playTurn(currentPlayer, currentTeam);

            // Afficher les scores mis à jour
            view.displayTeamScores(game.getTeams());

            if (!game.isFinished()) {
                game.nextTurn();
            }
        }

        // Afficher l'équipe gagnante
        TrioHolder winner = game.getWinner();
        if (winner instanceof Team) {
            Team winningTeam = (Team) winner;
            Logs.getInstance().writeLogs("FIN DE PARTIE - Équipe Vainqueur : " + winningTeam.getName());

            view.displayTeamWinner(winningTeam);

            // Sauvegarder le résultat et incrémenter les victoires
            DataService.getInstance().saveTeamGameResult(winningTeam, game.getTeams());
            DataService.getInstance().incrementTeamVictories(winningTeam);
        }
    }

    /**
     * Gère un tour de jeu pour un joueur
     */
    private void playTurn(Player currentPlayer, Team currentTeam) {
        boolean turnContinues = true;
        boolean turnSuccess = true;

        // Reset du flag d'échange au début de chaque nouveau tour de joueur
        exchangeUsedThisTurn = false;

        while (turnContinues && turnSuccess && game.getRevealedCards().size() < 3) {
            // Afficher les cartes révélées ce tour
            view.displayRevealedCards(game.getRevealedCards());

            // Choisir une action
            int action = chooseAction(currentPlayer);

            // Log de l'action brute pour debug (sauf Bot qui logue déjà son intention
            // parfois)
            if (!(currentPlayer instanceof Bot)) {
                Logs.getInstance().writeLogs(currentPlayer.getPseudo() + " a choisi l'action n°" + action);
            }

            // --- GESTION DE L'ACTION ÉCHANGE (6) ---
            if (action == 6) {
                if (exchangeUsedThisTurn) {
                    view.displayError("Vous avez déjà échangé ce tour !");
                    continue;
                }
                if (!game.getRevealedCards().isEmpty()) {
                    view.displayError("Impossible d'échanger après avoir révélé des cartes !");
                    continue;
                }

                Logs.getInstance().writeLogs(currentPlayer.getPseudo() + " tente un échange avec son coéquipier.");
                boolean ok = performTeamExchange(currentPlayer, currentTeam);
                if (ok) {
                    exchangeUsedThisTurn = true;
                    Logs.getInstance().writeLogs(">> Échange effectué avec succès.");
                    // Rafraîchir l'affichage après échange
                    refreshHumanView();
                    view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());
                } else {
                    Logs.getInstance().writeLogs(">> Échange annulé ou échoué.");
                    if (!(currentPlayer instanceof Bot)) {
                        view.displayError("Échange annulé ou impossible.");
                    }
                }
                continue;
            }
            // ---------------------------------------

            if (action == 0) {
                // Arrêter le tour
                if (game.getRevealedCards().size() < 2) {
                    view.displayError("Vous devez révéler au moins 2 cartes avant d'arrêter!");
                    continue;
                }
                Logs.getInstance().writeLogs(currentPlayer.getPseudo() + " décide d'arrêter son tour.");
                turnSuccess = false;
                turnContinues = false;
            } else {
                // Exécuter l'action de révélation
                Card revealedCard = executeAction(action, currentPlayer);

                if (revealedCard == null) {
                    if (currentPlayer instanceof Bot) {
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        view.displayError("Action invalide!");
                    }
                    Logs.getInstance()
                            .writeLogs("Erreur : Action invalide ou annulée par " + currentPlayer.getPseudo());
                    continue;
                }

                Logs.getInstance().writeLogs(
                        "Carte révélée : " + revealedCard.getValue() + " (" + revealedCard.getCoordinate() + ")");

                // Récupérer les infos de la carte révélée pour affichage
                List<RevealedCard> revealed = game.getRevealedCards();
                RevealedCard lastRevealed = revealed.get(revealed.size() - 1);
                Player cardOwner = lastRevealed.getOwner();
                int cardIndex = lastRevealed.getCardIndex();

                // Vérifier si la carte correspond à la série en cours
                if (revealed.size() > 1) {
                    int expectedValue = revealed.get(0).getValue();
                    if (revealedCard.getValue() != expectedValue) {
                        Logs.getInstance().writeLogs(
                                ">> Mauvaise carte ! Attendu: " + expectedValue + ", Reçu: " + revealedCard.getValue());
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, false, expectedValue);
                        view.displayVisibleCards(game.getAllPlayers(), null);
                        pause(); // Pause pour voir la carte
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        Logs.getInstance().writeLogs(">> Bonne carte ! La série continue.");
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, true, expectedValue);
                        view.displayVisibleCards(game.getAllPlayers(), null);
                        pause(); // Pause pour voir la carte
                    }
                } else {
                    // Première carte révélée
                    Logs.getInstance().writeLogs(">> Première carte de la série.");
                    view.displayCardRevealed(revealedCard, cardOwner, cardIndex, true, true, 0);
                    view.displayVisibleCards(game.getAllPlayers(), null);
                    pause(); // Pause pour voir la carte
                }
            }
        }

        // Fin du tour
        if (game.getRevealedCards().size() == 3 && game.isValidTrio()) {
            Logs.getInstance().writeLogs("SUCCÈS ! Trio validé pour l'équipe " + currentTeam.getName());
            game.awardTrioToTeam(currentTeam);
            view.displayTeamTrioSuccess(currentTeam, currentTeam.getTrioCount());
        } else if (!turnSuccess || (!game.getRevealedCards().isEmpty() && game.getRevealedCards().size() < 3)) {
            // Échec ou arrêt volontaire
            Logs.getInstance().writeLogs("Échec du tour. Les cartes sont remises face cachée.");
            view.displayTurnFailed();
            game.failTurn();

            // Réafficher l'état après échec
            refreshHumanView();
            view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());
        }
    }

    /**
     * Choisit une action selon le type de joueur.
     */
    private int chooseAction(Player player) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            if (!exchangeUsedThisTurn && game.getRevealedCards().isEmpty()) {
                if (new Random().nextDouble() < 0.20) {
                    return 6; // Action échange
                }
            }
            return bot.chooseBotAction(
                    game.getRevealedCards(),
                    game.getAllPlayers(),
                    game.getCenterDeck());
        }

        // Joueur humain
        view.displayPlayerHand(player);
        view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());
        view.displayRevealedCards(game.getRevealedCards());

        return view.promptAction();
    }

    /**
     * Exécute l'action de révélation choisie
     */
    private Card executeAction(int action, Player currentPlayer) {
        boolean isBot = currentPlayer instanceof Bot;
        Bot bot = isBot ? (Bot) currentPlayer : null;
        List<Player> allPlayers = game.getAllPlayers();

        String actionDesc = "";

        switch (action) {
            case 1: // Ma carte MIN
                actionDesc = currentPlayer.getPseudo() + " révèle sa carte MIN.";
                Logs.getInstance().writeLogs(actionDesc);
                return game.revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                actionDesc = currentPlayer.getPseudo() + " révèle sa carte MAX.";
                Logs.getInstance().writeLogs(actionDesc);
                return game.revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot
                        ? bot.chooseTargetPlayer(allPlayers)
                        : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "révèle MIN de", target3);
                    }
                    Logs.getInstance()
                            .writeLogs(currentPlayer.getPseudo() + " demande la carte MIN de " + target3.getPseudo());
                    return game.revealLowestCardFromPlayer(target3);
                }
                return null;

            case 4: // Carte MAX d'un autre joueur
                Player target4 = isBot
                        ? bot.chooseTargetPlayer(allPlayers)
                        : selectOtherPlayer(currentPlayer);
                if (target4 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "révèle MAX de", target4);
                    }
                    Logs.getInstance()
                            .writeLogs(currentPlayer.getPseudo() + " demande la carte MAX de " + target4.getPseudo());
                    return game.revealHighestCardFromPlayer(target4);
                }
                return null;

            default:
                Logs.getInstance().writeLogs("Action inconnue ou non gérée : " + action);
                return null;
        }
    }

    /**
     * Effectue un échange de carte entre 2 joueurs d'une même équipe.
     */
    private boolean performTeamExchange(Player currentPlayer, Team currentTeam) {
        // Trouver coéquipier(s) avec des cartes
        List<Player> mates = new ArrayList<>();
        for (Player p : game.getAllPlayers()) {
            if (!p.equals(currentPlayer) && currentTeam.hasPlayer(p) && !p.getDeck().isEmpty()) {
                mates.add(p);
            }
        }

        if (mates.isEmpty()) {
            if (!(currentPlayer instanceof Bot))
                view.displayError("Aucun coéquipier disponible.");
            return false;
        }

        Player mate;
        int idxA, idxB;

        if (currentPlayer instanceof Bot) {
            Bot bot = (Bot) currentPlayer;
            Random rand = new Random();
            mate = mates.get(rand.nextInt(mates.size()));

            Deck deckA = currentPlayer.getDeck();
            Deck deckB = mate.getDeck();

            if (deckA.isEmpty() || deckB.isEmpty())
                return false;

            idxA = rand.nextInt(deckA.getSize());
            idxB = rand.nextInt(deckB.getSize());

            view.displayBotAction(bot, "échange une carte avec", mate);
        } else {
            mate = view.promptSelectPlayer(mates);
            if (mate == null)
                return false;

            idxA = view.promptSelectHandCard(currentPlayer);
            if (idxA < 0)
                return false;

            idxB = view.promptSelectHandCard(mate);
            if (idxB < 0)
                return false;
        }

        // Log détaillé de l'échange
        Logs.getInstance().writeLogs(currentPlayer.getPseudo() + " échange une carte avec " + mate.getPseudo());

        return game.performExchange(currentPlayer, idxA, mate, idxB);
    }

    /**
     * Demande au joueur de sélectionner un autre joueur
     */
    private Player selectOtherPlayer(Player currentPlayer) {
        List<Player> others = new ArrayList<>();
        for (Player p : game.getAllPlayers()) {
            if (!p.equals(currentPlayer) && !p.getDeck().isEmpty()) {
                others.add(p);
            }
        }

        if (others.isEmpty()) {
            view.displayError("Aucun autre joueur disponible!");
            return null;
        }

        return view.promptSelectPlayer(others);
    }

    /**
     * Helper pour rafraîchir la main du joueur humain s'il existe
     */
    private void refreshHumanView() {
        Player humanPlayer = findHumanPlayer();
        if (humanPlayer != null) {
            view.displayPlayerHand(humanPlayer);
        }
    }

    private Player findHumanPlayer() {
        for (Player p : game.getAllPlayers()) {
            if (p instanceof User) {
                return p;
            }
        }
        return null;
    }

    // === Constante de pause ===
    private static final int PAUSE_MS = 1200; // 1.2 secondes de pause

    /**
     * Pause pour permettre de voir les cartes révélées
     */
    private void pause() {
        try {
            Thread.sleep(PAUSE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
