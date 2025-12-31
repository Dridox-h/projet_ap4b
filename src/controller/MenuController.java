package controller;

import model.Menu;
import model.User;

/**
 * MenuController - Handles interaction between Menu (Model) and MenuGUI (View)
 * Follows MVC pattern: This is the CONTROLLER layer
 */
public class MenuController {

    private Menu menu;

    public MenuController(Menu menu) {
        this.menu = menu;
    }

    /**
     * Attempts to start the game with current settings
     * Logs the game session if successful
     * 
     * @return ValidationResult indicating success or failure
     */
    public Menu.ValidationResult startGame() {
        Menu.ValidationResult validation = menu.validateGameStart();
        if (validation.isValid()) {
            System.out.println("Starting the game with " + menu.getNbPlayers() + " players and "
                    + menu.getNbBots() + " bots in " + menu.getType() + " type");

            // Log the game session
            menu.logGameSession("logs/GameLogs.txt");
        }
        return validation;
    }

    /**
     * Configures bots to complete the game
     * 
     * @param nbPlayers number of players
     */
    public void configureBots(int nbPlayers) {
        menu.setNbPlayers(nbPlayers);
        menu.completewithBots(nbPlayers);
        System.out.println("Bots configured: " + menu.getNbBots());
    }

    /**
     * Sets the number of players
     * 
     * @param nbPlayers number of players
     * @return ValidationResult
     */
    public Menu.ValidationResult setNbPlayers(int nbPlayers) {
        Menu.ValidationResult validation = menu.validatePlayerCount(nbPlayers);
        if (validation.isValid()) {
            menu.setNbPlayers(nbPlayers);
        }
        return validation;
    }

    /**
     * Sets the game type
     * 
     * @param type game type (Individual or Team)
     * @return ValidationResult
     */
    public Menu.ValidationResult setType(String type) {
        Menu.ValidationResult validation = menu.validateGameType(type);
        if (validation.isValid()) {
            menu.setType(type);
        }
        return validation;
    }

    /**
     * Creates a new user and saves to logs
     * 
     * @param name       user name
     * @param age        user age
     * @param avatarPath path to avatar (can be empty)
     * @return created User object
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
     * 
     * @param id user ID
     * @return selected User object or null if not found
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
     * 
     * @return true if empty, false otherwise
     */
    public boolean isUserLogsEmpty() {
        return menu.isUserLogsEmpty("logs/UserLogs.txt");
    }

    /**
     * Gets the menu model
     * 
     * @return Menu object
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Exits the game
     */
    public void exitGame() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
