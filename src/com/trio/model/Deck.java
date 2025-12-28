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

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public Card removeCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.remove(index);
        }
        return null;
    }

    public List<Card> getCards() { 
        return cards; 
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void sort() {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }

    public int getSize() { 
        return cards.size(); 
    }

    public boolean isEmpty() { 
        return cards.isEmpty(); 
    }
}