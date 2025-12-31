import javax.swing.SwingUtilities;
import view.MenuGUI;

/**
 * Main entry point for the application
 * Launches the MenuGUI following MVC pattern
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            MenuGUI menu = new MenuGUI();
            menu.setVisible(true);
        });
    }
}