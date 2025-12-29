package com.trio.model;

public interface Game {

    void startGame();

    void playTurn();

    boolean isFinished();

    TrioHolder getWinner();
}
