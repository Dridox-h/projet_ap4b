package com.trio.model;

public class Card {
    private int value;
    private String pathImage;
    private boolean isVisible;

    public Card(int value) {
        this.value = value;
        this.pathImage = "Image" + value + ".jpg";
        this.isVisible = false;
    }

    public int getValue() { 
        return value; 
    }

    public String getPathImage() { 
        return pathImage; 
    }

    public boolean isVisible() { 
        return isVisible; 
    }

    public void setVisible(boolean visible) { 
        this.isVisible = visible; 
    }

    @Override
    public String toString() {
        return isVisible ? "[" + value + "]" : "[?]";
    }
}