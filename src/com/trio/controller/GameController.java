package com.trio.controller;

import com.trio.model.*;

import com.trio.view.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur du jeu Trio.
 * Orchestre les interactions entre la View et le Model (SoloGame).
 * Utilise le service Logs pour tracer l'exécution.
 */
public class GameController {

    private SoloGame game;
    private GameView view;

    public GameController(SoloGame game, GameView view) {
        this.game = game;
        this.view = view;
    }

    /**
     * Lance le jeu complet
     */
    public void startGame() {
        Logs.getInstance().writeLogs("=== Démarrage d'une nouvelle partie (Solo) ===");
        view.displayWelcome(game.getPlayers().size());

        // Distribuer les cartes
        game.distributeCards();
        Logs.getInstance().writeLogs("Distribution des cartes effectuée.");

        // Afficher la main du joueur humain
        Player humanPlayer = findHumanPlayer();
        if (humanPlayer != null) {
            view.displayPlayerHand(humanPlayer);
        }

        // Boucle principale du jeu
        while (!game.isFinished()) {
            Player currentPlayer = game.getCurrentPlayer();
            Logs.getInstance().writeLogs("Début du tour de : " + currentPlayer.getPseudo());
            view.displayTurnStart(currentPlayer);

            // Afficher les cartes visibles au début du tour
            view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());

            boolean trioWon = playTurn(currentPlayer);

            if (!game.isFinished() && !trioWon) {
                game.nextTurn();
            }
        }

