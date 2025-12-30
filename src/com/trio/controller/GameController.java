package com.trio.controller;

import com.trio.model.*;
import com.trio.view.GameView;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur du jeu Trio.
 * Orchestre les interactions entre la View et le Model (SoloGame).
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
        view.displayWelcome(game.getPlayers().size());

        // Distribuer les cartes
        game.distributeCards();

        // Afficher la main du joueur humain
        Player humanPlayer = findHumanPlayer();
        if (humanPlayer != null) {
            view.displayPlayerHand(humanPlayer);
        }

        // Boucle principale du jeu
        while (!game.isFinished()) {
            Player currentPlayer = game.getCurrentPlayer();
            view.displayTurnStart(currentPlayer);

            // Afficher les cartes visibles au début du tour
            view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());

            playTurn(currentPlayer);

            if (!game.isFinished()) {
                game.nextTurn();
            }
        }

        // Afficher le gagnant
        TrioHolder winner = game.getWinner();
        if (winner instanceof Player) {
            view.displayGameWinner((Player) winner);
        }
    }

    /**
     * Gère un tour de jeu pour un joueur
     */
    private void playTurn(Player currentPlayer) {
        boolean turnContinues = true;
        boolean turnSuccess = true;

        while (turnContinues && turnSuccess && game.getRevealedCards().size() < 3) {
            // Choisir une action
            int action = chooseAction(currentPlayer);

            if (action == 0) {
                // Arrêter le tour
                if (game.getRevealedCards().size() < 2) {
                    view.displayError("Vous devez révéler au moins 2 cartes avant d'arrêter!");
                    continue;
                }
                turnSuccess = false;
                turnContinues = false;
            } else {
                // Exécuter l'action
                Card revealedCard = executeAction(action, currentPlayer);

                if (revealedCard == null) {
                    view.displayError("Action invalide!");
                    continue;
                }

                // Récupérer l'owner et l'index de la carte révélée (dernière ajoutée)
                List<RevealedCard> revealed = game.getRevealedCards();
                RevealedCard lastRevealed = revealed.get(revealed.size() - 1);
                Player cardOwner = lastRevealed.getOwner();
                int cardIndex = lastRevealed.getCardIndex();

                // Vérifier si la carte correspond
                if (revealed.size() > 1) {
                    int expectedValue = revealed.get(0).getValue();
                    if (revealedCard.getValue() != expectedValue) {
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, false, expectedValue);
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        view.displayCardRevealed(revealedCard, cardOwner, cardIndex, false, true, expectedValue);
                    }
                } else {
                    view.displayCardRevealed(revealedCard, cardOwner, cardIndex, true, true, 0);
                }
            }
        }

        // Fin du tour
        if (game.getRevealedCards().size() == 3 && game.isValidTrio()) {
            game.awardTrioToWinner(currentPlayer);
            view.displayTrioSuccess(currentPlayer, currentPlayer.getTrioCount());
        } else if (!turnSuccess || game.getRevealedCards().size() > 0) {
            view.displayTurnFailed();
            game.failTurn();

            // Réafficher l'état après échec
            Player humanPlayer = findHumanPlayer();
            if (humanPlayer != null) {
                view.displayPlayerHand(humanPlayer);
            }
            view.displayVisibleCards(game.getPlayers(), game.getCenterDeck());
        }
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

        switch (action) {
            case 1: // Ma carte MIN
                return game.revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                return game.revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot
                        ? bot.chooseTargetPlayer(game.getPlayers())
                        : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "cible", target3);
                    }
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
                    return game.revealCardFromCenter(centerIndex);
                }
                return null;

            default:
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
}
