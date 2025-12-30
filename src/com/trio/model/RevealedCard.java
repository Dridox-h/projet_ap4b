package com.trio.model;

/**
 * Représente une carte révélée pendant un tour avec son propriétaire
 */
public class RevealedCard {

    // Attributs
    private Card card;
    private Player owner; // null si carte du centre
    private int cardIndex; // index de la carte si elle vient du centre, -1 sinon

    // Constructeurs
    public RevealedCard(Card card, Player owner) {
        this(card, owner, -1);
    }

    public RevealedCard(Card card, Player owner, int cardIndex) {
        this.card = card;
        this.owner = owner;
        this.cardIndex = cardIndex;
    }

    // Getters
    public Card getCard() {
        return card;
    }

    public Player getOwner() {
        return owner;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public int getValue() {
        return card.getValue();
    }
}
