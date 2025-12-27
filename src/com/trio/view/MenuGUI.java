package com.trio.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuGUI extends JFrame {
    private JSlider totalSlider;
    private JSlider humainSlider;
    private JButton playButton;
    private boolean isReady = false;

    // Couleurs de ton thème
    private static final Color BACKGROUND_DARK = new Color(18, 27, 40);
    private static final Color BACKGROUND_MEDIUM = new Color(28, 40, 58);
    private static final Color ACCENT_BLUE = new Color(129, 182, 232);
    private static final Color TEXT_WHITE = new Color(235, 240, 245);

    public MenuGUI() {
        setTitle("TRIO - Configuration");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Titre stylisé
        JLabel title = new JLabel("TRIO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 60));
        title.setForeground(ACCENT_BLUE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Configuration Sliders
        totalSlider = createStyledSlider(3, 6, 3);
        humainSlider = createStyledSlider(1, 6, 1);

        addSection(mainPanel, "NOMBRE TOTAL DE JOUEURS", totalSlider);
        addSection(mainPanel, "NOMBRE DE JOUEURS HUMAINS", humainSlider);

        // Bouton Jouer
        playButton = new JButton("LANCER LA PARTIE");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setBackground(ACCENT_BLUE);
        playButton.setForeground(BACKGROUND_DARK);
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        playButton.addActionListener(e -> {
            if(humainSlider.getValue() > totalSlider.getValue()) {
                JOptionPane.showMessageDialog(this, "Il ne peut pas y avoir plus d'humains que de joueurs au total !");
            } else {
                isReady = true;
                this.setVisible(false);
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(playButton);

        add(mainPanel);
    }

    private JSlider createStyledSlider(int min, int max, int val) {
        JSlider slider = new JSlider(min, max, val);
        slider.setBackground(BACKGROUND_DARK);
        slider.setForeground(ACCENT_BLUE);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return slider;
    }

    private void addSection(JPanel panel, String title, JSlider slider) {
        JLabel lbl = new JLabel(title);
        lbl.setForeground(TEXT_WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(slider);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    public int getTotal() { return totalSlider.getValue(); }
    public int getHumains() { return humainSlider.getValue(); }
    public boolean isReady() { return isReady; }
}