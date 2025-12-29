package com.trio.model;

/**
 * Représente une carte révélée pendant un tour avec son propriétaire
 */
public class RevealedCard {

    // Attributs
    private Card card;
    private Player owner; // null si carte du centre

    // Constructeurs
    public RevealedCard(Card card, Player owner) {
        this.card = card;
        this.owner = owner;
    }

    // Getters
    public Card getCard() {
        return card;
    }

    public Player getOwner() {
        return owner;
    }

    public int getValue() {
        return card.getValue();
    }
}
