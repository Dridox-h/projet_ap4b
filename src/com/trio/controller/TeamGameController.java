package com.trio.controller;

import com.trio.model.*;
import com.trio.view.TeamGameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contrôleur du jeu Trio en mode Équipe.
 * Orchestre les interactions entre la TeamGameView et le TeamGame model.
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
        // Afficher bienvenue avec les équipes
        view.displayTeamWelcome(game.getTeams());

        // Afficher l'ordre de jeu
        view.displayPlayOrder(game.getPlayOrder(), game.getTeams());

        // Distribuer les cartes
        game.distributeCards();

        // Afficher la main du joueur humain
        refreshHumanView();

        // Afficher les scores initiaux
        view.displayTeamScores(game.getTeams());

        // Boucle principale du jeu
        while (!game.isFinished()) {
            Player currentPlayer = game.getCurrentPlayer();
            Team currentTeam = game.getTeamForPlayer(currentPlayer);

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
            view.displayTeamWinner((Team) winner);
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

                boolean ok = performTeamExchange(currentPlayer, currentTeam);
                if (ok) {
                    exchangeUsedThisTurn = true;
                    // Rafraîchir l'affichage après échange
                    refreshHumanView();
                    view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());
                } else {
                    // Si l'échange a échoué (annulation user, ou pas de cartes), on affiche l'erreur
                    // mais on ne change pas le flag, le joueur peut retenter ou jouer autre chose.
                    if (!(currentPlayer instanceof Bot)) {
                        view.displayError("Échange annulé ou impossible.");
                    }
                }
                // L'échange ne compte pas comme une révélation, on continue la boucle
                continue;
            }
            // ---------------------------------------

            if (action == 0) {
                // Arrêter le tour
                if (game.getRevealedCards().size() < 2) {
                    view.displayError("Vous devez révéler au moins 2 cartes avant d'arrêter!");
                    continue;
                }
                turnSuccess = false;
                turnContinues = false;
            } else {
                // Exécuter l'action de révélation
                Card revealedCard = executeAction(action, currentPlayer);

                if (revealedCard == null) {
                    // Si executeAction retourne null (ex: annulation ou erreur), on boucle
                    // sauf si c'est un bot qui plante (éviter boucle infinie)
                    if (currentPlayer instanceof Bot) {
                        turnSuccess = false;
                        turnContinues = false;
                    } else {
                        view.displayError("Action invalide!");
                    }
                    continue;
                }

                // Récupérer les infos de la carte révélée pour affichage
                List<RevealedCard> revealed = game.getRevealedCards();
                RevealedCard lastRevealed = revealed.get(revealed.size() - 1);
                Player cardOwner = lastRevealed.getOwner();
                int cardIndex = lastRevealed.getCardIndex();

                // Vérifier si la carte correspond à la série en cours
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
                    // Première carte révélée
                    view.displayCardRevealed(revealedCard, cardOwner, cardIndex, true, true, 0);
                }
            }
        }

        // Fin du tour
        if (game.getRevealedCards().size() == 3 && game.isValidTrio()) {
            game.awardTrioToTeam(currentTeam);
            view.displayTeamTrioSuccess(currentTeam, currentTeam.getTrioCount());

            // Important : Après un trio réussi, le joueur rejoue potentiellement (selon variantes),
            // mais surtout le compteur d'échange doit être remis à zéro pour le "nouveau" tour logique.
            // Si le jeu passe au joueur suivant, c'est fait au début de playTurn.
            // Si le joueur garde la main (variante experte), il faudrait reset ici.
            // Dans votre boucle principale startGame(), on fait game.nextTurn() systématiquement,
            // donc le reset se fera au prochain appel de playTurn().

        } else if (!turnSuccess || (!game.getRevealedCards().isEmpty() && game.getRevealedCards().size() < 3)) {
            // Échec ou arrêt volontaire
            view.displayTurnFailed();
            game.failTurn();

            // Réafficher l'état après échec (cartes retournées)
            refreshHumanView();
            view.displayVisibleCards(game.getAllPlayers(), game.getCenterDeck());
        }
    }

    /**
     * Choisit une action selon le type de joueur.
     * Pour un Bot, on injecte une petite probabilité d'échange aléatoire.
     */
    private int chooseAction(Player player) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            // Logique Bot simple pour l'échange :
            // Si pas encore échangé, pas de carte révélée, et proba 20% -> tenter échange
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

        switch (action) {
            case 1: // Ma carte MIN
                return game.revealLowestCardFromPlayer(currentPlayer);

            case 2: // Ma carte MAX
                return game.revealHighestCardFromPlayer(currentPlayer);

            case 3: // Carte MIN d'un autre joueur
                Player target3 = isBot
                        ? bot.chooseTargetPlayer(allPlayers)
                        : selectOtherPlayer(currentPlayer);
                if (target3 != null) {
                    if (isBot) {
                        view.displayBotAction(bot, "révèle MIN de", target3);
                    }
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
     * Effectue un échange de carte entre 2 joueurs d'une même équipe.
     * Gère à la fois les joueurs humains et les bots.
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
            if (!(currentPlayer instanceof Bot)) view.displayError("Aucun coéquipier disponible.");
            return false;
        }

        Player mate;
        int idxA, idxB;

        if (currentPlayer instanceof Bot) {
            // Bot: choix entièrement aléatoire
            Bot bot = (Bot) currentPlayer;
            Random rand = new Random();

            mate = mates.get(rand.nextInt(mates.size()));

            Deck deckA = currentPlayer.getDeck();
            Deck deckB = mate.getDeck();

            if (deckA.isEmpty() || deckB.isEmpty()) return false;

            idxA = rand.nextInt(deckA.getSize());
            idxB = rand.nextInt(deckB.getSize());

            view.displayBotAction(bot, "échange une carte avec", mate);

        } else {
            // Joueur humain: interface UI
            mate = view.promptSelectPlayer(mates);
            if (mate == null) return false; // Annulation

            // Sélectionner SA carte
            idxA = view.promptSelectHandCard(currentPlayer);
            if (idxA < 0) return false; // Annulation

            // Sélectionner la carte du PARTENAIRE
            // Note: promptSelectHandCard affiche les cartes masquées [?] pour les autres joueurs
            idxB = view.promptSelectHandCard(mate);
            if (idxB < 0) return false; // Annulation
        }

        // Appeler le modèle pour effectuer l'échange physique
        // On suppose que game.performExchange existe (ajouté dans le modèle précédent)
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

    /**
     * Trouve le joueur humain (User)
     */
    private Player findHumanPlayer() {
        for (Player p : game.getAllPlayers()) {
            if (p instanceof User) {
                return p;
            }
        }
        return null;
    }
}
