package com.trio.controller;

import com.trio.model.*;
import com.trio.view.*;

import java.util.List;
import java.util.Random;

public class GameController {
    private Game game;
    private GameGUI gameView;
    private MenuView menuView;
    private Random random;

    public GameController() {
        this.game = new Game();
        this.menuView = new MenuView();
        this.random = new Random();
        // On n'instancie plus GameGUI ici !
    }

    public void start() {
        // 1. Affiche le menu et attend la validation
        menuView.afficherMenuPrincipal();

        // 2. Récupère les deux infos du menu
        int nbTotal = menuView.demanderNombreJoueurs();
        int nbHumains = menuView.demanderNombreHumains();
        int nbBots = nbTotal - nbHumains;

        // 3. Crée l'interface de jeu avec les bons arguments
        this.gameView = new GameGUI(nbTotal, nbBots);
        this.gameView.setVisible(true);

        // 4. Configure la logique métier
        configurerPartie(nbTotal, nbHumains);
        jouerPartie();
    }

    private void configurerPartie(int nbTotal, int nbHumains) {
        game.getListPlayers().clear();

        // Création des Humains
        for (int i = 0; i < nbHumains; i++) {
            game.getListPlayers().add(new User("Humain " + (i + 1)));
        }
        // Création des Bots
        for (int i = nbHumains; i < nbTotal; i++) {
            game.getListPlayers().add(new Bot("Bot " + (i + 1)));
            gameView.ajouterLog("Le joueur " + (i + 1) + " est une IA.");
        }

       // Mode équipe automatique selon les règles (4 ou 6 joueurs) [cite: 2, 3]
        if (nbTotal == 4 || nbTotal == 6) {
            int rep = gameView.demanderEntier("Jouer en mode ÉQUIPE ? 1:Oui, 2:Non", 1, 2);
            if (rep == 1) {
                game.setTeam(true);
                creerEquipes(nbTotal);
            }
        }

        distribuerCartes(nbTotal);
    }

    private void creerEquipes(int nbJoueurs) {
        int nbEquipes = nbJoueurs / 2;
        for (int i = 0; i < nbEquipes; i++) {
            Team eq = new Team(i, "Team " + (i + 1));
            eq.addPlayer(game.getListPlayers().get(i));
            eq.addPlayer(game.getListPlayers().get(i + nbEquipes)); // Partenaire à l'opposé
            game.addEquipe(eq);
        }
    }

    private void distribuerCartes(int nbJoueurs) {
        Deck deckComplet = new Deck();
        for (int i = 1; i <= 12; i++) {
            for (int k = 0; k < 3; k++) deckComplet.addCard(new Card(i));
        }
        deckComplet.shuffle();

        // Règle de distribution (selon PDF p.4)
        int cartesParJoueur = switch (nbJoueurs) {
            case 3 -> 9;
            case 4 -> 7;
            case 5 -> 6;
            case 6 -> 5;
            default -> 0;
        };

        for (Player j : game.getListPlayers()) {
            for (int k = 0; k < cartesParJoueur; k++) {
                j.receiveCard(deckComplet.removeCard(0));
            }
        }

        // Le reste va au centre
        while (!deckComplet.isEmpty()) {
            game.getCardCenter().addCard(deckComplet.removeCard(0));
        }
    }

    /**
     * Boucle de jeu principale
     */
    private void jouerPartie() {
        boolean fini = false;
        while (!fini) {
            gameView.afficherPlateau(game);
            jouerTour();

            if (verifierVictoire()) {
                gameView.afficherPlateau(game);
                gameView.afficherMessage("★ FÉLICITATIONS ! " + game.getCurrentPlayer().getPseudo().toUpperCase() + " REMPORTE LA PARTIE ! ★");
                fini = true;
            } else {
                game.nextPlayer();
            }
        }
    }

