package com.trio.model;

/**
 * Modèle de données pour la configuration du menu.
 * Stocke les préférences de la partie avant son lancement.
 */
public class Menu {

    // Attributs de configuration
    private User currentUser;
    private int nbPlayers;
    private int nbBots;
    private String type; // "Individual" ou "Team"

    // Constructeur
    public Menu() {
        this.nbPlayers = 2;
        this.nbBots = 0;
        this.type = "Individual";
    }

    // === Getters & Setters ===

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public int getNbBots() {
        return nbBots;
    }

    public void setNbBots(int nbBots) {
        this.nbBots = nbBots;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // === Helpers ===

    public boolean isTeamMode() {
        return "Team".equals(type);
    }

    /**
     * Classe interne pour le résultat de validation
     */
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
}
