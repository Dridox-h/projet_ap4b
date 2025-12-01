package carte;
public class Carte {
    private String email;

    private String pathtoimage;

    private int id_carte;

    private int valeur;

    // setter et getter

    public int getId_carte(){return id_carte;}
    public int getValeur(){return valeur;}
    public String getEmail(){return email;}
    public String getPathToImage(){return pathtoimage;}
    public void setId_carte(int id_carte){this.id_carte = id_carte;}
    public void setValeur(int valeur){this.valeur = valeur;}
    public void setEmail(String email){this.email = email;}
    public void setPathToImage(String pathtoimage){this.pathtoimage = pathtoimage;}

    // constructeur

    public Carte(int id_carte, int valeur, String email, String pathtoimage){
        this.id_carte = id_carte;
        this.valeur = valeur;
        this.email = email;
        this.pathtoimage = pathtoimage;
    }
}
