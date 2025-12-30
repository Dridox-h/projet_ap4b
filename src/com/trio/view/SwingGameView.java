package com.trio.view;

import com.trio.model.*;
import com.trio.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface graphique Swing pour le jeu Trio.
 * Implémente GameView avec une fenêtre graphique.
 */
public class SwingGameView extends JFrame implements GameView {

    // Panels principaux
    private JPanel mainPanel;
    private JTextArea gameLog;
    private JPanel handPanel;
    private JPanel visibleCardsPanel;
    private JPanel actionsPanel;
    private JLabel statusLabel;

    // Pour les inputs
    private int selectedAction = -1;
    private Player selectedPlayer = null;
    private int selectedCenterIndex = -1;
    private final Object inputLock = new Object();

    // Références
    private List<Player> currentPlayers;
    private Deck currentCenterDeck;

    public SwingGameView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("🎮 TRIO - Le Jeu de Cartes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Panel principal avec BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(34, 40, 49));

        // Header avec status
        statusLabel = new JLabel("Bienvenue dans TRIO!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(new Color(0, 173, 181));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        // Centre: Log du jeu
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        gameLog.setBackground(new Color(57, 62, 70));
        gameLog.setForeground(Color.WHITE);
        gameLog.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(gameLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 173, 181)),
                "📜 Historique", 0, 0,
                new Font("Arial", Font.BOLD, 12), new Color(0, 173, 181)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bas: Main du joueur
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        handPanel.setBackground(new Color(34, 40, 49));
        handPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(238, 238, 238)),
                "🃏 Votre Main", 0, 0,
                new Font("Arial", Font.BOLD, 12), Color.WHITE));
        handPanel.setPreferredSize(new Dimension(0, 100));
        mainPanel.add(handPanel, BorderLayout.SOUTH);

        // Droite: Cartes visibles
        visibleCardsPanel = new JPanel();
        visibleCardsPanel.setLayout(new BoxLayout(visibleCardsPanel, BoxLayout.Y_AXIS));
        visibleCardsPanel.setBackground(new Color(34, 40, 49));
        visibleCardsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 211, 105)),
                "👁️ Cartes Visibles", 0, 0,
                new Font("Arial", Font.BOLD, 12), new Color(255, 211, 105)));
        visibleCardsPanel.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(visibleCardsPanel, BorderLayout.EAST);

        // Gauche: Actions
        actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(new Color(34, 40, 49));
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 173, 181)),
                "⚡ Actions", 0, 0,
                new Font("Arial", Font.BOLD, 12), new Color(0, 173, 181)));
        actionsPanel.setPreferredSize(new Dimension(250, 0));
        mainPanel.add(actionsPanel, BorderLayout.WEST);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createCardButton(int value, boolean visible) {
        String text = visible ? String.valueOf(value) : "?";
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(50, 70));
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        if (visible) {
            btn.setBackground(new Color(0, 173, 181));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(100, 100, 100));
            btn.setForeground(Color.GRAY);
        }
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        return btn;
    }

    private JButton createActionButton(String text, int actionCode) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(0, 173, 181));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            synchronized (inputLock) {
                selectedAction = actionCode;
                inputLock.notifyAll();
            }
        });
        return btn;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            gameLog.append(message + "\n");
            gameLog.setCaretPosition(gameLog.getDocument().getLength());
        });
    }

    // === IMPLÉMENTATION GameView ===

    @Override
    public void displayWelcome(int nbPlayers) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🎮 Partie avec " + nbPlayers + " joueurs!");
        });
        log("=== DÉBUT DE LA PARTIE TRIO ===");
        log("Nombre de joueurs: " + nbPlayers);
        log("Objectif: 3 trios pour gagner\n");
    }

    @Override
    public void displayTurnStart(Player player) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🎯 Tour de " + player.getPseudo());
        });
        log("\n═══════════════════════════════════");
        log("   Tour de " + player.getPseudo());
        log("═══════════════════════════════════");
    }

    @Override
    public void displayPlayerHand(Player player) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            for (Card c : player.getDeck().getCards()) {
                // Les cartes visibles (révélées) sont grisées
                JButton cardBtn = createCardButton(c.getValue(), !c.isVisible());
                if (c.isVisible()) {
                    // Carte déjà révélée: grisée et barrée
                    cardBtn.setEnabled(false);
                    cardBtn.setToolTipText("Carte déjà révélée");
                }
                handPanel.add(cardBtn);
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
            visibleCardsPanel.removeAll();

            for (Player p : players) {
                for (Card c : p.getDeck().getCards()) {
                    if (c.isVisible()) {
                        JLabel label = new JLabel(p.getPseudo() + ": [" + c.getValue() + "]");
                        label.setForeground(Color.WHITE);
                        label.setFont(new Font("Arial", Font.PLAIN, 12));
                        label.setAlignmentX(Component.LEFT_ALIGNMENT);
                        visibleCardsPanel.add(label);
                    }
                }
            }

            if (centerDeck != null) {
                for (Card c : centerDeck.getCards()) {
                    if (c.isVisible()) {
                        JLabel label = new JLabel("Centre: [" + c.getValue() + "]");
                        label.setForeground(new Color(255, 211, 105));
                        label.setFont(new Font("Arial", Font.PLAIN, 12));
                        label.setAlignmentX(Component.LEFT_ALIGNMENT);
                        visibleCardsPanel.add(label);
                    }
                }
            }

            if (visibleCardsPanel.getComponentCount() == 0) {
                JLabel label = new JLabel("Aucune");
                label.setForeground(Color.GRAY);
                visibleCardsPanel.add(label);
            }

            visibleCardsPanel.revalidate();
            visibleCardsPanel.repaint();
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
        String source;
        if (owner != null) {
            source = owner.getPseudo();
        } else {
            source = "Centre" + (cardIndex >= 0 ? " (Carte n°" + (cardIndex + 1) + ")" : "");
        }

        if (isFirst) {
            log("✓ Première carte révélée: [" + card.getValue() + "] de " + source);
        } else if (isCorrect) {
            log("✓ Bonne carte! [" + card.getValue() + "] de " + source);
        } else {
            log("❌ Mauvaise carte! Attendu: " + expectedValue + ", Reçu: " + card.getValue() + " de " + source);
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
        log("\n❌ Échec du tour. Les cartes sont remises face cachée.");
    }

    @Override
    public void displayGameWinner(Player winner) {
        log("\n🎉 " + winner.getPseudo() + " GAGNE avec " + winner.getTrioCount() + " trios!");
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("🏆 " + winner.getPseudo() + " GAGNE!");
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
            actionsPanel.add(Box.createVerticalStrut(10));
            actionsPanel.add(createActionButton("1. Ma carte MIN", 1));
            actionsPanel.add(Box.createVerticalStrut(5));
            actionsPanel.add(createActionButton("2. Ma carte MAX", 2));
            actionsPanel.add(Box.createVerticalStrut(5));
            actionsPanel.add(createActionButton("3. MIN autre joueur", 3));
            actionsPanel.add(Box.createVerticalStrut(5));
            actionsPanel.add(createActionButton("4. MAX autre joueur", 4));
            actionsPanel.add(Box.createVerticalStrut(5));
            actionsPanel.add(createActionButton("5. Carte du centre", 5));
            actionsPanel.add(Box.createVerticalStrut(15));
            actionsPanel.add(createActionButton("0. Arrêter le tour", 0));
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

    @Override
    public Player promptSelectPlayer(List<Player> availablePlayers) {
        selectedPlayer = null;

        SwingUtilities.invokeLater(() -> {
            actionsPanel.removeAll();
            actionsPanel.add(Box.createVerticalStrut(10));
            JLabel label = new JLabel("Choisir un joueur:");
            label.setForeground(Color.WHITE);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsPanel.add(label);
            actionsPanel.add(Box.createVerticalStrut(10));

            for (Player p : availablePlayers) {
                JButton btn = new JButton(p.getPseudo() + " (" + p.getDeck().getSize() + " cartes)");
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(220, 35));
                btn.setBackground(new Color(0, 173, 181));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    synchronized (inputLock) {
                        selectedPlayer = p;
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
            actionsPanel.add(Box.createVerticalStrut(10));
            JLabel label = new JLabel("Choisir une carte du centre:");
            label.setForeground(Color.WHITE);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsPanel.add(label);
            actionsPanel.add(Box.createVerticalStrut(10));

            for (int i = 0; i < centerDeck.getSize(); i++) {
                Card c = centerDeck.getCard(i);
                String text = (i + 1) + ". " + (c.isVisible() ? "[" + c.getValue() + "]" : "[?]");
                final int index = i;
                JButton btn = new JButton(text);
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(220, 35));
                btn.setBackground(c.isVisible() ? new Color(255, 211, 105) : new Color(100, 100, 100));
                btn.setForeground(Color.BLACK);
                btn.addActionListener(e -> {
                    synchronized (inputLock) {
                        selectedCenterIndex = index;
                        inputLock.notifyAll();
                    }
                });
                actionsPanel.add(btn);
                actionsPanel.add(Box.createVerticalStrut(3));
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

    /**
     * Affiche un dialogue pour entrer le pseudo
     */
    public String promptPseudo() {
        return JOptionPane.showInputDialog(this,
                "Entrez votre pseudo:", "TRIO - Nouveau joueur", JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Affiche un dialogue pour choisir le nombre de joueurs
     */
    public int promptPlayerCount() {
        String[] options = { "3", "4", "5", "6" };
        int choice = JOptionPane.showOptionDialog(this,
                "Nombre de joueurs:", "TRIO - Configuration",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return choice >= 0 ? Integer.parseInt(options[choice]) : 3;
    }
}
