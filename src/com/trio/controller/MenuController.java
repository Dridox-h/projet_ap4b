package com.trio.controller;

import com.trio.model.*;

/**
 * Contrôleur du menu principal.
 * Gère la configuration de la partie avant de la lancer.
 */
public class MenuController {

    private Menu menu;
    private User currentUser;
    private int nbPlayers;
    private int gameMode;

    // Constructor for MenuGUI
    public MenuController(Menu menu) {
        this.menu = menu;
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public int getGameMode() {
        return gameMode;
    }

    public Menu getMenu() {
        return menu;
    }

    // === GUI SUPPORT METHODS ===

    /**
     * Attempts to start the game with current settings (for GUI)
     */
    public Menu.ValidationResult startGame() {
        Menu.ValidationResult validation = menu.validateGameStart();
        if (validation.isValid()) {
            System.out.println("Starting the game with " + menu.getNbPlayers() +
                    " players and " + menu.getNbBots() + " bots in " +
                    menu.getType() + " type");
            menu.logGameSession("logs/GameLogs.txt");
        }
        return validation;
    }

    /**
     * Configures bots to complete the game
     */
    public void configureBots(int nbPlayers) {
        menu.setNbPlayers(nbPlayers);
        menu.completewithBots(nbPlayers);
        System.out.println("Bots configured: " + menu.getNbBots());
    }

    /**
     * Creates a new user and saves to logs
     */
    public User createNewUser(String name, int age, String avatarPath) {
        User newUser = new User(name, age, 0, avatarPath);
        menu.setCurrentUser(newUser);
        menu.writeLogsUser("logs/UserLogs.txt", newUser);
        System.out.println("Current user set to: " + menu.getCurrentUser().getName());
        return newUser;
    }

    /**
     * Selects an existing user by ID
     */
    public User selectExistingUser(int id) {
        User selectedUser = menu.selectUser(id);
        if (selectedUser != null) {
            System.out.println("Current user set to: " + menu.getCurrentUser().getName());
        }
        return selectedUser;
    }

    /**
     * Checks if user logs are empty
     */
    public boolean isUserLogsEmpty() {
        return menu.isUserLogsEmpty("logs/UserLogs.txt");
    }

    /**
     * Exits the game
     */
    public void exitGame() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