    /**
     * Logique d'un tour de jeu (Humain ou Bot)
     */
    private void jouerTour() {
        Player joueurActuel = game.getCurrentPlayer();
        boolean estBot = (joueurActuel instanceof Bot);

        if (!estBot) {
            gameView.afficherMainJoueurActif(joueurActuel);
        } else {
            gameView.afficherMessage("\n[IA] " + joueurActuel.getPseudo() + " réfléchit...");
            attendre(1000);
        }

        boolean tourFini = false;
        while (!tourFini && game.getCardVisibleRound().size() < 3) {

            // 1. Choix de la source
            int source;
            if (!estBot) {
                source = gameView.demanderEntier(joueurActuel.getPseudo() + ", révéler depuis : 1.Centre, 2.Player", 1, 2);
            } else {
                source = ((Bot) joueurActuel).chooseOrigin();
            }

            Card cardRevelee = null;

            // 2. Sélection de la carte
            if (source == 1) { // CENTRE
                if (game.getCardCenter().isEmpty()) {
                    gameView.afficherMessage("Plus de cartes au centre !");
                    continue;
                }
                int idx = !estBot ? gameView.demanderEntier("Index de la carte ?", 0, game.getCardCenter().getSize() - 1)
                        : random.nextInt(game.getCardCenter().getSize());
                cardRevelee = game.getCardCenter().getCartes().get(idx);
            } else { // JOUEUR
                int idCible = !estBot ? gameView.demanderEntier("Id du Player cible ?", 0, game.getListPlayers().size() - 1)
                        : ((Bot) joueurActuel).chooseTarget(game.getListPlayers().size());

                Player cible = game.getListPlayers().get(idCible);
                if (cible.getMain().isEmpty()) {
                    if(!estBot) gameView.afficherMessage("Ce joueur n'a plus de cartes !");
                    continue;
                }

                int type = !estBot ? gameView.demanderEntier("1.Plus PETIT, 2.Plus GRAND de " + cible.getPseudo(), 1, 2)
                        : ((Bot) joueurActuel).chooseCardType();

                cardRevelee = (type == 1) ? cible.getMain().getLowestCard() : cible.getMain().getMaxCard();
            }

            // 3. Révélation et Vérification
            if (cardRevelee != null && !cardRevelee.isVisible()) {
                game.addVisibleCard(cardRevelee);
                gameView.afficherMessage("-> " + joueurActuel.getPseudo() + " a révélé un " + cardRevelee.getValeur());

                List<Card> rev = game.getCardVisibleRound();
                if (rev.size() > 1) {
                    if (rev.get(rev.size() - 1).getValeur() != rev.get(rev.size() - 2).getValeur()) {
                        gameView.afficherMessage("Dommage... Les numéros sont différents !");
                        attendre(1500);
                        game.restRound();
                        tourFini = true;
                    }
                }

                if (rev.size() == 3 && !tourFini) {
                    gameView.afficherMessage("!!! TRIO DE " + rev.get(0).getValeur() + " COMPLÉTÉ !!!");
                    attendre(1000);
                    retirerCartesDuJeu(rev);
                    game.awardCardsToWinner(joueurActuel);
                    tourFini = true;
                }
            } else {
                if(!estBot) gameView.afficherMessage("Action impossible (carte déjà visible).");
            }
        }
    }

    private void retirerCartesDuJeu(List<Card> cards) {
        for (Card c : cards) {
            game.getCardCenter().removeCard(c);
            for (Player j : game.getListPlayers()) j.getMain().removeCard(c);
        }
    }

    private boolean verifierVictoire() {
        Player j = game.getCurrentPlayer();
        if (game.isTeam()) {
            Team e = trouverEquipe(j);
            return (e != null && (e.getNumberTrio() >= 3 || e.isTrioSeven()));
        } else {
            int nbTrios = j.getTrioWins().getSize() / 3;
            boolean aTrioSept = j.getTrioWins().getCartes().stream().filter(c -> c.getValeur() == 7).count() == 3;
            return nbTrios >= 3 || aTrioSept;
        }
    }

    private Team trouverEquipe(Player j) {
        for (Team e : game.getListTeams()) {
            if (e.getListPlayer().contains(j)) return e;
        }
        return null;
    }

    private void attendre(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}