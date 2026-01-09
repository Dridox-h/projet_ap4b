package com.trio.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        try {
            // Essayer plusieurs chemins possibles (resources)
            InputStream is = getClass().getResourceAsStream("/resources/cards.json");

            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream("resources/cards.json");
            }
            if (is == null) {
                is = getClass().getResourceAsStream("/cards.json");
            }
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream("cards.json");
            }

            // Essayer depuis le système de fichiers
            if (is == null) {
                java.io.File file = new java.io.File("src/resources/cards.json");
                if (file.exists()) {
                    is = new java.io.FileInputStream(file);
                    System.out.println("✓ cards.json chargé depuis: " + file.getAbsolutePath());
                }
            }

            if (is == null) {
                System.out.println("Info: cards.json non trouvé, génération des cartes par défaut...");
                generateDefaultCards();
                return;
            }

            // Astuce "One-liner" pour lire tout le stream dans une String (Scanner avec
            // délimiteur \A)
            Scanner scanner = new Scanner(is, "UTF-8");
            String jsonContent = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();
            is.close();

            // Parser manuellement avec Regex
            String regex = "\"value\"\\s*:\\s*(\\d+).*?\"coordinate\"\\s*:\\s*\"(.*?)\".*?\"imagePath\"\\s*:\\s*\"(.*?)\"";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(jsonContent);

            while (matcher.find()) {
                int value = Integer.parseInt(matcher.group(1));
                String coord = matcher.group(2);
                String imagePath = matcher.group(3);
                deck.addCard(new Card(value, coord, imagePath));
            }

            // Vérifier si des cartes ont été chargées
            if (deck.isEmpty()) {
                System.out.println("Info: Aucune carte trouvée dans le JSON, génération par défaut...");
                generateDefaultCards();
            } else {
                System.out.println("✓ " + deck.getSize() + " cartes chargées depuis cards.json");
            }

        } catch (Exception e) {
            System.out.println("Info: Erreur lors du chargement: " + e.getMessage());
            generateDefaultCards();
        }
    }

    /**
     * Génère les 36 cartes par défaut en mémoire (fallback)
     * Valeurs de 1 à 12, 3 cartes par valeur
     */
    private void generateDefaultCards() {
        deck = new Deck();
        for (int value = 1; value <= 12; value++) {
            for (int copy = 0; copy < 3; copy++) {
                String coord = value + "-" + (char) ('A' + copy);
                String imagePath = "cards/" + value + ".png";
                deck.addCard(new Card(value, coord, imagePath));
            }
        }
        System.out.println("✓ " + deck.getSize() + " cartes générées par défaut");
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
            case 2:
                cardsPerPlayer = 9;
                cardsAtCenter = 18;
                break;
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
