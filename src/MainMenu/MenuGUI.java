package MainMenu;

import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JFrame {

    public MenuGUI() {
        // Basic window setup
        setTitle("Game Menu");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen
        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        // --- Top Section: Player Slider ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel sliderLabel = new JLabel("Number of Players");
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider playerSlider = new JSlider(JSlider.HORIZONTAL, 3, 6, 3);
        playerSlider.setMajorTickSpacing(1);
        playerSlider.setPaintTicks(true);
        playerSlider.setPaintLabels(true);
        playerSlider.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        topPanel.add(sliderLabel);
        topPanel.add(playerSlider);

        // --- Settings Section: Mode and Type ---
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Mode Button (Simple / Picante)
        JButton modeButton = new JButton("Mode: Simple");
        JPopupMenu modePopup = new JPopupMenu();
        modePopup.add(new JMenuItem("Simple")).addActionListener(e -> modeButton.setText("Mode: Simple"));
        modePopup.add(new JMenuItem("Picante")).addActionListener(e -> modeButton.setText("Mode: Picante"));

        modeButton.addActionListener(e -> modePopup.show(modeButton, 0, modeButton.getHeight()));

        // Type Button (Individual / Team)
        JButton typeButton = new JButton("Type: Individual");
        JPopupMenu typePopup = new JPopupMenu();
        typePopup.add(new JMenuItem("Individual")).addActionListener(e -> typeButton.setText("Type: Individual"));
        typePopup.add(new JMenuItem("Team")).addActionListener(e -> typeButton.setText("Type: Team"));

        typeButton.addActionListener(e -> typePopup.show(typeButton, 0, typeButton.getHeight()));

        settingsPanel.add(modeButton);
        settingsPanel.add(typeButton);
        topPanel.add(settingsPanel);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Section: Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows now
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // Create buttons
        JButton startButton = new JButton("Start Game");
        JButton botsButton = new JButton("Complete with bots");
        JButton optionsButton = new JButton("Options");
        JButton exitButton = new JButton("Exit");

        // Add functionality to buttons
        startButton.addActionListener(e -> {
            int players = playerSlider.getValue();
            String mode = modeButton.getText().replace("Mode: ", "");
            String type = typeButton.getText().replace("Type: ", "");

            // Start Game launches with 0 bots (all humans).
            Game.Game game = new Game.Game(players, 0, mode, type);
            game.setVisible(true);
        });

        botsButton.addActionListener(e -> {
            int totalPlayers = playerSlider.getValue();
            int bots = totalPlayers - 1;
            String mode = modeButton.getText().replace("Mode: ", "");
            String type = typeButton.getText().replace("Type: ", "");

            Game.Game game = new Game.Game(totalPlayers, bots, mode, type);
            game.setVisible(true);
        });

        optionsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Opening options...");
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        // Add buttons to the panel
        buttonPanel.add(startButton);
        buttonPanel.add(botsButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(exitButton);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.CENTER);
    }
}
