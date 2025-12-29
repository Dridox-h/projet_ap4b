package com.trio.model;

public class Card {

    // Attributs
    private int value;
    private String coordinate;
    private String pathImage;
    private boolean isVisible;

    // Constructeurs
    public Card(int value, String coordinate, String pathImage) {
        this.value = value;
        this.coordinate = coordinate;
        this.pathImage = pathImage;
        this.isVisible = false;
    }

    // Getters
    public int getValue() {
        return value;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getPathImage() {
        return pathImage;
    }

    public boolean isVisible() {
        return isVisible;
    }

    // Setters
    public void setValue(int value) {
        this.value = value;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    // Méthodes Métier
    public void setVisible() {
        this.isVisible = true;
    }

    public void setInvisible() {
        this.isVisible = false;
    }

    // toString
    @Override
    public String toString() {
        return isVisible ? "[" + value + "]" : "[?]";
    }
}