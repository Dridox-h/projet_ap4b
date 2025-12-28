package com.trio.services;

public class Logs {
    private static Logs instance;
    private String lastLogs;

    private Logs() {}

    public static Logs getInstance() {
        if (instance == null) instance = new Logs();
        return instance;
    }

    public void writeLogs(String message) {
        this.lastLogs = message;
        System.out.println("[LOG] " + message);
    }
}