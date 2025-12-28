package com.trio.model;

import java.util.List;

public class Player {
    protected final String pseudo;
    protected Deck deck;
    protected Deck triosWon;
    protected int teamId;
    protected Card tradeCard; // Carte marquée pour l'échange avec le coéquipier
    private int score;


    public Player(String pseudo) {
        this.pseudo = pseudo;
        this.deck = new Deck();
        this.triosWon = new Deck();
        this.teamId = -1;
        this.tradeCard = null; // Initialisé à null
    }

    public void addScore(int score){
        this.score += score;
    }
    public String getPseudo() { 
        return pseudo; 
    }

    public Deck getDeck() { 
        return deck; 
    }

    public Deck getTriosWon() { 
        return triosWon; 
    }

    public void winTrio(List<Card> trio) {
        for (Card card : trio) {
            triosWon.addCard(card);
        }
    }

    public int getTeamId() { 
        return teamId; 
    }

    public void setTeamId(int teamId) { 
        this.teamId = teamId; 
    }

    public int getScore() { 
        return triosWon.getSize(); 
    }

    public String chooseAction(Game game) {
        return "DEFAULT_ACTION";
    }

    /**
     * Marque une carte pour l'échange avec le coéquipier
     * Vérifie que la carte appartient bien au joueur avant de la marquer
     * @param card la carte à échanger
     * @throws IllegalArgumentException si la carte n'appartient pas au joueur
     */
    public void setTradeCard(Card card) {
        if (card == null) {
            this.tradeCard = null;
            return;
        }
        
        // Vérifier que la carte appartient bien au joueur
        if (!deck.getCards().contains(card)) {
            throw new IllegalArgumentException("La carte [" + card.getValue() + 
                    "] n'appartient pas au joueur " + pseudo);
        }
        
        this.tradeCard = card;
    }

    /**
     * Obtient la carte marquée pour l'échange
     * @return la carte à échanger ou null si aucune carte n'est marquée
     */
    public Card getTradeCard() {
        return tradeCard;
    }

    /**
     * Efface la carte marquée pour l'échange
     * Utilisé après l'exécution de l'échange ou pour annuler un échange
     */
    public void clearTradeCard() {
        this.tradeCard = null;
    }

    /**
     * Vérifie si le joueur a une carte marquée pour l'échange
     * @return true si une carte est marquée, false sinon
     */
    public boolean hasTradeCard() {
        return tradeCard != null;
    }

    /**
     * Obtient une représentation textuelle de la carte à échanger
     * @return description de la carte ou "Aucune" si pas de carte marquée
     */
    public String getTradeCardInfo() {
        if (tradeCard == null) {
            return "Aucune carte marquée";
        }
        return "Carte marquée : [" + tradeCard.getValue() + "]";
    }
}