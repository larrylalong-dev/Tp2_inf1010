package Entite;

import CategorieEnum.Categorie;
import java.text.Normalizer;

public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String matricule;
    private String telephone;
    private String adresseCourriel;
    private String domaineActivite;
    private String motDePasse;
    private Categorie categorie;
    private boolean listeRouge;
    private boolean estConnecte; // Nouveau champ pour gérer l'état de connexion

    //  Constructeur vide (OBLIGATOIRE pour le DAO)
    public Personne() {
    }

    public Personne(int id, String nom, String prenom, String matricule, String telephone,String adresseCourriel,
            String domaineActivite, String motDePasse, Categorie categorie, boolean listeRouge) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.telephone = telephone;
        this.adresseCourriel = adresseCourriel;
        this.domaineActivite = domaineActivite;
        this.motDePasse = motDePasse;
        this.categorie = categorie;
        this.listeRouge = listeRouge;
    }

    // Constructeur sans ID (pour les insertions)
    public Personne(String nom, String prenom, String matricule, String telephone,
                    String adresseCourriel, String domaineActivite, String motDePasse,
                    Categorie categorie, boolean listeRouge) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.telephone = telephone;
        this.adresseCourriel = adresseCourriel;
        this.domaineActivite = domaineActivite;
        this.motDePasse = motDePasse;
        this.categorie = categorie;
        this.listeRouge = listeRouge;
    }

    // Constructeur avec le nouveau champ estConnecte
    public Personne(int id, String nom, String prenom, String matricule, String telephone, String adresseCourriel,
            String domaineActivite, String motDePasse, Categorie categorie, boolean listeRouge, boolean estConnecte) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.telephone = telephone;
        this.adresseCourriel = adresseCourriel;
        this.domaineActivite = domaineActivite;
        this.motDePasse = motDePasse;
        this.categorie = categorie;
        this.listeRouge = listeRouge;
        this.estConnecte = estConnecte;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters et Setters
    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = supprimerAccents(nom);
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = supprimerAccents(prenom);
    }

    public String getMatricule() {
        return this.matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = supprimerAccents(matricule);
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = supprimerAccents(telephone);
    }

    public String getAdresseCourriel() {
       return this.adresseCourriel;
    }

    public void setAdresseCourriel(String adresseCourriel) {
        this.adresseCourriel = supprimerAccents(adresseCourriel);
    }

    public String getDomaineActivite() {
        return this.domaineActivite;
    }

    public void setDomaineActivite(String domaineActivite) {
        this.domaineActivite = supprimerAccents(domaineActivite);
    }

    public String getMotDePasse() {
        return this.motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = supprimerAccents(motDePasse);
    }

    public Categorie getCategorie() {
        return this.categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public boolean isListeRouge() {
        return this.listeRouge;
    }

    public void setListeRouge(boolean listeRouge) {
        this.listeRouge = listeRouge;
    }

    // Getter et setter pour estConnecte
    public boolean isEstConnecte() {
        return estConnecte;
    }

    public void setEstConnecte(boolean estConnecte) {
        this.estConnecte = estConnecte;
    }

    // Méthode pour compatibilité avec le champ "informations" utilisé dans le contrôleur
    public String getInformations() {
        return this.motDePasse; // Ou vous pouvez créer un nouveau champ si nécessaire
    }

    public void setInformations(String informations) {
        this.motDePasse = supprimerAccents(informations); // Ou vous pouvez créer un nouveau champ si nécessaire
    }

    // Méthode utilitaire pour supprimer les accents
    private String supprimerAccents(String texte) {
        if (texte == null || texte.isEmpty()) {
            return texte;
        }
        // Normalise le texte en décomposant les caractères accentués
        String normalise = Normalizer.normalize(texte, Normalizer.Form.NFD);
        // Supprime tous les caractères diacritiques (accents)
        return normalise.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + this.id +
                ", nom='" + this.nom + '\'' +
                ", prenom='" + this.prenom + '\'' +
                ", matricule='" + this.matricule + '\'' +
                ", telephone='" + this.telephone + '\'' +
                ", adresseCourriel='" + this.adresseCourriel + '\'' +
                ", domaineActivite='" + this.domaineActivite + '\'' +
                ", motDePasse='" + this.motDePasse + '\'' +
                ", categorie='" + this.categorie + '\'' +
                ", listeRouge=" + this.listeRouge +
                '}';
    }
}
