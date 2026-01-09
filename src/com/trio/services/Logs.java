package com.trio.services;

public class Logs {
    private static Logs instance;

    private Logs() {
    }

    public static Logs getInstance() {
        if (instance == null)
            instance = new Logs();
        return instance;
    }

    public void writeLogs(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'writeLogs'");
    }

}