package com.trio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une équipe de joueurs dans le mode équipe.
 * Par défaut, une équipe contient 2 joueurs.
 * Implémente TrioHolder pour gérer les trios gagnés par l'équipe.
 */
public class Team implements TrioHolder {

    // Attributs
    private String name;
    private List<Player> players;
    private List<Deck> trios;

    // Constructeurs
    public Team(String name, List<Player> players) {
        this.name = name;
        this.players = new ArrayList<>(players);
        this.trios = new ArrayList<>();
    }

    /**
     * Constructeur pratique pour créer une équipe de 2 joueurs
     */
    public Team(String name, Player player1, Player player2) {
        this.name = name;
        this.players = new ArrayList<>();
        this.players.add(player1);
        this.players.add(player2);
        this.trios = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        }
        return null;
    }

    public int getTeamSize() {
        return players.size();
    }

    public List<Deck> getTrios() {
        return trios;
    }

    // TrioHolder Implementation
    @Override
    public void addTrio(Deck trio) {
        trios.add(trio);
    }

    @Override
    public int getTrioCount() {
        return trios.size();
    }

    // Méthodes Métier

    /**
     * Vérifie si un joueur appartient à cette équipe
     */
    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * Retourne l'équipe sous forme de chaîne
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        for (int i = 0; i < players.size(); i++) {
            sb.append(players.get(i).getPseudo());
            if (i < players.size() - 1) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }
}
