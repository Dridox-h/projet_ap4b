package com.trio.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

/**
 * Classe pour créer et initialiser la pioche à partir d'un fichier JSON
 */
public class DrawPile {

    // Attributs
    private Deck deck;

    // Constructeurs
    public DrawPile() {
        this.deck = new Deck();
    }

    // Getters
    public Deck getDeck() {
        return deck;
    }

    // Méthodes Métier

    /**
     * Charge les cartes depuis le fichier JSON
     */
    public void loadFromJson(String resourcePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new RuntimeException("Fichier JSON non trouvé: " + resourcePath);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            parseJson(json.toString());

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des cartes: " + e.getMessage());
            createDefaultCards();
        }
    }

    /**
     * Parse le JSON et crée les cartes avec value, coordinate, imagePath
     */
    private void parseJson(String json) {
        String cardsArray = json.substring(json.indexOf("[") + 1, json.lastIndexOf("]"));
        String[] cardObjects = cardsArray.split("\\},");

        for (String cardObj : cardObjects) {
            cardObj = cardObj.replace("{", "").replace("}", "").trim();

            int value = 0;
            String coordinate = "";
            String imagePath = "";

            String[] properties = cardObj.split(",");
            for (String prop : properties) {
                String[] keyValue = prop.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String val = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "value":
                            value = Integer.parseInt(val);
                            break;
                        case "coordinate":
                            coordinate = val;
                            break;
                        case "imagePath":
                            imagePath = val;
                            break;
                    }
                }
            }

            if (value > 0) {
                Card card = new Card(value, coordinate, imagePath);
                deck.addCard(card);
            }
        }
    }

    /**
     * Crée les 36 cartes par défaut avec coordonnées génériques
     */
    public void createDefaultCards() {
        deck = new Deck();
        String[] coordinates = { "Entreprise", "email@example.com", "Nom Prénom" };

        for (int value = 1; value <= 12; value++) {
            for (int copy = 0; copy < 3; copy++) {
                String coord = coordinates[copy];
                String imagePath = "card_" + value + "_" + (char) ('a' + copy) + ".png";
                deck.addCard(new Card(value, coord, imagePath));
            }
        }
    }

    /**
     * Mélange la pioche
     */
    public void shuffle() {
        deck.shuffle();
    }

    /**
     * Distribue les cartes aux joueurs ET au centre selon les règles
     * 
     * @param players    liste des joueurs
     * @param centerDeck le deck central
     */
    public void distributeToPlayers(java.util.List<Player> players, Deck centerDeck) {
        shuffle();

        int nbPlayers = players.size();
        int cardsPerPlayer;
        int cardsAtCenter;

        // Règles de distribution selon le nombre de joueurs
        switch (nbPlayers) {
            case 3:
                cardsPerPlayer = 9;
                cardsAtCenter = 9;
                break;
            case 4:
                cardsPerPlayer = 7;
                cardsAtCenter = 8;
                break;
            case 5:
                cardsPerPlayer = 6;
                cardsAtCenter = 6;
                break;
            case 6:
            default:
                cardsPerPlayer = 5;
                cardsAtCenter = 6;
                break;
        }

        // Distribuer aux joueurs
        for (Player player : players) {
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!deck.isEmpty()) {
                    Card card = deck.removeCard(0);
                    player.getDeck().addCard(card);
                }
            }
            player.getDeck().sort();
        }

        // Distribuer au centre
        for (int i = 0; i < cardsAtCenter; i++) {
            if (!deck.isEmpty()) {
                Card card = deck.removeCard(0);
                centerDeck.addCard(card);
            }
        }

        System.out.println("📋 Distribution: " + cardsPerPlayer + " cartes/joueur, " + cardsAtCenter + " au centre");
    }
}
