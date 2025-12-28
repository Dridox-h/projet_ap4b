package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> listPlayers;
    private List<Team> listTeams;
    private Deck CardCenter;
    private int currentPlayer;
    private boolean isTeam;

    // Pour la gestion du tour actuel (cartes révélées temporairement)
    private List<Card> cardVisibleRound;

    public Game() {
        this.listPlayers = new ArrayList<>();
        this.listTeams = new ArrayList<>();
        this.CardCenter = new Deck();
        this.cardVisibleRound = new ArrayList<>();
        this.currentPlayer = 0;
    }

    // --- Getters & Setters ---
    public List<Player> getListPlayers() { return listPlayers; }
    public Deck getCardCenter() { return CardCenter; }
    public void setTeam(boolean b) { this.isTeam = b; }
    public boolean isTeam() { return isTeam; }

    public Player getCurrentPlayer() {
        return listPlayers.get(currentPlayer);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % listPlayers.size();
    }

    public List<Card> getCardVisibleRound() { return cardVisibleRound; }

    public void addVisibleCard(Card c) {
        c.setVisible(true);
        cardVisibleRound.add(c);
    }

    public void restRound() {
        // Cache les cartes si pas gagnées
        for(Card c : cardVisibleRound) {
            c.setVisible(false);
        }
        cardVisibleRound.clear();
    }

    public void awardCardsToWinner(Player gagnant) {
        // Les cartes restent visibles et vont au gagnant
        gagnant.winTrio(new ArrayList<>(cardVisibleRound));
        cardVisibleRound.clear();
    }

    public List<Team> getListTeams() { return listTeams; }
    public void addEquipe(Team e) { listTeams.add(e); }
}
