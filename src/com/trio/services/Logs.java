package com.trio.services;

public class Logs {
    private static Logs instance;
    private String dernierLog;

    private Logs() {}

    public static Logs getInstance() {
        if (instance == null) instance = new Logs();
        return instance;
    }

    public void ecrireLog(String message) {
        this.dernierLog = message;
        System.out.println("[LOG] " + message);
    }
}