        // Afficher le gagnant
        TrioHolder winner = game.getWinner();
        if (winner instanceof Player) {
            Player pWinner = (Player) winner;
            Logs.getInstance().writeLogs("FIN DE PARTIE - Vainqueur : " + pWinner.getPseudo());

            // Vérifier si le gagnant est un User (joueur humain) ou un Bot
            if (pWinner instanceof User) {
                // Le joueur humain a gagné
                view.displayGameWinner(pWinner);
            } else {
                // Un bot a gagné, afficher l'écran de défaite
                view.displayDefeat(pWinner.getPseudo());
            }

            // Sauvegarder le résultat et incrémenter les victoires
            DataService.getInstance().saveGameResult(pWinner, game.getPlayers(), "Solo");
            DataService.getInstance().incrementVictory(pWinner);
        }
    }

    /**
     * Gère un tour de jeu pour un joueur
     * 
     * @return true si un trio a été gagné, false sinon
     */
    private boolean playTurn(Player currentPlayer) {
        boolean turnContinues = true;
        boolean turnSuccess = true;

        while (turnContinues && turnSuccess && game.getRevealedCards().size() < 3) {
            // Choisir une action
            int action = chooseAction(currentPlayer);

            // Log de l'action brute (sauf si c'est un bot qui gère ses propres logs
            // d'intention)
            if (!(currentPlayer instanceof Bot)) {
                Logs.getInstance().writeLogs(currentPlayer.getPseudo() + " a choisi l'action n°" + action);
            }

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
                // Exécuter l'action
                Card revealedCard = executeAction(action, currentPlayer);

                if (revealedCard == null) {
                    view.displayError("Action invalide!");
                    Logs.getInstance()
                            .writeLogs("Erreur : Action invalide ou annulée par " + currentPlayer.getPseudo());
                    continue;
                }

                Logs.getInstance().writeLogs(
                        "Carte révélée : " + revealedCard.getValue() + " (" + revealedCard.getCoordinate() + ")");

                // Récupérer l'owner et l'index de la carte révélée (dernière ajoutée)
                List<RevealedCard> revealed = game.getRevealedCards();
                RevealedCard lastRevealed = revealed.get(revealed.size() - 1);
                Player cardOwner = lastRevealed.getOwner();
                int cardIndex = lastRevealed.getCardIndex();

                // Vérifier si la carte correspond
                if (revealed.size() > 1) {
                    int expectedValue = revealed.get(0).getValue();
                    if (revealedCard.getValue() != expectedValue) {
                        Logs.getInstance().writeLogs(
                                ">> Mauvaise carte ! Attendu: " + expectedValue + ", Reçu: " + revealedCard.getValue());
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, false, expectedValue);
                        view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
                        pause(); // Pause pour voir la carte
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        Logs.getInstance().writeLogs(">> Bonne carte ! La série continue.");
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, true, expectedValue);
                        view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
                        pause(); // Pause pour voir la carte
                    }
                } else {
                    Logs.getInstance().writeLogs(">> Première carte de la série.");
                    view.displayCardRevealed(revealedCard, cardOwner, cardIndex, true, true, 0);
                    view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
                    pause(); // Pause pour voir la carte
                }
            }
        }

        // Fin du tour
        if (game.getRevealedCards().size() == 3 && game.isValidTrio()) {
            Logs.getInstance().writeLogs("SUCCÈS ! Trio validé pour " + currentPlayer.getPseudo());
            game.awardTrioToWinner(currentPlayer);
            view.displayTrioSuccess(currentPlayer, currentPlayer.getTrioCount());
            return true; // Trio gagné, le joueur rejoue
        } else if (!turnSuccess || !game.getRevealedCards().isEmpty()) {
            Logs.getInstance().writeLogs("Échec du tour. Les cartes sont remises face cachée.");
            view.displayTurnFailed();
            game.failTurn();

            // Réafficher l'état après échec
            Player humanPlayer = findHumanPlayer();
            if (humanPlayer != null) {
                view.displayPlayerHand(humanPlayer);
            }
            view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
        }
        return false; // Pas de trio, passer au joueur suivant
    }

    /**
     * Choisit une action selon le type de joueur
     */
    private int chooseAction(Player player) {
        if (player instanceof Bot) {
            return ((Bot) player).chooseBotAction(
                    game.getRevealedCards(),
                    game.getPlayers(),
                    game.getCenterDeck());
        }

        // Joueur humain: afficher l'état et demander l'action
        view.displayPlayerHand(player);
        view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
        view.displayRevealedCards(game.getRevealedCards());

        return view.promptAction();
    }

    /**
     * Exécute l'action choisie
     */
    private Card executeAction(int action, Player currentPlayer) {
        boolean isBot = currentPlayer instanceof Bot;
        Bot bot = isBot ? (Bot) currentPlayer : null;

        // Log descriptif de l'action
        String actionDesc = "";

        switch (action) {
            case 1: // Ma carte MIN
                actionDesc = currentPlayer.getPseudo() + " révèle sa carte la plus faible.";
                Logs.getInstance().writeLogs(actionDesc);
                return game.revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                actionDesc = currentPlayer.getPseudo() + " révèle sa carte la plus forte.";
                Logs.getInstance().writeLogs(actionDesc);
                return game.revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot
                        ? bot.chooseTargetPlayer(game.getPlayers())
                        : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "cible", target3);
                    }
                    Logs.getInstance()
                            .writeLogs(currentPlayer.getPseudo() + " demande la carte MIN de " + target3.getPseudo());
                    return game.revealLowestCardFromPlayer(target3);
                }
                return null;

            case 4: // Carte MAX d'un autre joueur
                Player target4 = isBot
                        ? bot.chooseTargetPlayer(game.getPlayers())
                        : selectOtherPlayer(currentPlayer);
                if (target4 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "cible", target4);
                    }
                    Logs.getInstance()
                            .writeLogs(currentPlayer.getPseudo() + " demande la carte MAX de " + target4.getPseudo());
                    return game.revealHighestCardFromPlayer(target4);
                }
                return null;

            case 5: // Carte du centre
                int centerIndex;
                if (isBot) {
                    centerIndex = bot.chooseCenterCardIndex(game.getCenterDeck());
                    if (centerIndex >= 0) {
                        view.displayBotAction(bot, "révèle une carte du centre", null);
                    }
                } else {
                    centerIndex = view.promptSelectCenterCard(game.getCenterDeck());
                }
                if (centerIndex >= 0) {
                    Logs.getInstance()
                            .writeLogs(currentPlayer.getPseudo() + " révèle la carte du centre n°" + (centerIndex + 1));
                    return game.revealCardFromCenter(centerIndex);
                }
                return null;

            default:
                Logs.getInstance().writeLogs("Action inconnue : " + action);
                return null;
        }
    }

    /**
     * Demande au joueur de sélectionner un autre joueur
     */
    private Player selectOtherPlayer(Player currentPlayer) {
        List<Player> others = new ArrayList<>();
        for (Player p : game.getPlayers()) {
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
     * Trouve le joueur humain (User)
     */
    private Player findHumanPlayer() {
        for (Player p : game.getPlayers()) {
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
