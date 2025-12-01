package com.example.demo.service;

import com.example.demo.dao.PersonneDAO;
import com.example.demo.dao.PersonneDAOImpl;
import Entite.Personne;
import CategorieEnum.Categorie;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Service class pour gérer les opérations sur les personnes
 * Fait le pont entre les contrôleurs JavaFX et la couche DAO
 */
public class PersonneService {

    private final PersonneDAO personneDAO;

    public PersonneService() {
        this.personneDAO = new PersonneDAOImpl();
    }

    /**
     * Récupère tous les membres de la base de données
     * @return Liste de toutes les personnes
     */
    public List<Personne> getAllMembres() {
        try {
            return personneDAO.getAll();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les membres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les membres d'une catégorie spécifique
     * @param categorie La catégorie recherchée
     * @return Liste des personnes de cette catégorie
     */
    public List<Personne> getMembresParCategorie(Categorie categorie) {
        try {
            return personneDAO.getMembresParCategorie(categorie);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des membres par catégorie: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ajoute un nouveau membre
     * @param personne La personne à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterMembre(Personne personne) {
        try {
            int result = personneDAO.ajouterMembre(personne);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du membre: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifie un membre existant
     * @param personne La personne à modifier
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifierMembre(Personne personne) {
        try {
            int result = personneDAO.modifierMembre(personne);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du membre: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un membre
     * @param personne La personne à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerMembre(Personne personne) {
        try {
            int result = personneDAO.supprimerMembre(personne);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du membre: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recherche un membre par ses informations
     * @param personne La personne avec les critères de recherche
     * @return La personne trouvée ou null
     */
    public Personne rechercherUnmembre(Personne personne) {
        try {
            return personneDAO.rechercherUnmembre(personne);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du membre: " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère un membre par son ID
     * @param id L'identifiant du membre
     * @return La personne trouvée ou null
     */
    public Personne getMembreById(int id) {
        try {
            return personneDAO.getMembreById(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du membre par ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ajoute un membre à la liste rouge
     * @param id L'identifiant du membre
     * @return true si l'opération a réussi, false sinon
     */
    public boolean ajouterAListeRouge(int id) {
        try {
            int result = personneDAO.gererListeRouge(id, "oui");
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout à la liste rouge: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retire un membre de la liste rouge
     * @param id L'identifiant du membre
     * @return true si l'opération a réussi, false sinon
     */
    public boolean retirerDeListeRouge(int id) {
        try {
            int result = personneDAO.gererListeRouge(id, "non");
            return result >= 0; // 0 peut être retourné pour "retirer"
        } catch (SQLException e) {
            System.err.println("Erreur lors du retrait de la liste rouge: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gère l'ajout/retrait d'un membre de la liste rouge
     * @param identifiant L'identifiant du membre
     * @param metSurLrouge "oui" pour ajouter, "non" pour retirer
     * @return le résultat de l'opération
     */
    public int gererListeRouge(int identifiant, String metSurLrouge) {
        try {
            return personneDAO.gererListeRouge(identifiant, metSurLrouge);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la gestion de la liste rouge: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Récupère les professeurs par domaine d'activité
     * @param domaine Le domaine d'activité
     * @return Liste des professeurs du domaine
     */
    public List<Personne> getProfesseursParDomaine(int domaine) {
        try {
            return personneDAO.getProfesseursParDomaine(domaine);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des professeurs par domaine: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Convertit une chaîne en énumération Categorie
     * @param categorieStr La chaîne représentant la catégorie
     * @return L'énumération Categorie correspondante
     */
    public Categorie stringToCategorie(String categorieStr) {
        if (categorieStr == null || categorieStr.trim().isEmpty()) {
            return null;
        }

        try {
            return switch (categorieStr.toLowerCase().trim()) {
                case "professeur" -> Categorie.professeur;
                case "étudiant", "etudiant" -> Categorie.etudiant;
                case "auxiliaire", "auxiliaire d'enseignement" -> Categorie.auxiliaire;
                case "administrateur" -> Categorie.administrateur;
                default -> null;
            };
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion de la catégorie: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convertit une énumération Categorie en chaîne lisible
     * @param categorie L'énumération Categorie
     * @return La chaîne lisible correspondante
     */
    public String categorieToString(Categorie categorie) {
        if (categorie == null) {
            return "";
        }

        return switch (categorie) {
            case professeur -> "Professeur";
            case etudiant -> "Étudiant";
            case auxiliaire -> "Auxiliaire d'enseignement";
            case administrateur -> "Administrateur";
        };
    }
}
