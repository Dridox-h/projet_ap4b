package carte;
public class Carte {
    private String email;

    private String pathtoimage;

    private int id_carte;

    public Carte(String nom) {
        this.email = nom;
    }

    public String getNom() {
        return email;
    }
}
