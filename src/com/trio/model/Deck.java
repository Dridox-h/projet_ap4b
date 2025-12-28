package com.trio.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card c) {
        this.cards.add(c);
    }

    public Card removeCard(Card c) {
        if (cards.remove(c)) return c;
        return null;
    }

    public Card removeCard(int index) {
        if (index >= 0 && index < cards.size()) return cards.remove(index);
        return null;
    }

    public List<Card> getCartes() { return cards; }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void sort() {
        cards.sort(Comparator.comparingInt(Card::getvalue));
    }

    public int getSize() { return cards.size(); }

    public boolean isEmpty() { return cards.isEmpty(); }

    // Helpers pour le gameplay Trio
    public Card getLowestCard() {
        return cards.isEmpty() ? null : cards.get(0);
    }

    public Card getMaxCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }
}