package com.trio.model;

import java.util.List;
import java.util.Random;

public class Bot extends Player {
    private Random random = new Random();

    public Bot(String pseudo) {
        super(pseudo);
    }

    @Override
    public String chooseAction(Game game) {
        return "BOT_ACTION";
    }

    /**
     * IA intelligente pour choisir l'action du Bot
     * 
     * @param revealedThisTurn Cartes déjà révélées ce tour
     * @param players          Liste de tous les joueurs
     * @param centerDeck       Deck du centre
     * @return int représentant l'action (1-5)
     */
    public int chooseBotAction(List<RevealedCard> revealedThisTurn, List<Player> players, Deck centerDeck) {
        // Si aucune carte révélée, commencer par sa propre carte MIN ou MAX
        if (revealedThisTurn.isEmpty()) {
            return random.nextBoolean() ? 1 : 2; // MIN ou MAX aléatoire
        }

        // Valeur cible à trouver
        int targetValue = revealedThisTurn.get(0).getValue();

        // 1. Vérifier sa propre main
        Card myLow = this.getDeck().getLowCard();
        Card myHigh = this.getDeck().getHighCard();

        if (myLow != null && myLow.getValue() == targetValue) {
            return 1; // Révéler ma carte MIN
        }
        if (myHigh != null && myHigh.getValue() == targetValue) {
            return 2; // Révéler ma carte MAX
        }

        // 2. Analyser les cartes visibles des autres joueurs
        for (Player p : players) {
            if (!p.equals(this)) {
                // Chercher si ce joueur a une carte visible avec la bonne valeur
                for (Card c : p.getDeck().getCards()) {
                    if (c.isVisible() && c.getValue() == targetValue) {
                        // Il pourrait avoir d'autres cartes identiques
                        Card pLow = p.getDeck().getLowCard();
                        Card pHigh = p.getDeck().getHighCard();

                        if (pLow != null && !pLow.isVisible()) {
                            return 3; // Tenter la MIN d'un autre
                        }
                        if (pHigh != null && !pHigh.isVisible()) {
                            return 4; // Tenter la MAX d'un autre
                        }
                    }
                }
            }
        }

        // 3. Vérifier le centre
        for (Card c : centerDeck.getCards()) {
            if (c.isVisible() && c.getValue() == targetValue) {
                return 5; // Tenter une autre carte du centre
            }
        }

        // 4. Fallback: action aléatoire avec préférence pour le centre
        double rand = random.nextDouble();
        if (rand < 0.3)
            return 3; // MIN autre joueur
        else if (rand < 0.6)
            return 4; // MAX autre joueur
        else
            return 5; // Centre
    }

    /**
     * Choisir un joueur cible parmi les autres
     */
    public Player chooseTargetPlayer(List<Player> players) {
        List<Player> others = players.stream()
                .filter(p -> !p.equals(this) && !p.getDeck().isEmpty())
                .toList();

        if (others.isEmpty())
            return null;
        return others.get(random.nextInt(others.size()));
    }

    /**
     * Choisir un index de carte au centre
     */
    public int chooseCenterCardIndex(Deck centerDeck) {
        if (centerDeck.isEmpty())
            return -1;
        return random.nextInt(centerDeck.getSize());
    }
}