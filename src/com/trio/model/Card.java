package com.trio.model;

public class Card {
    private int value;
    private String pathImage;
    private boolean isVisible;

    public Card(int value) {
        this.value = value;
        // Format demandé: Images[i]
        this.pathImage = "Image" + value + ".jpg";
        this.isVisible = false;
    }

    public int getvalue() { return value; }

    public String getpathImage() { return pathImage; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }

    @Override
    public String toString() {
        return isVisible ? "[" + value + "]" : "[?]";
    }
}
