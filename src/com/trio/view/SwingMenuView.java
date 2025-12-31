package com.trio.view;

import javax.swing.*;
import java.awt.*;

/**
 * Implémentation Swing de MenuView.
 * Affiche les dialogues du menu dans une interface graphique.
 */
public class SwingMenuView extends JFrame implements MenuView {

    public SwingMenuView() {
        setTitle("🎮 TRIO - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(34, 40, 49));

        // Panel de bienvenue
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(new Color(34, 40, 49));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("🎮 TRIO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(0, 173, 181));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Le jeu de cartes");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(subtitleLabel);
        welcomePanel.add(Box.createVerticalGlue());

        add(welcomePanel);
        setVisible(true);
    }

    @Override
    public void displayWelcome() {
        // La fenêtre est déjà affichée avec le titre
    }

    @Override
    public String promptPseudo() {
        String pseudo = (String) JOptionPane.showInputDialog(
                this,
                "Entrez votre pseudo:",
                "🎮 TRIO - Nouveau joueur",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "Joueur");
        return (pseudo == null || pseudo.trim().isEmpty()) ? "Joueur" : pseudo.trim();
    }

    @Override
    public int promptGameMode() {
        String[] options = { "Solo (3-6 joueurs)", "Équipe (4 ou 6 joueurs)" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choisissez le mode de jeu:",
                "🎮 TRIO - Mode de jeu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return choice >= 0 ? choice + 1 : 1; // 1 = Solo, 2 = Équipe
    }

    @Override
    public int promptPlayerCount(int gameMode) {
        if (gameMode == 2) {
            // Mode Équipe: seulement 4 ou 6 joueurs
            String[] options = { "4 joueurs (2 équipes)", "6 joueurs (3 équipes)" };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Nombre de joueurs (mode Équipe):",
                    "🎮 TRIO - Configuration",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            return choice == 1 ? 6 : 4; // 0 = 4 joueurs, 1 = 6 joueurs
        } else {
            // Mode Solo: 3 à 6 joueurs
            String[] options = { "3 joueurs", "4 joueurs", "5 joueurs", "6 joueurs" };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Nombre de joueurs:",
                    "🎮 TRIO - Configuration",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            return choice >= 0 ? choice + 3 : 3;
        }
    }

    @Override
    public void displayPlayersList(String[] playerNames, boolean[] isBot) {
        StringBuilder sb = new StringBuilder("<html><b>Joueurs créés:</b><br>");
        for (int i = 0; i < playerNames.length; i++) {
            String type = isBot[i] ? "🤖 BOT" : "👤 HUMAIN";
            sb.append("• ").append(playerNames[i]).append(" (").append(type).append(")<br>");
        }
        sb.append("</html>");

        JOptionPane.showMessageDialog(
                this,
                sb.toString(),
                "🎮 TRIO - Joueurs",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "🎮 TRIO",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "⚠️ Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Ferme la fenêtre du menu
     */
    public void close() {
        dispose();
    }
}
