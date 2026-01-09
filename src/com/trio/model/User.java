package com.trio.model;

public class User extends Player {

    private int id;
    private int age;
    private String avatarPath;
    private int nbVictoire;

    private static int nextId = 1;

    // Constructeurs
    public User(String pseudo) {
        super(pseudo);
        this.id = nextId++;
        this.age = 0;
        this.avatarPath = "";
        this.nbVictoire = 0;
    }

    public User(String pseudo, int age) {
        super(pseudo);
        this.id = nextId++;
        this.age = age;
        this.avatarPath = "";
        this.nbVictoire = 0;
    }

    public User(String pseudo, int age, String avatarPath) {
        super(pseudo);
        this.id = nextId++;
        this.age = age;
        this.avatarPath = avatarPath != null ? avatarPath : "";
        this.nbVictoire = 0;
    }

    public User(int id, String pseudo, int age, int nbVictoire) {
        super(pseudo);
        this.id = id;
        this.age = age;
        this.avatarPath = "";
        this.nbVictoire = nbVictoire;
        // Update nextId if necessary
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public String getName() {
        return pseudo;
    }

    public int getAge() {
        return age;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public int getNBVictoire() {
        return nbVictoire;
    }

    // Setters
    public void setAge(int age) {
        this.age = age;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setNbVictoire(int nbVictoire) {
        this.nbVictoire = nbVictoire;
    }

    public void addVictory() {
        this.nbVictoire++;
    }

    // Méthodes Métier
    @Override
    public String chooseAction(Game game) {
        return "HUMAN_INPUT";
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name=%s, age=%d, victories=%d]", id, pseudo, age, nbVictoire);
    }
}