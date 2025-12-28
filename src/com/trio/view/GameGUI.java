package com.trio.view;

import javax.swing.*;
import java.awt.*;
import com.trio.model.*;

public class GameGUI extends JFrame {
    private JLabel tourLabel;
    private JTextArea logArea;
    private JPanel plateauCentral;

    private static final Color BACKGROUND_DARK = new Color(18, 27, 40);
    private static final Color BACKGROUND_MEDIUM = new Color(28, 40, 58);
    private static final Color ACCENT_BLUE = new Color(129, 182, 232);

    public GameGUI(int nbTotal, int nbBots) {
        setTitle("TRIO - Plateau");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(BACKGROUND_MEDIUM);
        header.setPreferredSize(new Dimension(0, 80));
        tourLabel = new JLabel("Initialisation...");
        tourLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tourLabel.setForeground(ACCENT_BLUE);
        header.add(tourLabel);
        add(header, BorderLayout.NORTH);

        // Plateau (Grille)
        plateauCentral = new JPanel(new GridLayout(3, 4, 20, 20));
        plateauCentral.setBackground(BACKGROUND_DARK);
        plateauCentral.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(plateauCentral, BorderLayout.CENTER);

        // Sidebar Logs
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(BACKGROUND_MEDIUM);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.DARK_GRAY));

        logArea = new JTextArea();
        logArea.setBackground(BACKGROUND_MEDIUM);
        logArea.setForeground(Color.LIGHT_GRAY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JLabel logTitle = new JLabel(" HISTORIQUE", SwingConstants.LEFT);
        logTitle.setForeground(ACCENT_BLUE);
        logTitle.setPreferredSize(new Dimension(0, 40));

        sidebar.add(logTitle, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);
    }

    public void ajouterLog(String msg) {
        logArea.append("> " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void setTourLabel(String texte) {
        tourLabel.setText(texte.toUpperCase());
    }

    public void afficherPlateau(Game game) {
        plateauCentral.removeAll();
        for (Card c : game.getCardCenter().getCartes()) {
            JPanel card = createCardView(c);
            plateauCentral.add(card);
        }
        plateauCentral.revalidate();
        plateauCentral.repaint();
    }

    private JPanel createCardView(Card c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(100, 140));

        if (c.isVisible()) {
            p.setBackground(ACCENT_BLUE);
            JLabel val = new JLabel(String.valueOf(c.getValeur()), SwingConstants.CENTER);
            val.setFont(new Font("Segoe UI", Font.BOLD, 30));
            val.setForeground(BACKGROUND_DARK);
            p.add(val, BorderLayout.CENTER);
        } else {
            p.setBackground(new Color(44, 62, 80));
            JLabel secret = new JLabel("?", SwingConstants.CENTER);
            secret.setFont(new Font("Segoe UI", Font.BOLD, 30));
            secret.setForeground(new Color(60, 80, 100));
            p.add(secret, BorderLayout.CENTER);
        }
        p.setBorder(BorderFactory.createLineBorder(new Color(20, 30, 45), 2));
        return p;
    }

    // Méthodes requises par le Controller
    public void afficherMessage(String msg) { ajouterLog(msg); }
    public void afficherMainJoueurActif(Player j) { ajouterLog("Main affichée en console pour " + j.getPseudo()); }
    public int demanderEntier(String msg, int min, int max) {
        String res = (String)JOptionPane.showInputDialog(this, msg, "Action", JOptionPane.QUESTION_MESSAGE, null, null, null);
        try { return Integer.parseInt(res); } catch (Exception e) { return min; }
    }
}