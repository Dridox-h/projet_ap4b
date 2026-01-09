package com.trio.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Deck {
    // Attributs
    private List<Card> cards;

    // Constructeurs
    public Deck() {
        this.cards = new ArrayList<>();
    }

    // Getters
    public Card getLowCard() {
        return this.cards.stream()
                .filter(card -> !card.isVisible())
                .min(Comparator.comparingInt(Card::getValue))
                .orElse(null);
    }

    public Card getHighCard() {
        return this.cards.stream()
                .filter(card -> !card.isVisible())
                .max(Comparator.comparingInt(Card::getValue))
                .orElse(null);
    }

    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getSize() {
        return cards.size();
    }

    // Setters

    // Méthodes Métiers

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public Card removeCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.remove(index);
        }
        return null;
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void sort() {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            if (card.isVisible()) {
                sb.append(card.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}