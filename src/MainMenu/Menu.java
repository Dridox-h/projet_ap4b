package MainMenu;

import java.util.Scanner;
import model.User;

public class Menu {

    private User currentUser;
    private int nbplayers = 0;
    private int currentBots = 0;
    private String currentType = "Individual";

    private void exitGame() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private void startGame(int nbplayers, int currentBots, String currentType) {
        System.out.println("Starting the game with " + nbplayers + " players and " + currentBots + " bots" + " in "
                + currentType + " type");
    }

    public int getNbPlayers() {
        return nbplayers;
    }

    public int getNbBots() {
        return currentBots;
    }

    public String getType() {
        return currentType;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setNbPlayers(int nbplayers) {
        this.nbplayers = nbplayers;
    }

    public void setNbBots(int currentBots) {
        this.currentBots = currentBots;
    }

    public void completewithBots(int nbplayers) {
        this.currentBots = nbplayers - 1;
    }

    public void setType(String currentType) {
        this.currentType = currentType;
    }

    public void MenuGame() {
        System.out.println("\n-----------------------------------------");
        System.out.println("Welcome to the game! Current settings:");
        System.out.println("Players: " + nbplayers + " | Bots: " + currentBots + " | Type: "
                + currentType);
        System.out.println("-----------------------------------------");
        if (currentUser != null) {
            System.out.println("User: " + currentUser.getName());
            System.out.println("Victories: " + currentUser.getNBVictoire());
        }
        System.out.println("-----------------------------------------");
        System.out.println("Please select an option:");
        System.out.println("1. Start Game");
        System.out.println("2. Number of players");
        System.out.println("3. Bots configuration");
        System.out.println("4. Type of game");
        System.out.println("5. Game mode");
        System.out.println("6. User configuration");
        System.out.println("7. Exit Game");
        System.out.println("Menu navigation : ");
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        // Saisie de l'option
        if (scanner.hasNextInt()) {
            option = scanner.nextInt();
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consomme l'entrée invalide
            option = 0; // Force la continuité de la boucle
        }

        switch (option) {
            case 1:
                if (nbplayers == 0 || currentBots == 0) {
                    System.out.println("Please select a number of players and bots");
                }
                if (currentType == null) {
                    System.out.println("Please select a type");
                }
                if (nbplayers > 6) {
                    System.out.println("Please select a number of players equal or less than 6");
                }
                startGame(nbplayers, currentBots, currentType);
                break;
            case 2:
                do {
                    System.out.println("Please enter the number of players (1-6):");
                    nbplayers = scanner.nextInt();
                    if (nbplayers < 1 || nbplayers > 6) {
                        System.out.println("Invalid number! Please select a number of players between 1 and 6");
                    }
                } while (nbplayers < 1 || nbplayers > 6);
                setNbPlayers(nbplayers);
                break;
            case 3:
                System.out.println("Do you want to complete with bots? (y/n)");
                String answer = scanner.next();
                if (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Invalid input. Please enter y or n.");
                }
                if (answer.equals("y")) {
                    completewithBots(nbplayers);
                    System.out.println("Number of bots: " + currentBots);
                }
                break;
            case 4:
                System.out.println("Please enter the type of game: Individual or Team");
                currentType = scanner.next();
                if (!currentType.equals("Individual") && !currentType.equals("Team")) {
                    System.out.println("Invalid input. Please enter Individual or Team.");
                }
                setType(currentType);
                break;

            case 6:
                System.out.println("Please enter the user name:");
                String name = scanner.next();
                System.out.println("Please enter the user age:");
                int age = scanner.nextInt();
                int victories = 0;
                String path_to_avatar = "";
                setCurrentUser(new User(name, age, victories, path_to_avatar));
                break;
            case 7:
                exitGame();
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
        while (option != 1 && option != 7) {
            MenuGame();
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.MenuGame();
    }
}
