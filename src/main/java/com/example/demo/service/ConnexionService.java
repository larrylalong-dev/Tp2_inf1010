package com.example.demo.service;

import java.sql.SQLException;
import java.util.List;

import com.example.demo.dao.PersonneDAO;
import com.example.demo.dao.PersonneDAOImpl;
import Entite.Personne;

public class ConnexionService {

    private PersonneDAO personneDAO;

    public ConnexionService() {
        this.personneDAO = new PersonneDAOImpl();
    }

    /**
     * Marque un utilisateur comme connecté
     */
    public boolean marquerUtilisateurConnecte(int idPersonne) {
        try {
            int resultat = personneDAO.marquerConnecte(idPersonne);
            return resultat > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marque un utilisateur comme déconnecté
     */
    public boolean marquerUtilisateurDeconnecte(int idPersonne) {
        try {
            int resultat = personneDAO.marquerDeconnecte(idPersonne);
            return resultat > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean verifierEtatConnexion(int idPersonne) {
        try {
            return personneDAO.verifierEtatConnexion(idPersonne);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère la liste des utilisateurs connectés
     */
    public List<Personne> getUtilisateursConnectes() {
        try {
            return personneDAO.getPersonnesConnectees();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Déconnecte tous les utilisateurs (utile au démarrage de l'application)
     */
    public void deconnecterTousLesUtilisateurs() {
        try {
            List<Personne> tousLesUtilisateurs = personneDAO.getAll();
            for (Personne personne : tousLesUtilisateurs) {
                if (personne.isEstConnecte()) {
                    personneDAO.marquerDeconnecte(personne.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
