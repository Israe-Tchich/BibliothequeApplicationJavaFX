package model;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private String categorie;
    private String isbn;
    private int annee;
    private boolean disponible;
    private String description;
    private int nbExemplaires;

    public Livre() {}

    public Livre(String titre, String auteur, String categorie, String isbn,
                 int annee, boolean disponible, String description, int nbExemplaires) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.isbn = isbn;
        this.annee = annee;
        this.disponible = disponible;
        this.description = description;
        this.nbExemplaires = nbExemplaires;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getNbExemplaires() { return nbExemplaires; }
    public void setNbExemplaires(int nbExemplaires) { this.nbExemplaires = nbExemplaires; }

    @Override
    public String toString() { return titre + " - " + auteur; }
}
