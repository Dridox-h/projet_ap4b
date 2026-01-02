package com.trio.view;

import com.trio.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Interface graphique Swing style Apple pour le jeu Trio (mode Solo).
 * Design minimaliste, clean avec couleurs douces.
 */
public class SwingGameView extends JFrame implements GameView {

    // === Apple-like Color Palette ===
    private static final Color BACKGROUND = new Color(248, 248, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 122, 255); // Apple Blue
    private static final Color SUCCESS = new Color(52, 199, 89); // Apple Green
    private static final Color WARNING = new Color(255, 149, 0); // Apple Orange
    private static final Color DANGER = new Color(255, 59, 48); // Apple Red
    private static final Color GRAY_1 = new Color(142, 142, 147);
    private static final Color GRAY_2 = new Color(174, 174, 178);
    private static final Color GRAY_3 = new Color(199, 199, 204);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(60, 60, 67, 153);

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

    // State
    private List<Player> currentPlayers;
    private Deck currentCenterDeck;

    public SwingGameView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TRIO - Mode Solo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND);

        // Main container
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center content
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);

        // Left: Players + Scores
        playersPanel = createPlayersPanel();
        contentPanel.add(playersPanel, BorderLayout.WEST);

        // Center: Game area (cards + center)
        JPanel gameArea = createGameAreaPanel();
        contentPanel.add(gameArea, BorderLayout.CENTER);

        // Right: Actions
        actionsPanel = createActionsPanel();
        contentPanel.add(actionsPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Bottom: Log
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
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
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel createGameAreaPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // --- CORRECTION DU PANNEAU JOUEUR ---
        // 1. On crée le conteneur visuel (la boîte blanche avec bordure)
        JPanel handContainer = createCardPanel("Votre Main");
        handContainer.setLayout(new BorderLayout(0, 10)); // BorderLayout pour placer le titre au dessus

        // 2. On retire le titre par défaut ajouté par createCardPanel pour le gérer nous-même proprement
        handContainer.removeAll();

        // 3. On remet le Titre (fixe, ne défilera pas)
        JLabel titleLabel = new JLabel("Votre Main");
        titleLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 5, 0, 0)); // Petite marge
        handContainer.add(titleLabel, BorderLayout.NORTH);

        // 4. On configure handPanel pour contenir UNIQUEMENT les cartes
        // BoxLayout.X_AXIS force l'alignement horizontal sans retour à la ligne
        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
        handPanel.setOpaque(false);

        // 5. On ajoute le ScrollPane horizontal
        JScrollPane scrollPane = new JScrollPane(handPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // Pas de bordure moche
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Pas de scroll vertical
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Scroll horizontal si besoin

        // Astuce pour le scroll tactile/souris plus fluide
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        handContainer.add(scrollPane, BorderLayout.CENTER);

        // Placement en BAS (SOUTH) pour laisser la place au centre
        panel.add(handContainer, BorderLayout.SOUTH);

        // ------------------------------------

        // Center: Center deck
        centerPanel = createCardPanel("Cartes du Centre");
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panel.add(centerPanel, BorderLayout.CENTER);

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
        logArea.setBackground(Color.WHITE);
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

    // Custom rounded border
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
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
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

    private JPanel createCardView(int value, boolean visible) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(visible ? Color.WHITE : GRAY_3);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(GRAY_2);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(55, 80));
        card.setMaximumSize(new Dimension(55, 80));
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());

        JLabel label = new JLabel(visible ? String.valueOf(value) : "?");
        label.setFont(new Font("SF Pro Display", Font.BOLD, 22));
        label.setForeground(visible ? TEXT_PRIMARY : GRAY_1);
        card.add(label);

        return card;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
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
            playerCard.setMaximumSize(new Dimension(180, 80));
            playerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLabel = new JLabel(player.getPseudo());
            nameLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            nameLabel.setForeground(player instanceof User ? PRIMARY : TEXT_PRIMARY);

            JLabel scoreLabel = new JLabel(player.getTrioCount() + " trio" + (player.getTrioCount() != 1 ? "s" : ""));
            scoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
            scoreLabel.setForeground(TEXT_PRIMARY);

            String type = player instanceof User ? "👤 Vous" : "🤖 Bot";
            JLabel typeLabel = new JLabel(type);
            typeLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 11));
            typeLabel.setForeground(TEXT_SECONDARY);

            playerCard.add(nameLabel);
            playerCard.add(scoreLabel);
            playerCard.add(typeLabel);

            playersPanel.add(playerCard);
            playersPanel.add(Box.createVerticalStrut(8));
        }

        playersPanel.revalidate();
        playersPanel.repaint();
    }

    // === GAME VIEW IMPLEMENTATION ===

    @Override
    public void displayWelcome(int nbPlayers) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Mode Solo • " + nbPlayers + " joueurs");
        });
        log("═══ DÉBUT DE LA PARTIE TRIO ═══");
        log("Nombre de joueurs: " + nbPlayers);
        log("Objectif: 3 trios pour gagner\n");
    }

    @Override
    public void displayTurnStart(Player player) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Tour de " + player.getPseudo());
            statusLabel.setForeground(player instanceof User ? PRIMARY : TEXT_SECONDARY);
        });
        log("\n═══════════════════════════════════");
        log("   Tour de " + player.getPseudo());
        log("═══════════════════════════════════");
    }

    @Override
    public void displayPlayerHand(Player player) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();

            // NOTE: On ne rajoute PLUS le titre "Votre Main" ici, car il est géré dans le parent.

            // On ajoute une petite marge au début pour ne pas coller au bord
            handPanel.add(Box.createHorizontalStrut(5));

            for (Card c : player.getDeck().getCards()) {
                JPanel card = createCardView(c.getValue(), !c.isVisible());
                handPanel.add(card);
                // Espace entre les cartes
                handPanel.add(Box.createHorizontalStrut(10));
            }

            handPanel.revalidate();
            handPanel.repaint();
        });
    }


    @Override
    public void displayVisibleCards(List<Player> players, Deck centerDeck) {
        this.currentPlayers = players;
        this.currentCenterDeck = centerDeck;

        SwingUtilities.invokeLater(() -> {
            updatePlayersPanel(players);

            centerPanel.removeAll();

            JLabel title = new JLabel("Cartes du Centre");
            title.setFont(new Font("SF Pro Text", Font.BOLD, 14));
            title.setForeground(TEXT_PRIMARY);
            centerPanel.add(title);

            if (centerDeck != null) {
                for (Card c : centerDeck.getCards()) {
                    JPanel card = createCardView(c.getValue(), c.isVisible());
                    centerPanel.add(card);
                }
            }

            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }

    @Override
    public void displayRevealedCards(List<RevealedCard> revealedCards) {
        if (!revealedCards.isEmpty()) {
            StringBuilder sb = new StringBuilder("Cartes révélées: ");
            for (RevealedCard rc : revealedCards) {
                sb.append("[").append(rc.getValue()).append("] ");
            }
            log(sb.toString());
        }
    }

    @Override
    public void displayCardRevealed(Card card, Player owner, int cardIndex, boolean isFirst, boolean isCorrect,
            int expectedValue) {
        String source = owner != null ? owner.getPseudo() : "Centre";
        if (isFirst) {
            log("✓ Première carte: [" + card.getValue() + "] de " + source);
        } else if (isCorrect) {
            log("✓ Bonne carte: [" + card.getValue() + "] de " + source);
        } else {
            log("✗ Mauvaise carte! Attendu: " + expectedValue + ", Reçu: " + card.getValue());
        }
    }

    @Override
    public void displayTrioSuccess(Player winner, int trioCount) {
        log("\n🎉 TRIO COMPLÉTÉ!");
        log(winner.getPseudo() + " a maintenant " + trioCount + " trio(s)");
        JOptionPane.showMessageDialog(this,
                "🎉 TRIO!\n" + winner.getPseudo() + " a maintenant " + trioCount + " trio(s)",
                "Trio réussi!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayTurnFailed() {
        log("\n✗ Échec du tour. Cartes remises face cachée.");
    }

    @Override
    public void displayGameWinner(Player winner) {
        log("\n🏆 " + winner.getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!");
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🏆 " + winner.getPseudo() + " GAGNE!");
            statusLabel.setForeground(SUCCESS);
        });
        JOptionPane.showMessageDialog(this,
                "🏆 " + winner.getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!",
                "Fin de partie!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayError(String message) {
        log("⚠️ " + message);
    }

    @Override
    public void displayBotAction(Bot bot, String action, Player target) {
        if (target != null) {
            log(bot.getPseudo() + " " + action + " " + target.getPseudo());
        } else {
            log(bot.getPseudo() + " " + action);
        }
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
            actionsPanel.add(Box.createVerticalStrut(15));

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
                actionsPanel.add(btn);
                actionsPanel.add(Box.createVerticalStrut(5));
            }

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
}
