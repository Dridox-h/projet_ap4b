package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private List<Player> players;
    private int teamId;
    private String name;

    public Team(int teamId, String name) {
        this.teamId = teamId;
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setTeamId(this.teamId);
    }

    public List<Player> getPlayers() { 
        return players; 
    }

    public String getName() { 
        return name; 
    }

    public int getTeamId() { 
        return teamId; 
    }

    public int getScore() {
        int totalScore = 0;
        for (Player player : players) {
            totalScore += player.getScore();
        }
        return totalScore;
    }

    public int getTrioCount() {
        int count = 0;
        for (Player player : players) {
            count += player.getTriosWon().getSize() / 3;
        }
        return count;
    }

    public boolean hasTrioSeven() {
        for (Player player : players) {
            int sevenCount = 0;
            for (Card card : player.getTriosWon().getCards()) {
                if (card.getValue() == 7) {
                    sevenCount++;
                }
            }
            if (sevenCount >= 3) {
                return true;
            }
        }
        return false;
    }
}