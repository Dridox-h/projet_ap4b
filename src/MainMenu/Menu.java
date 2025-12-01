package MainMenu;

import java.util.Scanner;

public class Menu {
    // 1. We need a Scanner to read user input
    private Scanner scanner;

    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    // 2. A method to display the options
    public void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Start Game");
        System.out.println("2. Options");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    // 3. A method to get a valid choice from the user
    public int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("That's not a number! Please try again.");
            scanner.next(); // Consume the invalid input
            System.out.print("Enter your choice: ");
        }
        return scanner.nextInt();
    }

    // 4. The main loop that keeps the menu running
    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("Starting the game...");
                    // Game logic would go here
                    break;
                case 2:
                    System.out.println("Opening options...");
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-3.");
            }
        }
        scanner.close();
    }
}
