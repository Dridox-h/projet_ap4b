package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private List<Team> teams;
    private Deck centerCards;
    private int currentPlayerIndex;
    private boolean isTeamMode;
    private List<Card> visibleCards;

    public Game(List<Player> players, boolean isTeamMode) {
        this.players = players;
        this.teams = new ArrayList<>();
        this.centerCards = new Deck();
        this.visibleCards = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isTeamMode = isTeamMode;
    }

    public List<Player> getPlayers() { 
        return players; 
    }

    public Deck getCenterCards() { 
        return centerCards; 
    }

    public void setTeamMode(boolean isTeamMode) { 
        this.isTeamMode = isTeamMode; 
    }

    public boolean isTeamMode() { 
        return isTeamMode; 
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public List<Card> getVisibleCards() { 
        return visibleCards; 
    }

    public void addVisibleCard(Card card) {
        card.setVisible(true);
        visibleCards.add(card);
    }

    public void clearVisibleCards() {
        for (Card card : visibleCards) {
            card.setVisible(false);
        }
        visibleCards.clear();
    }

    public void awardCardsToWinner(Player winner) {
        winner.winTrio(new ArrayList<>(visibleCards));
        visibleCards.clear();
    }

    public List<Team> getTeams() { 
        return teams; 
    }

    public void addTeam(Team team) { 
        teams.add(team); 
    }


}