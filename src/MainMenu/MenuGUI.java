package MainMenu;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.User;

public class MenuGUI extends JFrame {

    private User currentUser;
    private int nbplayers = 0;
    private int currentBots = 0;
    private String currentMode = "Simple";
    private String currentType = "Individual";

    // UI Components
    private JSlider playerSlider;
    private JButton modeButton;
    private JButton typeButton;

    // Chess.com Blue Theme Colors
    private static final Color BACKGROUND_DARK = new Color(18, 27, 40);
    private static final Color BACKGROUND_MEDIUM = new Color(28, 40, 58);
    private static final Color ACCENT_BLUE = new Color(129, 182, 232);
    private static final Color ACCENT_BLUE_HOVER = new Color(149, 202, 252);
    private static final Color BUTTON_PRIMARY = new Color(108, 162, 215);
    private static final Color BUTTON_PRIMARY_HOVER = new Color(129, 182, 232);
    private static final Color BUTTON_SECONDARY = new Color(44, 62, 84);
    private static final Color BUTTON_SECONDARY_HOVER = new Color(54, 72, 94);
    private static final Color TEXT_PRIMARY = new Color(235, 240, 245);
    private static final Color TEXT_SECONDARY = new Color(162, 177, 195);
    private static final Color BORDER_COLOR = new Color(62, 82, 105);

    private void startGame() {
        nbplayers = playerSlider.getValue();
        currentMode = modeButton.getText().replace("Mode: ", "");
        currentType = typeButton.getText().replace("Type: ", "");
        // currentBots = configureBots();
        Game.Game game = new Game.Game(nbplayers, currentBots, currentMode, currentType);
        game.setVisible(true);
    }

    private int configureBots() {
        int totalPlayers = playerSlider.getValue();
        currentBots = totalPlayers - 1;

        System.out.println("Bots configured: " + currentBots);

        JOptionPane pane = new JOptionPane(
                "Bots set to: " + currentBots,
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Bot Configuration");
        dialog.getContentPane().setBackground(BACKGROUND_MEDIUM);
        dialog.setVisible(true);
        return currentBots;
    }

    /**
     * Creates a new User by showing a custom form dialog
     * Uses User setter methods to configure the user properties
     * 
     * @return User object with the configured properties, or null if cancelled
     */
    private User createNewUser() {
        // Create the dialog
        JDialog formDialog = new JDialog(this, "Create New User", true);
        formDialog.setSize(500, 400);
        formDialog.setLocationRelativeTo(this);
        formDialog.getContentPane().setBackground(BACKGROUND_DARK);
        formDialog.setLayout(new BorderLayout());

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_MEDIUM);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Name field
        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBackground(BACKGROUND_DARK);
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setCaretColor(TEXT_PRIMARY);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(nameField, gbc);

        // Age field
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ageLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(ageLabel, gbc);

        JTextField ageField = new JTextField(20);
        ageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ageField.setBackground(BACKGROUND_DARK);
        ageField.setForeground(TEXT_PRIMARY);
        ageField.setCaretColor(TEXT_PRIMARY);
        ageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(ageField, gbc);

        // Avatar path field
        JLabel avatarLabel = new JLabel("Avatar Path:");
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatarLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(avatarLabel, gbc);

        JTextField avatarField = new JTextField(20);
        avatarField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        avatarField.setBackground(BACKGROUND_DARK);
        avatarField.setForeground(TEXT_PRIMARY);
        avatarField.setCaretColor(TEXT_PRIMARY);
        avatarField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(avatarField, gbc);

        // Optional label
        JLabel optionalLabel = new JLabel("(Optional)");
        optionalLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        optionalLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(optionalLabel, gbc);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_DARK);

        // Container to hold the result
        final User[] resultUser = new User[1];

