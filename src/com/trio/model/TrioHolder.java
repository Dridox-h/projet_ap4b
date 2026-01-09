package com.trio.model;

import java.util.List;

public interface TrioHolder {

    void addTrio(Deck trio);

    List<Deck> getTrios();

    int getTrioCount();

    default boolean hasSevenTrio() {
        for (Deck trio : getTrios()) {
            if (!trio.isEmpty() && trio.getCard(0).getValue() == 7) {
                return true;
            }
        }
        return false;
    }
}
