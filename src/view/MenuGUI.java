package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import controller.MenuController;
import model.Menu;
import model.User;

public class MenuGUI extends JFrame {

    // Use MenuController to handle interactions between View and Model
    private MenuController controller;
    private Menu menu; // Reference to model for reading state

    // UI Components
    private JSlider playerSlider;
    private JButton typeButton;

    // Chess.com Blue Theme Colors
    private static final Color BACKGROUND_DARK = new Color(18, 27, 40);
    private static final Color BACKGROUND_MEDIUM = new Color(28, 40, 58);
    private static final Color ACCENT_BLUE = new Color(129, 182, 232);
    private static final Color BUTTON_PRIMARY = new Color(108, 162, 215);
    private static final Color BUTTON_PRIMARY_HOVER = new Color(129, 182, 232);
    private static final Color BUTTON_SECONDARY = new Color(44, 62, 84);
    private static final Color BUTTON_SECONDARY_HOVER = new Color(54, 72, 94);
    private static final Color TEXT_PRIMARY = new Color(235, 240, 245);
    private static final Color TEXT_SECONDARY = new Color(162, 177, 195);
    private static final Color BORDER_COLOR = new Color(62, 82, 105);

    private void startGame() {
        menu.setNbPlayers(playerSlider.getValue());
        menu.setType(typeButton.getText().replace("Type: ", ""));

        // Use controller to validate and start game
        Menu.ValidationResult validation = controller.startGame();

        if (!validation.isValid()) {
            JOptionPane.showMessageDialog(this,
                    validation.getErrorMessage(),
                    "Configuration Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Start the game
        Game.Game game = new Game.Game(menu.getNbPlayers(), menu.getNbBots(), menu.getType());
        game.setVisible(true);
    }

    private int configureBots() {
        // Use controller to configure bots
        controller.configureBots(playerSlider.getValue());

        JOptionPane.showMessageDialog(this,
                String.format("Game will be completed with %d bots.\nTotal players: %d | Bots: %d",
                        menu.getNbBots(), menu.getNbPlayers(), menu.getNbBots()),
                "Bot Configuration",
                JOptionPane.INFORMATION_MESSAGE);
        return menu.getNbBots();
    }

    private User selectExistingUser() {
        // Check if UserLogs.txt exists and has users using controller
        if (controller.isUserLogsEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No users found. Please create a user first.",
                    "No Users Available",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Create the dialog
        JDialog selectDialog = new JDialog(this, "Select User", true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        selectDialog.setSize(800, screenSize.height);
        selectDialog.setLocationRelativeTo(this);
        selectDialog.getContentPane().setBackground(BACKGROUND_DARK);
        selectDialog.setLayout(new BorderLayout());

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Select User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Display users from logs
        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setBackground(BACKGROUND_MEDIUM);
        usersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        // Read and display users
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader("logs/UserLogs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Parse user info from log line
                String displayText = line;
                JLabel userLabel = new JLabel(displayText);
                userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                userLabel.setForeground(TEXT_PRIMARY);
                usersPanel.add(userLabel);
                usersPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading user logs: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.getViewport().setBackground(BACKGROUND_MEDIUM);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ID input panel
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        idPanel.setBackground(BACKGROUND_DARK);

        JLabel idLabel = new JLabel("Enter User ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(TEXT_PRIMARY);

        JTextField idField = new JTextField(10);
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idField.setBackground(BACKGROUND_MEDIUM);
        idField.setForeground(TEXT_PRIMARY);
        idField.setCaretColor(TEXT_PRIMARY);
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        idPanel.add(idLabel);
        idPanel.add(idField);
        mainPanel.add(idPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Container to hold the result
        final User[] resultUser = new User[1];

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_DARK);

        ChessButton selectButton = new ChessButton("Select User", BUTTON_PRIMARY, BUTTON_PRIMARY_HOVER);
        selectButton.setPreferredSize(new Dimension(150, 45));
        selectButton.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(selectDialog,
                        "Please enter a user ID.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(selectDialog,
                        "Please enter a valid number for ID.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use controller to select user
            User selectedUser = controller.selectExistingUser(id);
            if (selectedUser != null) {
                resultUser[0] = selectedUser;
                JOptionPane.showMessageDialog(selectDialog,
                        String.format("User selected successfully!\nName: %s\nVictories: %d",
                                selectedUser.getName(), selectedUser.getNBVictoire()),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                selectDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(selectDialog,
                        "User with ID " + id + " not found.",
                        "User Not Found",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        ChessButton cancelButton = new ChessButton("Cancel", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> selectDialog.dispose());

        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        selectDialog.add(mainPanel, BorderLayout.CENTER);
        selectDialog.setVisible(true);

        return resultUser[0];
    }

    private User createNewUser() {
        // Create the dialog
        JDialog formDialog = new JDialog(this, "Create New User", true);
        // Make dialog full width
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        formDialog.setSize(800, screenSize.height);
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

            // Use controller to create user
            User newUser = controller.createNewUser(name, age, avatarPath);

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
        // Initialize the Model and Controller
        menu = new Menu();
        controller = new MenuController(menu);

        // Window setup
        setTitle("Game Menu");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make window full width
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_DARK);

        // --- Right Side Panel for User Info ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BACKGROUND_DARK);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 40));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        // User Info Card on the right
        JPanel userInfoCard = new JPanel();
        userInfoCard.setLayout(new BoxLayout(userInfoCard, BoxLayout.Y_AXIS));
        userInfoCard.setBackground(BACKGROUND_MEDIUM);
        userInfoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)));
        userInfoCard.setMaximumSize(new Dimension(300, 200));

        JLabel userInfoTitle = new JLabel("Current User");
        userInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userInfoTitle.setForeground(TEXT_PRIMARY);
        userInfoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userNameDisplay = new JLabel(
                menu.getCurrentUser() != null ? menu.getCurrentUser().getName() : "No User");
        userNameDisplay.setFont(new Font("Segoe UI", Font.BOLD, 20));
        userNameDisplay.setForeground(ACCENT_BLUE);
        userNameDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel victoriesDisplay = new JLabel(
                "Victories: " + (menu.getCurrentUser() != null ? menu.getCurrentUser().getNBVictoire() : 0));
        victoriesDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        victoriesDisplay.setForeground(TEXT_PRIMARY);
        victoriesDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoCard.add(userInfoTitle);
        userInfoCard.add(Box.createRigidArea(new Dimension(0, 15)));
        userInfoCard.add(userNameDisplay);
        userInfoCard.add(Box.createRigidArea(new Dimension(0, 8)));
        userInfoCard.add(victoriesDisplay);

        rightPanel.add(userInfoCard);
        rightPanel.add(Box.createVerticalGlue());

        add(rightPanel, BorderLayout.EAST);

        // --- Left Side Panel for Game Settings ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND_DARK);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 20));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // Current Settings Card on the left
        JPanel settingsDisplayCard = new JPanel();
        settingsDisplayCard.setLayout(new BoxLayout(settingsDisplayCard, BoxLayout.Y_AXIS));
        settingsDisplayCard.setBackground(BACKGROUND_MEDIUM);
        settingsDisplayCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)));
        settingsDisplayCard.setMaximumSize(new Dimension(300, 200));

        JLabel settingsTitle = new JLabel("Current Settings");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        settingsTitle.setForeground(TEXT_PRIMARY);
        settingsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel settingsInfo = new JLabel(String.format(
                "Players: %d | Bots: %d | Type: %s",
                menu.getNbPlayers(), menu.getNbBots(), menu.getType()));
        settingsInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        settingsInfo.setForeground(TEXT_PRIMARY);
        settingsInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsDisplayCard.add(settingsTitle);
        settingsDisplayCard.add(Box.createRigidArea(new Dimension(0, 15)));
        settingsDisplayCard.add(settingsInfo);

        leftPanel.add(settingsDisplayCard);
        leftPanel.add(Box.createVerticalGlue());

        add(leftPanel, BorderLayout.WEST);

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
        mainContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Player Slider Card ---
        JPanel sliderCard = createCard();
        sliderCard.setLayout(new BoxLayout(sliderCard, BoxLayout.Y_AXIS));

        JLabel sliderLabel = new JLabel("Number of Players");
        sliderLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sliderLabel.setForeground(TEXT_PRIMARY);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerSlider = new JSlider(JSlider.HORIZONTAL, 2, 6, 2);
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

        // Update settings display when slider changes
        playerSlider.addChangeListener(e -> {
            menu.setNbPlayers(playerSlider.getValue());
            settingsInfo.setText(String.format(
                    "Players: %d | Bots: %d | Type: %s",
                    menu.getNbPlayers(), menu.getNbBots(), menu.getType()));
        });

        // --- Type Button ---
        JPanel typeCard = createCard();
        typeCard.setLayout(new BoxLayout(typeCard, BoxLayout.Y_AXIS));

        JLabel typeLabel = new JLabel("Game Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        typeLabel.setForeground(TEXT_PRIMARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        typeButton = createDropdownButton("Type: Individual");
        typeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        typeButton.setMaximumSize(new Dimension(300, 50));
        JPopupMenu typePopup = createStyledPopup();
        typePopup.add(createPopupItem("Individual")).addActionListener(e -> {
            typeButton.setText("Type: Individual");
            menu.setType("Individual");
            settingsInfo.setText(String.format(
                    "Players: %d | Bots: %d | Type: %s",
                    menu.getNbPlayers(), menu.getNbBots(), menu.getType()));
        });
        typePopup.add(createPopupItem("Team")).addActionListener(e -> {
            typeButton.setText("Type: Team");
            menu.setType("Team");
            settingsInfo.setText(String.format(
                    "Players: %d | Bots: %d | Type: %s",
                    menu.getNbPlayers(), menu.getNbBots(), menu.getType()));
        });
        typeButton.addActionListener(e -> typePopup.show(typeButton, 0, typeButton.getHeight()));

        typeCard.add(typeLabel);
        typeCard.add(Box.createRigidArea(new Dimension(0, 15)));
        typeCard.add(typeButton);

        mainContainer.add(typeCard);
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
        botsButton.addActionListener(e -> {
            configureBots();
            settingsInfo.setText(String.format(
                    "Players: %d | Bots: %d | Type: %s",
                    menu.getNbPlayers(), menu.getNbBots(), menu.getType()));
        });

        // User management buttons panel (side by side)
        ChessButton createUserButton = new ChessButton("Create New User", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        createUserButton.setPreferredSize(new Dimension(280, 55));
        createUserButton.setMaximumSize(new Dimension(280, 55));
        createUserButton.addActionListener(e -> {
            User newUser = createNewUser();
            if (newUser != null) {
                // User is already set and logged by controller
                // Update right panel user info
                userNameDisplay.setText(menu.getCurrentUser().getName());
                victoriesDisplay.setText("Victories: " + menu.getCurrentUser().getNBVictoire());
            }
        });

        ChessButton selectUserButton = new ChessButton("Select a User", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        selectUserButton.setPreferredSize(new Dimension(280, 55));
        selectUserButton.setMaximumSize(new Dimension(280, 55));
        selectUserButton.addActionListener(e -> {
            User selectedUser = selectExistingUser();
            if (selectedUser != null) {
                // User is already set by controller
                // Update right panel user info
                userNameDisplay.setText(menu.getCurrentUser().getName());
                victoriesDisplay.setText("Victories: " + menu.getCurrentUser().getNBVictoire());
            }
        });

        // Create horizontal box for buttons
        Box userButtonsPanel = Box.createHorizontalBox();
        userButtonsPanel.add(Box.createHorizontalGlue());
        userButtonsPanel.add(createUserButton);
        userButtonsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        userButtonsPanel.add(selectUserButton);
        userButtonsPanel.add(Box.createHorizontalGlue());
        userButtonsPanel.setMaximumSize(new Dimension(580, 55));
        userButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ChessButton optionsButton = new ChessButton("Options", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        optionsButton.setPreferredSize(new Dimension(580, 55));
        optionsButton.setMaximumSize(new Dimension(580, 55));
        optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsButton.addActionListener(e -> openOptions());

        ChessButton exitButton = new ChessButton("Exit", BUTTON_SECONDARY, BUTTON_SECONDARY_HOVER);
        exitButton.setPreferredSize(new Dimension(580, 55));
        exitButton.setMaximumSize(new Dimension(580, 55));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> controller.exitGame());

        mainContainer.add(startButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(botsButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(userButtonsPanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(optionsButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContainer.add(exitButton);

        add(mainContainer, BorderLayout.CENTER);

        menu.setNbPlayers(playerSlider.getValue());
        menu.setType(typeButton.getText().replace("Type: ", ""));
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