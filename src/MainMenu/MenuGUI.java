package MainMenu;

import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JFrame {

    private int nbplayers = 0;
    private int currentBots = 0;
    private String currentMode = "Simple";
    private String currentType = "Individual";

    // UI Components (Must be here to be accessed by methods)
    private JSlider playerSlider;
    private JButton modeButton;
    private JButton typeButton;

    private void startGame() {
        // 1. Update the state variables to match what is currently on screen
        nbplayers = playerSlider.getValue();
        currentMode = modeButton.getText().replace("Mode: ", "");
        currentType = typeButton.getText().replace("Type: ", "");

        // 2. Launch the game
        Game.Game game = new Game.Game(nbplayers, currentBots, currentMode, currentType);
        game.setVisible(true);

        // Optional: Close the menu
        // this.dispose();
    }

    private void configureBots() {
        // Calculate bots based on the CURRENT slider value
        int totalPlayers = playerSlider.getValue();
        currentBots = totalPlayers - 1;

        System.out.println("Bots configured: " + currentBots);
        JOptionPane.showMessageDialog(this, "Bots set to: " + currentBots);
    }

    private void openOptions() {
        JOptionPane.showMessageDialog(this, "Opening options...");
    }

    private void exitGame() {
        System.exit(0);
    }

    public MenuGUI() {
        // Theme Colors
        Color backgroundColor = new Color(20, 30, 48); // Dark Blue Background
        Color panelColor = new Color(32, 45, 65); // Slightly Lighter Blue Panel
        Color buttonColor = new Color(52, 152, 219); // Vivid Blue Button
        Color textColor = Color.WHITE;
        Font mainFont = new Font("SansSerif", Font.BOLD, 16);
        Font titleFont = new Font("SansSerif", Font.BOLD, 20);

        // Basic window setup
        setTitle("Game Menu");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // --- Top Section: Player Slider ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        topPanel.setBackground(backgroundColor);

        JLabel sliderLabel = new JLabel("Number of Players");
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderLabel.setForeground(textColor);
        sliderLabel.setFont(titleFont);

        playerSlider = new JSlider(JSlider.HORIZONTAL, 3, 6, 3);
        playerSlider.setMajorTickSpacing(1);
        playerSlider.setPaintTicks(true);
        playerSlider.setPaintLabels(true);
        playerSlider.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        playerSlider.setBackground(backgroundColor);
        playerSlider.setForeground(textColor);
        playerSlider.setFont(mainFont);

        topPanel.add(sliderLabel);
        topPanel.add(playerSlider);
        nbplayers = playerSlider.getValue();

        // --- Settings Section: Mode and Type ---
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        settingsPanel.setBackground(backgroundColor);

        // Helper to style buttons
        class StyleHelper {
            void styleButton(JButton btn) {
                btn.setBackground(buttonColor);
                btn.setForeground(textColor);
                btn.setFont(mainFont);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
        StyleHelper styler = new StyleHelper();

        // Mode Button
        modeButton = new JButton("Mode: Simple");
        styler.styleButton(modeButton);
        JPopupMenu modePopup = new JPopupMenu();
        modePopup.add(new JMenuItem("Simple")).addActionListener(e -> modeButton.setText("Mode: Simple"));
        modePopup.add(new JMenuItem("Picante")).addActionListener(e -> modeButton.setText("Mode: Picante"));

        modeButton.addActionListener(e -> modePopup.show(modeButton, 0, modeButton.getHeight()));
        currentMode = modeButton.getText().replace("Mode: ", "");

        // Type Button
        typeButton = new JButton("Type: Individual");
        styler.styleButton(typeButton);
        JPopupMenu typePopup = new JPopupMenu();
        typePopup.add(new JMenuItem("Individual")).addActionListener(e -> typeButton.setText("Type: Individual"));
        typePopup.add(new JMenuItem("Team")).addActionListener(e -> typeButton.setText("Type: Team"));

        typeButton.addActionListener(e -> typePopup.show(typeButton, 0, typeButton.getHeight()));
        currentType = typeButton.getText().replace("Type: ", "");

        settingsPanel.add(modeButton);
        settingsPanel.add(typeButton);
        topPanel.add(settingsPanel);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Section: Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 50, 100));
        buttonPanel.setBackground(backgroundColor);

        // Create buttons
        JButton startButton = new JButton("Start Game");
        JButton botsButton = new JButton("Complete with bots");
        JButton optionsButton = new JButton("Options");
        JButton exitButton = new JButton("Exit");

        // Style buttons
        styler.styleButton(startButton);
        styler.styleButton(botsButton);
        styler.styleButton(optionsButton);
        styler.styleButton(exitButton);

        // Add functionality to buttons
        startButton.addActionListener(e -> startGame());
        botsButton.addActionListener(e -> configureBots());
        optionsButton.addActionListener(e -> openOptions());
        exitButton.addActionListener(e -> exitGame());

        // Add buttons to the panel
        buttonPanel.add(startButton);
        buttonPanel.add(botsButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(exitButton);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.CENTER);
    }
}
