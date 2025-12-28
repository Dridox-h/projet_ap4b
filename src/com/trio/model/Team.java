package com.trio.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private List<Player> listPlayer;
    private int idTeam;
    private String name;

    public Team(int id, String name) {
        this.idTeam = id;
        this.name = name;
        this.listPlayer = new ArrayList<>();
    }

    public void addPlayer(Player j) {
        listPlayer.add(j);
        j.setIdTeam(this.idTeam);
    }

    public List<Player> getListPlayer() { return listPlayer; }
    public String getName() { return name; }

    // Calcule le score total de l'équipe
    public int getNumberTrio() {
        int count = 0;
        for(Player j : listPlayer) {
            count += j.getTrioWins().getSize() / 3;
        }
        return count;
    }

    public boolean isTrioSeven() {
        for(Player j : listPlayer) {
            int septCount = 0;
            for(Card c : j.getTrioWins().getCartes()) {
                if(c.getvalue() == 7) septCount++;
            }
            if(septCount >= 3) return true;
        }
        return false;
    }
}
