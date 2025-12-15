package model;

public class User {
    private String name;
    private int age;
    private int nbre_victoire;
    private String path_to_avatar;

    public User(String name, int age, int victories, String path_to_avatar) {
        this.name = name;
        this.age = age;
        this.nbre_victoire = victories;
        this.path_to_avatar = path_to_avatar;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
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

}