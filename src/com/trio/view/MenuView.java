package com.trio.view;

public class MenuView {
    private MenuGUI gui;

    public MenuView() {
        this.gui = new MenuGUI();
    }

    public void afficherMenuPrincipal() {
        gui.setVisible(true);
        // On attend que l'utilisateur clique sur le bouton
        while (!gui.isReady()) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    public int demanderNombreJoueurs() { return gui.getTotal(); }
    public int demanderNombreHumains() { return gui.getHumains(); }
}