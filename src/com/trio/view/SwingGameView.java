package com.trio.view;

import com.trio.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Interface graphique Swing style sombre premium pour le jeu Trio (mode Solo).
 * Harmonisée avec le MenuGUI.
 */
public class SwingGameView extends JFrame implements GameView {

    // === Dark Premium Color Palette (harmonisé avec MenuGUI) ===
    private static final Color BACKGROUND = new Color(26, 26, 46); // Deep navy
    private static final Color CARD_BG = new Color(40, 45, 75); // Card background
    private static final Color PRIMARY = new Color(0, 217, 255); // Electric cyan
    private static final Color SUCCESS = new Color(16, 185, 129); // Success green
    private static final Color WARNING = new Color(255, 149, 0); // Orange
    private static final Color DANGER = new Color(239, 68, 68); // Red
    private static final Color GRAY_1 = new Color(100, 110, 140); // Lighter gray
    private static final Color GRAY_2 = new Color(80, 90, 130); // Border color
    private static final Color GRAY_3 = new Color(50, 55, 80); // Dark gray
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255); // White
    private static final Color TEXT_SECONDARY = new Color(160, 170, 200); // Light gray-blue

    // === Card Dimensions ===
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 110;

    // === UI Components ===
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JPanel playersPanel;
    private JPanel handPanel;
    private JPanel centerPanel;
    private JPanel actionsPanel;
    private JTextArea logArea;

    // Input handling
    private int selectedAction = -1;
    private Player selectedPlayer = null;
    private int selectedCenterIndex = -1;
    private final Object inputLock = new Object();

    public SwingGameView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TRIO - Mode Solo");
        // Permet de fermer l'application complètement lors du clic sur la croix
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        setSize(1200, 850);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND);

        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);

        playersPanel = createPlayersPanel();
        contentPanel.add(playersPanel, BorderLayout.WEST);

        JPanel gameArea = createGameAreaPanel();
        contentPanel.add(gameArea, BorderLayout.CENTER);

        actionsPanel = createActionsPanel();
        contentPanel.add(actionsPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Demande confirmation avant de quitter l'application.
     */
    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment quitter le jeu ?",
                "Quitter Trio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        titleLabel = new JLabel("TRIO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 36));
        titleLabel.setForeground(TEXT_PRIMARY);

        statusLabel = new JLabel("Mode Solo", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
        statusLabel.setForeground(TEXT_SECONDARY);

        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(5));
        titleGroup.add(statusLabel);

        header.add(titleGroup, BorderLayout.CENTER);
        return header;
    }

    private JPanel createPlayersPanel() {
        JPanel panel = createCardPanel("Joueurs");
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel createGameAreaPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // --- HAND PANEL ---
        JPanel handContainer = createCardPanel("Votre Main");
        handContainer.setLayout(new BorderLayout(0, 10));
        handContainer.removeAll();

        JLabel titleLabel = new JLabel("Votre Main");
        titleLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        handContainer.add(titleLabel, BorderLayout.NORTH);

        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
        handPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(handPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        handContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(handContainer, BorderLayout.SOUTH);

        // --- CENTER DECK avec défilement ---
        JPanel centerContainer = createCardPanel("Cartes du Centre");
        centerContainer.setLayout(new BorderLayout(0, 10));
        centerContainer.removeAll();

        JLabel centerTitle = new JLabel("Cartes du Centre");
        centerTitle.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        centerTitle.setForeground(TEXT_PRIMARY);
        centerTitle.setBorder(new EmptyBorder(0, 5, 0, 0));
        centerContainer.add(centerTitle, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 6, 10, 10)); // 3 lignes, 6 colonnes
        centerPanel.setOpaque(false);

        JScrollPane centerScrollPane = new JScrollPane(centerPanel);
        centerScrollPane.setOpaque(false);
        centerScrollPane.getViewport().setOpaque(false);
        centerScrollPane.setBorder(null);
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        centerContainer.add(centerScrollPane, BorderLayout.CENTER);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = createCardPanel("Actions");
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = createCardPanel("Historique");
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setBackground(CARD_BG);
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, GRAY_3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel);

        return panel;
    }

    // === CUSTOM CARD VIEW ===

    private JPanel createCardView(Card card, boolean forceVisible) {
        // Couleur de fond basée sur la valeur de la carte (1-12)
        final Color cardColor = forceVisible ? getCardColor(card.getValue()) : GRAY_3;

        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(cardColor.darker());
                g2.setStroke(new BasicStroke(2));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
            }
        };

        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        if (forceVisible) {
            // Valeur en haut à gauche avec fond contrasté
            JLabel valueLabel = new JLabel(String.valueOf(card.getValue()));
            valueLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
            cardPanel.add(valueLabel, BorderLayout.NORTH);

            // Coordonnée au centre - affichage complet avec retour à la ligne
            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.setLayout(new GridBagLayout());

            String coord = card.getCoordinate();
            JLabel coordLabel = new JLabel(
                    "<html><div style='text-align:center;width:60px;'>" + coord + "</div></html>");
            coordLabel.setFont(new Font("SF Pro Text", Font.BOLD, 10));
            coordLabel.setForeground(Color.WHITE);
            coordLabel.setHorizontalAlignment(SwingConstants.CENTER);
            coordLabel.setVerticalAlignment(SwingConstants.CENTER);
            centerPanel.add(coordLabel);
            cardPanel.add(centerPanel, BorderLayout.CENTER);

        } else {
            JLabel hiddenLabel = new JLabel("?");
            hiddenLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
            hiddenLabel.setForeground(GRAY_1);
            hiddenLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cardPanel.add(hiddenLabel, BorderLayout.CENTER);
        }

        return cardPanel;
    }

    /**
     * Retourne une couleur distincte pour chaque valeur de carte (1-12)
     */
    private Color getCardColor(int value) {
        switch (value) {
            case 1:
                return new Color(231, 76, 60); // Rouge
            case 2:
                return new Color(230, 126, 34); // Orange
            case 3:
                return new Color(241, 196, 15); // Jaune
            case 4:
                return new Color(46, 204, 113); // Vert
            case 5:
                return new Color(26, 188, 156); // Turquoise
            case 6:
                return new Color(52, 152, 219); // Bleu clair
            case 7:
                return new Color(41, 128, 185); // Bleu
            case 8:
                return new Color(155, 89, 182); // Violet
            case 9:
                return new Color(142, 68, 173); // Violet foncé
            case 10:
                return new Color(52, 73, 94); // Gris foncé
            case 11:
                return new Color(44, 62, 80); // Noir bleuté
            case 12:
                return new Color(192, 57, 43); // Rouge foncé
            default:
                return PRIMARY;
        }
    }

    // === UTILS ===

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    private JButton createAppleButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(color.darker());
                else if (getModel().isRollover())
                    g2.setColor(color.brighter());
                else
                    g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(190, 44));
        btn.setPreferredSize(new Dimension(190, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // === GAME VIEW IMPLEMENTATION ===

    @Override
    public void displayWelcome(int nbPlayers) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Mode Solo • " + nbPlayers + " joueurs"));
        log("═══ DÉBUT DE LA PARTIE TRIO ═══");
    }

    @Override
    public void displayTurnStart(Player player) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Tour de " + player.getPseudo());
            statusLabel.setForeground(player instanceof User ? PRIMARY : TEXT_SECONDARY);
        });
        log("Tour de " + player.getPseudo());
    }

    @Override
    public void displayPlayerHand(Player player) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            handPanel.add(Box.createHorizontalStrut(5));
            for (Card c : player.getDeck().getCards()) {
                JPanel card = createCardView(c, true);
                handPanel.add(card);
                handPanel.add(Box.createHorizontalStrut(10));
            }
            handPanel.revalidate();
            handPanel.repaint();
        });
    }

    @Override
    public void displayVisibleCards(List<Player> players, Deck centerDeck) {
        SwingUtilities.invokeLater(() -> {
            updatePlayersPanel(players);
            centerPanel.removeAll();
            if (centerDeck != null) {
                for (Card c : centerDeck.getCards()) {
                    JPanel card = createCardView(c, c.isVisible());
                    centerPanel.add(card);
                }
            }
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }

    private void updatePlayersPanel(List<Player> players) {
        playersPanel.removeAll();
        JLabel title = new JLabel("Joueurs");
        title.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        playersPanel.add(title);
        playersPanel.add(Box.createVerticalStrut(15));

        for (Player player : players) {
            JPanel playerCard = new JPanel();
            playerCard.setLayout(new BoxLayout(playerCard, BoxLayout.Y_AXIS));
            playerCard.setOpaque(false);
            playerCard.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, player instanceof User ? PRIMARY : GRAY_2),
                    new EmptyBorder(10, 10, 10, 10)));
            playerCard.setMaximumSize(new Dimension(200, 80));
            playerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLabel = new JLabel(player.getPseudo());
            nameLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            nameLabel.setForeground(player instanceof User ? PRIMARY : TEXT_PRIMARY);

            JLabel scoreLabel = new JLabel(player.getTrioCount() + " trio" + (player.getTrioCount() != 1 ? "s" : ""));
            scoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
            scoreLabel.setForeground(TEXT_PRIMARY);

            playerCard.add(nameLabel);
            playerCard.add(scoreLabel);
            playersPanel.add(playerCard);
            playersPanel.add(Box.createVerticalStrut(8));
        }
        playersPanel.revalidate();
        playersPanel.repaint();
    }

    // === DISPLAY GAME WINNER - CUSTOMIZED ===

    @Override
    public void displayGameWinner(Player winner) {
        log("\n🏆 " + winner.getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!");

        String companyName = "Inconnue";
        List<Deck> winningTrios = winner.getTrios();
        if (!winningTrios.isEmpty()) {
            Deck lastTrio = winningTrios.get(winningTrios.size() - 1);
            if (!lastTrio.isEmpty()) {
                companyName = lastTrio.getCard(0).getCoordinate();
            }
        }
        final String finalCompanyName = companyName;

        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🏆 VICTOIRE !");
            statusLabel.setForeground(SUCCESS);

            JDialog victoryDialog = new JDialog(this, "Stage Trouvé !", true);
            victoryDialog.setUndecorated(true);
            victoryDialog.setBackground(new Color(0, 0, 0, 0));

            JPanel content = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                    g2.setColor(SUCCESS);
                    g2.setStroke(new BasicStroke(3));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 30, 30));
                    g2.dispose();
                }
            };
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(40, 50, 40, 50));

            JLabel iconLabel = new JLabel("🎓");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titleLabel = new JLabel("Bien joué !");
            titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
            titleLabel.setForeground(SUCCESS);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextArea messageArea = new JTextArea(
                    "Après des milliers de candidatures,\nvous avez réussi à obtenir un poste chez\n" + finalCompanyName
                            + " !");
            messageArea.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
            messageArea.setForeground(TEXT_PRIMARY);
            messageArea.setOpaque(false);
            messageArea.setEditable(false);
            messageArea.setWrapStyleWord(true);
            messageArea.setLineWrap(true);
            messageArea.setHighlighter(null);
            messageArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel msgContainer = new JPanel();
            msgContainer.setOpaque(false);
            msgContainer.add(messageArea);

            JPanel cardsPanel = new JPanel();
            cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            cardsPanel.setOpaque(false);

            if (!winningTrios.isEmpty()) {
                Deck lastTrio = winningTrios.get(winningTrios.size() - 1);
                for (Card c : lastTrio.getCards()) {
                    JPanel cardView = createCardView(c, true);
                    cardsPanel.add(cardView);
                }
            }

            JButton closeBtn = createAppleButton("Célébrer & Quitter", PRIMARY);
            closeBtn.addActionListener(e -> System.exit(0));
            closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            content.add(iconLabel);
            content.add(Box.createVerticalStrut(10));
            content.add(titleLabel);
            content.add(Box.createVerticalStrut(20));
            content.add(msgContainer);
            content.add(Box.createVerticalStrut(20));
            content.add(cardsPanel);
            content.add(Box.createVerticalStrut(30));
            content.add(closeBtn);

            victoryDialog.add(content);
            victoryDialog.pack();
            victoryDialog.setLocationRelativeTo(this);
            victoryDialog.setVisible(true);
        });
    }

    // === OTHER LOGS ===

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public void displayRevealedCards(List<RevealedCard> revealedCards) {
        if (!revealedCards.isEmpty()) {
            StringBuilder sb = new StringBuilder("Cartes révélées: ");
            for (RevealedCard rc : revealedCards)
                sb.append("[").append(rc.getValue()).append("] ");
            log(sb.toString());
        }
    }

    @Override
    public void displayCardRevealed(Card card, Player owner, int cardIndex, boolean isFirst, boolean isCorrect,
            int expectedValue) {
        String source = owner != null ? owner.getPseudo() : "Centre";
        if (isFirst)
            log("✓ Première carte: [" + card.getValue() + "] de " + source);
        else if (isCorrect)
            log("✓ Bonne carte: [" + card.getValue() + "] de " + source);
        else
            log("✗ Mauvaise carte! Attendu: " + expectedValue + ", Reçu: " + card.getValue());
    }

    @Override
    public void displayTrioSuccess(Player winner, int trioCount) {
        log("\n🎉 TRIO COMPLÉTÉ!");
        log(winner.getPseudo() + " a maintenant " + trioCount + " trio(s)");

        JDialog toast = new JDialog(this, false);
        toast.setUndecorated(true);
        JLabel l = new JLabel("🎉 TRIO pour " + winner.getPseudo() + " !", SwingConstants.CENTER);
        l.setFont(new Font("SF Pro Text", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        l.setBorder(new EmptyBorder(15, 30, 15, 30));
        JPanel p = new JPanel();
        p.setBackground(new Color(52, 199, 89, 220));
        p.add(l);
        toast.add(p);
        toast.pack();
        toast.setLocation(getX() + getWidth() / 2 - toast.getWidth() / 2, getY() + 100);
        toast.setVisible(true);

        new Timer(2000, e -> toast.dispose()).start();
    }

    @Override
    public void displayTurnFailed() {
        log("\n✗ Échec du tour.");
    }

    @Override
    public void displayError(String message) {
        log("⚠️ " + message);
    }

    @Override
    public void displayBotAction(Bot bot, String action, Player target) {
        if (target != null)
            log(bot.getPseudo() + " " + action + " " + target.getPseudo());
        else
            log(bot.getPseudo() + " " + action);
    }

    // === INPUT ===

    @Override
    public int promptAction() {
        selectedAction = -1;
        SwingUtilities.invokeLater(() -> {
            actionsPanel.removeAll();
            JLabel title = new JLabel("Actions");
            title.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsPanel.add(title);
            actionsPanel.add(Box.createVerticalStrut(15));

            addActionButton("Ma carte MIN", 1, PRIMARY);
            addActionButton("Ma carte MAX", 2, PRIMARY);
            actionsPanel.add(Box.createVerticalStrut(10));
            addActionButton("MIN autre joueur", 3, WARNING);
            addActionButton("MAX autre joueur", 4, WARNING);
            actionsPanel.add(Box.createVerticalStrut(10));
            addActionButton("Carte du centre", 5, SUCCESS);
            actionsPanel.add(Box.createVerticalStrut(20));
            addActionButton("Arrêter le tour", 0, DANGER);

            actionsPanel.revalidate();
            actionsPanel.repaint();
        });

        synchronized (inputLock) {
            while (selectedAction == -1) {
                try {
                    inputLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return selectedAction;
    }

    private void addActionButton(String text, int actionCode, Color color) {
        JButton btn = createAppleButton(text, color);
        btn.addActionListener(e -> {
            synchronized (inputLock) {
                selectedAction = actionCode;
                inputLock.notifyAll();
            }
        });
        actionsPanel.add(btn);
        actionsPanel.add(Box.createVerticalStrut(8));
    }

    @Override
    public Player promptSelectPlayer(List<Player> availablePlayers) {
        selectedPlayer = null;
        SwingUtilities.invokeLater(() -> {
            actionsPanel.removeAll();
            JLabel title = new JLabel("Choisir un joueur");
            title.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsPanel.add(title);
            actionsPanel.add(Box.createVerticalStrut(15));
            for (Player p : availablePlayers) {
                JButton btn = createAppleButton(p.getPseudo(), PRIMARY);
                btn.addActionListener(e -> {
                    synchronized (inputLock) {
                        selectedPlayer = p;
                        inputLock.notifyAll();
                    }
                });
                actionsPanel.add(btn);
                actionsPanel.add(Box.createVerticalStrut(8));
            }
            actionsPanel.revalidate();
            actionsPanel.repaint();
        });
        synchronized (inputLock) {
            while (selectedPlayer == null) {
                try {
                    inputLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return selectedPlayer;
    }

    @Override
    public int promptSelectCenterCard(Deck centerDeck) {
        selectedCenterIndex = -1;
        SwingUtilities.invokeLater(() -> {
            actionsPanel.removeAll();

            JLabel title = new JLabel("Choisir une carte");
            title.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsPanel.add(title);
            actionsPanel.add(Box.createVerticalStrut(10));

            // Panel pour les boutons avec scroll
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
            buttonsPanel.setOpaque(false);

            for (int i = 0; i < centerDeck.getSize(); i++) {
                Card c = centerDeck.getCard(i);
                String text = (i + 1) + ". " + (c.isVisible() ? "[" + c.getValue() + "]" : "[?]");
                final int index = i;
                JButton btn = createAppleButton(text, c.isVisible() ? WARNING : GRAY_1);
                btn.addActionListener(e -> {
                    synchronized (inputLock) {
                        selectedCenterIndex = index;
                        inputLock.notifyAll();
                    }
                });
                buttonsPanel.add(btn);
                buttonsPanel.add(Box.createVerticalStrut(5));
            }

            JScrollPane scrollPane = new JScrollPane(buttonsPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setPreferredSize(new Dimension(200, 400));
            scrollPane.setMaximumSize(new Dimension(200, 400));

            actionsPanel.add(scrollPane);
            actionsPanel.revalidate();
            actionsPanel.repaint();
        });
        synchronized (inputLock) {
            while (selectedCenterIndex == -1) {
                try {
                    inputLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return selectedCenterIndex;
    }

    @Override
    public void startGame() {
        setVisible(true);
    }
}