        ChessButton createButton = new ChessButton("Create User", BUTTON_PRIMARY, BUTTON_PRIMARY_HOVER);
        createButton.setPreferredSize(new Dimension(150, 45));
        createButton.addActionListener(e -> {
            // Validate name
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please enter a player name.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate age
            String ageStr = ageField.getText().trim();
            if (ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please enter an age.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0 || age > 150) {
                    JOptionPane.showMessageDialog(formDialog,
                            "Age must be between 0 and 150.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please enter a valid number for age.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get avatar path (optional)
            String avatarPath = avatarField.getText().trim();

            // Create user
            User newUser = new User("", 0, 0, "");
            newUser.setName(name);
            newUser.setAge(age);
            newUser.setNBVictoire(0);
            newUser.setPathAvatar(avatarPath);

            resultUser[0] = newUser;

            // Show success message
            JOptionPane.showMessageDialog(formDialog,
                    String.format("User created successfully!\nName: %s\nAge: %d",
                            newUser.getName(), newUser.getAge()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            formDialog.dispose();
        });

        ChessButton cancelButton = new ChessButton("Cancel", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> formDialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        formDialog.add(mainPanel, BorderLayout.CENTER);
        formDialog.setVisible(true);

        return resultUser[0];
    }

    private void openOptions() {
        JOptionPane.showMessageDialog(this, "Opening options...");
    }

    private void exitGame() {
        System.exit(0);
    }

    public int getNbPlayers() {
        return nbplayers;
    }

    public int getBots() {
        return currentBots;
    }

    public String getMode() {
        return currentMode;
    }

    public String gettype() {
        return currentType;
    }

    // Custom rounded button with Chess.com styling
    private static class ChessButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public ChessButton(String text, Color baseColor, Color hoverColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentColor = isHovered ? hoverColor : baseColor;
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public MenuGUI() {
        // Window setup
        setTitle("Game Menu");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_DARK);

        // Main container with padding
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(BACKGROUND_DARK);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // --- Title ---
        JLabel titleLabel = new JLabel("Game Menu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContainer.add(titleLabel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- Player Slider Card ---
        JPanel sliderCard = createCard();
        sliderCard.setLayout(new BoxLayout(sliderCard, BoxLayout.Y_AXIS));

        JLabel sliderLabel = new JLabel("Number of Players");
        sliderLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sliderLabel.setForeground(TEXT_PRIMARY);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerSlider = new JSlider(JSlider.HORIZONTAL, 3, 6, 3);
        playerSlider.setMajorTickSpacing(1);
        playerSlider.setPaintTicks(true);
        playerSlider.setPaintLabels(true);
        playerSlider.setBackground(BACKGROUND_MEDIUM);
        playerSlider.setForeground(ACCENT_BLUE);
        playerSlider.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Custom slider UI
        playerSlider.setUI(new BasicSliderUI(playerSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_BLUE);
                g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle trackBounds = trackRect;
                int trackHeight = 6;
                int trackY = trackBounds.y + (trackBounds.height - trackHeight) / 2;

                g2.setColor(BUTTON_SECONDARY);
                g2.fillRoundRect(trackBounds.x, trackY, trackBounds.width, trackHeight, 3, 3);

                int filledWidth = thumbRect.x - trackBounds.x;
                g2.setColor(ACCENT_BLUE);
                g2.fillRoundRect(trackBounds.x, trackY, filledWidth, trackHeight, 3, 3);
            }
        });

        sliderCard.add(sliderLabel);
        sliderCard.add(Box.createRigidArea(new Dimension(0, 20)));
        sliderCard.add(playerSlider);

        mainContainer.add(sliderCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- Settings Card (Mode and Type) ---
        JPanel settingsCard = createCard();
        settingsCard.setLayout(new GridLayout(1, 2, 15, 0));

        // Mode Button
        modeButton = createDropdownButton("Mode: Simple");
        JPopupMenu modePopup = createStyledPopup();
        modePopup.add(createPopupItem("Simple")).addActionListener(e -> modeButton.setText("Mode: Simple"));
        modePopup.add(createPopupItem("Picante")).addActionListener(e -> modeButton.setText("Mode: Picante"));
        modeButton.addActionListener(e -> modePopup.show(modeButton, 0, modeButton.getHeight()));

        // Type Button
        typeButton = createDropdownButton("Type: Individual");
        JPopupMenu typePopup = createStyledPopup();
        typePopup.add(createPopupItem("Individual")).addActionListener(e -> typeButton.setText("Type: Individual"));
        typePopup.add(createPopupItem("Team")).addActionListener(e -> typeButton.setText("Type: Team"));
        typeButton.addActionListener(e -> typePopup.show(typeButton, 0, typeButton.getHeight()));

        settingsCard.add(modeButton);
        settingsCard.add(typeButton);

        mainContainer.add(settingsCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // --- Action Buttons ---
        ChessButton startButton = new ChessButton("Start Game", BUTTON_PRIMARY, BUTTON_PRIMARY_HOVER);
        startButton.setPreferredSize(new Dimension(580, 55));
        startButton.setMaximumSize(new Dimension(580, 55));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());

        ChessButton botsButton = new ChessButton("Complete with Bots", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        botsButton.setPreferredSize(new Dimension(580, 55));
        botsButton.setMaximumSize(new Dimension(580, 55));
        botsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        botsButton.addActionListener(e -> configureBots());

        ChessButton createUserButton = new ChessButton("Create New User", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        createUserButton.setPreferredSize(new Dimension(580, 55));
        createUserButton.setMaximumSize(new Dimension(580, 55));
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.addActionListener(e -> {
            User newUser = createNewUser();
            if (newUser != null) {
                currentUser = newUser;
                System.out.println("Current user set to: " + currentUser.getName());
            }
        });

        ChessButton optionsButton = new ChessButton("Options", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        optionsButton.setPreferredSize(new Dimension(580, 55));
        optionsButton.setMaximumSize(new Dimension(580, 55));
        optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsButton.addActionListener(e -> openOptions());

        ChessButton exitButton = new ChessButton("Exit", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        exitButton.setPreferredSize(new Dimension(580, 55));
        exitButton.setMaximumSize(new Dimension(580, 55));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> exitGame());

        mainContainer.add(startButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(botsButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(createUserButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(optionsButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(exitButton);

        add(mainContainer, BorderLayout.CENTER);

        nbplayers = playerSlider.getValue();
        currentMode = modeButton.getText().replace("Mode: ", "");
        currentType = typeButton.getText().replace("Type: ", "");
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BACKGROUND_MEDIUM);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));
        card.setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        return card;
    }

    private JButton createDropdownButton(String text) {
        JButton button = new ChessButton(text, BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return button;
    }

    private JPopupMenu createStyledPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(BACKGROUND_MEDIUM);
        popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return popup;
    }

    private JMenuItem createPopupItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(BACKGROUND_MEDIUM);
        item.setForeground(TEXT_PRIMARY);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(BUTTON_SECONDARY_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(BACKGROUND_MEDIUM);
            }
        });

        return item;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGUI menu = new MenuGUI();
            menu.setVisible(true);
        });
    }
}