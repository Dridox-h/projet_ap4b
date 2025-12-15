package Game;

import javax.swing.*;

public class Game extends JFrame {
    public Game(int numPlayers, int numBots, String type) {
        setTitle("Trio Game");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);

        String message = String.format(
                "<html><center>Game Started!<br>Players: %d (Bots: %d)<br>Type: %s</center></html>",
                numPlayers, numBots, type);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        add(label);
    }
}
