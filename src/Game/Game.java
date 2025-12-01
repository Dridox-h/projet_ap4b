package Game;

import javax.swing.*;

public class Game extends JFrame {
    public Game(int numPlayers, int numBots, String mode, String type) {
        setTitle("Trio Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);

        String message = String.format(
                "<html><center>Game Started!<br>Players: %d (Bots: %d)<br>Mode: %s<br>Type: %s</center></html>",
                numPlayers, numBots, mode, type);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        add(label);
    }
}
