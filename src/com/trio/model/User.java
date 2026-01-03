package com.trio.model;

public class User extends Player {

    private static int nextId = 1;
    private int id;
    private int age;
    private int nbre_victoire;
    private String path_to_avatar;

    // Constructor for new users (auto-generates ID)
    public User(String pseudo, int age, int victories, String path_to_avatar) {
        super(pseudo);
        this.id = nextId++;
        this.age = age;
        this.nbre_victoire = victories;
        this.path_to_avatar = path_to_avatar;
    }

    // Constructor for loading existing users from logs
    public User(int id, String pseudo, int age, int victories, String path_to_avatar) {
        super(pseudo);
        this.id = id;
        this.age = age;
        this.nbre_victoire = victories;
        this.path_to_avatar = path_to_avatar;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Keep backward compatibility with simple constructor
    public User(String pseudo) {
        this(pseudo, 0, 0, "");
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public int getNBVictoire() {
        return nbre_victoire;
    }

    public String getPathAvatar() {
        return path_to_avatar;
    }

    public String getName() {
        return getPseudo();
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setNBVictoire(int nbre_victoire) {
        this.nbre_victoire = nbre_victoire;
    }

    public void addVictoire() {
        this.nbre_victoire += 1;
    }

    public void setPathAvatar(String path_to_avatar) {
        this.path_to_avatar = path_to_avatar;
    }

    // Méthodes Métier
    @Override
    public String chooseAction(Game game) {
        return "HUMAN_INPUT";
    }
}