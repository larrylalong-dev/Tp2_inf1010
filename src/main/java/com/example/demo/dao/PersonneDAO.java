package com.example.demo.dao;

import java.sql.SQLException;
import java.util.List;

import Entite.Personne;
import CategorieEnum.Categorie;

public interface PersonneDAO {

    // CRUD operations
    List<Personne> getAll() throws SQLException;
    Personne getMembreById(int id) throws SQLException;
    int ajouterMembre(Personne personne) throws SQLException;
    int modifierMembre(Personne personne) throws SQLException;
    int supprimerMembre(Personne personne) throws SQLException;

    // Search operations
    Personne rechercherUnmembre(Personne personne) throws SQLException;
    List<Personne> getMembresParCategorie(Categorie categorie) throws SQLException;
    List<Personne> getProfesseursParDomaine(int domaine) throws SQLException;

    // Liste rouge management
    int gererListeRouge(int identifiant, String metSurLrouge) throws SQLException;

    // Gestion de l'état de connexion
    int marquerConnecte(int idPersonne) throws SQLException;
    int marquerDeconnecte(int idPersonne) throws SQLException;
    List<Personne> getPersonnesConnectees() throws SQLException;
    boolean verifierEtatConnexion(int idPersonne) throws SQLException;
}
