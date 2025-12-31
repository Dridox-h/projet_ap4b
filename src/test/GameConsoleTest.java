package test;

import java.util.Scanner;
import model.Menu;
import model.User;

/**
 * GameConsoleTest - Console-based UI for testing the game menu
 * This is a TEST/DEMO class, not part of the MVC architecture
 * Contains the console-based MenuGame() functionality
 */
public class GameConsoleTest {

    private Menu menu;
    private Scanner scanner;

    public GameConsoleTest() {
        this.menu = new Menu();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main menu loop - Console-based UI for testing
     */
    public void MenuGame() {
        System.out.println("\n-----------------------------------------");
        System.out.println("Welcome to the game! Current settings:");
        System.out.println(menu.getSettingsString());
        System.out.println("-----------------------------------------");
        if (menu.getCurrentUser() != null) {
            System.out.println("User: " + menu.getCurrentUser().getName());
            System.out.println("Victories: " + menu.getCurrentUser().getNBVictoire());
        }
        System.out.println("-----------------------------------------");
        System.out.println("Please select an option:");
        System.out.println("1. Start Game");
        System.out.println("2. Number of players");
        System.out.println("3. Bots configuration");
        System.out.println("4. Type of game");
        System.out.println("5. User configuration");
        System.out.println("6. Exit Game");
        System.out.println("Menu navigation : ");

        int option = 0;

        // Input handling
        if (scanner.hasNextInt()) {
            option = scanner.nextInt();
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume invalid input
            option = 0; // Force loop continuation
        }

        switch (option) {
            case 1:
                // Validate and start game
                Menu.ValidationResult validation = menu.validateGameStart();
                if (!validation.isValid()) {
                    System.out.println(validation.getErrorMessage());
                } else {
                    System.out.println("Starting the game with " + menu.getNbPlayers() + " players and "
                            + menu.getNbBots() + " bots in " + menu.getType() + " type");
                    // Here you would actually start the game
                }
                break;

            case 2:
                int nbplayers;
                do {
                    System.out.println("Please enter the number of players (1-6):");
                    nbplayers = scanner.nextInt();
                    Menu.ValidationResult playerValidation = menu.validatePlayerCount(nbplayers);
                    if (!playerValidation.isValid()) {
                        System.out.println(playerValidation.getErrorMessage());
                    } else {
                        menu.setNbPlayers(nbplayers);
                        break;
                    }
                } while (true);
                break;

            case 3:
                System.out.println("Do you want to complete with bots? (y/n)");
                String answer = scanner.next();
                if (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Invalid input. Please enter y or n.");
                } else if (answer.equals("y")) {
                    menu.completewithBots(menu.getNbPlayers());
                    System.out.println("Number of bots: " + menu.getNbBots());
                }
                break;

            case 4:
                System.out.println("Please enter the type of game: Individual or Team");
                String currentType = scanner.next();
                Menu.ValidationResult typeValidation = menu.validateGameType(currentType);
                if (!typeValidation.isValid()) {
                    System.out.println(typeValidation.getErrorMessage());
                } else {
                    menu.setType(currentType);
                }
                break;

            case 5:
                System.out.println("Please select an option:");
                System.out.println("1. Create new user");
                System.out.println("2. Select existing user");
                int optionUser = scanner.nextInt();

                if (optionUser == 1) {
                    System.out.println("Please enter the user name:");
                    String name = scanner.next();
                    System.out.println("Please enter the user age:");
                    int age = scanner.nextInt();
                    int victories = 0;
                    String path_to_avatar = "";

                    User newUser = new User(name, age, victories, path_to_avatar);
                    menu.setCurrentUser(newUser);
                    menu.writeLogsUser("logs/UserLogs.txt", newUser);
                    System.out.println("User created: " + newUser.getName());

                } else if (optionUser == 2) {
                    if (menu.isUserLogsEmpty("logs/UserLogs.txt")) {
                        System.out.println("No users found. Please create one first.");
                    } else {
                        menu.displayLogsUser("logs/UserLogs.txt");
                        System.out.println("Please enter the user ID:");
                        int id = scanner.nextInt();
                        menu.selectUser(id);
                    }
                }
                break;

            case 6:
                System.out.println("Goodbye!");
                scanner.close();
                System.exit(0);
                break;

            default:
                System.out.println("Invalid option");
                break;
        }

        // Loop until start game or exit
        while (option != 1 && option != 6) {
            MenuGame();
        }
    }

    /**
     * Main method to run the console-based test
     */
    public static void main(String[] args) {
        GameConsoleTest test = new GameConsoleTest();
        test.MenuGame();
    }
}
