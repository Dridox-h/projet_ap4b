package com.trio.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Interface graphique Swing style Apple pour le menu du jeu Trio.
 * Design minimaliste, clean avec couleurs douces et animations subtiles.
 */
public class SwingMenuView extends JFrame implements MenuView {

    // === Apple-like Color Palette ===
    private static final Color BACKGROUND = new Color(248, 248, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 122, 255); // Apple Blue
    private static final Color SUCCESS = new Color(52, 199, 89); // Apple Green
    private static final Color WARNING = new Color(255, 149, 0); // Apple Orange
    private static final Color GRAY_2 = new Color(174, 174, 178);
    private static final Color GRAY_3 = new Color(199, 199, 204);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(60, 60, 67, 153);

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;

    public SwingMenuView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TRIO - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450); // Légèrement plus grand pour le confort
        setLocationRelativeTo(null);
        setBackground(BACKGROUND);

        // Main panel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(60, 60, 60, 60));

        // Title card
        JPanel titleCard = createTitleCard();
        mainPanel.add(titleCard);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTitleCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, GRAY_3),
                new EmptyBorder(40, 40, 40, 40)));
        card.setMaximumSize(new Dimension(400, 300));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logoLabel = new JLabel("🃏");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64)); // Segoe UI Emoji pour Windows, Apple Color Emoji pour Mac
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        titleLabel = new JLabel("TRIO");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        subtitleLabel = new JLabel("Le jeu de déduction");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(logoLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);
        card.add(Box.createVerticalGlue());

        return card;
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

    private JButton createAppleButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 50));
        btn.setMaximumSize(new Dimension(280, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // === MENU VIEW IMPLEMENTATION ===

    @Override
    public void displayWelcome() {
        // La fenêtre principale est déjà affichée avec le titre
        // On peut éventuellement rafraîchir ici
    }

    @Override
    public String promptPseudo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Entrez votre pseudo:");
        label.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField textField = new JTextField("Joueur");
        textField.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        textField.setMaximumSize(new Dimension(300, 40));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(textField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "TRIO - Nouveau joueur",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            return textField.getText().trim();
        }
        return "Joueur";
    }

    @Override
    public int promptGameMode() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Choisissez le mode de jeu:");
        label.setFont(new Font("SF Pro Text", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(20));

        final int[] choice = { 1 }; // Default Solo

        JButton soloBtn = createAppleButton("Solo (3-6 joueurs)", PRIMARY);
        soloBtn.addActionListener(e -> {
            choice[0] = 1;
            SwingUtilities.getWindowAncestor(soloBtn).dispose();
        });

        JButton teamBtn = createAppleButton("Équipe (4 ou 6 joueurs)", WARNING);
        teamBtn.addActionListener(e -> {
            choice[0] = 2;
            SwingUtilities.getWindowAncestor(teamBtn).dispose();
        });

        panel.add(soloBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(teamBtn);

        JDialog dialog = new JDialog(this, "TRIO - Mode de jeu", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return choice[0];
    }

    @Override
    public int promptPlayerCount(int gameMode) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String title = gameMode == 2 ? "Nombre de joueurs (Équipe):" : "Nombre de joueurs:";
        JLabel label = new JLabel(title);
        label.setFont(new Font("SF Pro Text", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(20));

        final int[] choice = { gameMode == 2 ? 4 : 3 };

        if (gameMode == 2) {
            // Team mode: only 4 or 6
            JButton btn4 = createAppleButton("4 joueurs (2 équipes)", PRIMARY);
            btn4.addActionListener(e -> {
                choice[0] = 4;
                SwingUtilities.getWindowAncestor(btn4).dispose();
            });

            JButton btn6 = createAppleButton("6 joueurs (3 équipes)", SUCCESS);
            btn6.addActionListener(e -> {
                choice[0] = 6;
                SwingUtilities.getWindowAncestor(btn6).dispose();
            });

            panel.add(btn4);
            panel.add(Box.createVerticalStrut(10));
            panel.add(btn6);
        } else {
            // Solo mode: 3-6
            for (int i = 3; i <= 6; i++) {
                final int count = i;
                JButton btn = createAppleButton(i + " joueurs", PRIMARY);
                btn.addActionListener(e -> {
                    choice[0] = count;
                    SwingUtilities.getWindowAncestor(btn).dispose();
                });
                panel.add(btn);
                panel.add(Box.createVerticalStrut(8));
            }
        }

        JDialog dialog = new JDialog(this, "TRIO - Configuration", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setSize(350, gameMode == 2 ? 220 : 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return choice[0];
    }

    @Override
    public void displayPlayersList(String[] playerNames, boolean[] isBot) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("La partie va commencer !");
        label.setFont(new Font("SF Pro Text", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < playerNames.length; i++) {
            String icon = isBot[i] ? "🤖" : "👤";
            String type = isBot[i] ? "BOT" : "VOUS";

            JPanel playerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            playerRow.setOpaque(false);
            playerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

            JLabel nameLabel = new JLabel(playerNames[i]);
            nameLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
            nameLabel.setForeground(TEXT_PRIMARY);

            JLabel typeLabel = new JLabel("(" + type + ")");
            typeLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
            typeLabel.setForeground(TEXT_SECONDARY);

            playerRow.add(iconLabel);
            playerRow.add(nameLabel);
            playerRow.add(typeLabel);
            panel.add(playerRow);
        }

        JOptionPane.showMessageDialog(
                this, panel, "TRIO - Joueurs",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(
                this, message, "TRIO",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Ferme la fenêtre du menu (appelé depuis le Main ou Controller)
     */
    public void close() {
        this.dispose();
    }
}
