package com.example.demo.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Entite.Personne;
import CategorieEnum.Categorie;

/**
 * Interface distante RMI pour les opérations de l'annuaire.
 */
public interface RemoteAnnuaire extends Remote {
    // CRUD
    List<Personne> getAll() throws RemoteException;
    Personne getMembreById(int id) throws RemoteException;
    boolean ajouterMembre(Personne personne) throws RemoteException;
    boolean modifierMembre(Personne personne) throws RemoteException;
    boolean supprimerMembre(Personne personne) throws RemoteException;

    // Recherche
    Personne rechercherUnmembre(Personne personne) throws RemoteException;
    List<Personne> getMembresParCategorie(Categorie categorie) throws RemoteException;
    List<Personne> getProfesseursParDomaine(int domaine) throws RemoteException;

    // Liste rouge
    int gererListeRouge(int identifiant, String metSurLrouge) throws RemoteException;
    boolean ajouterAListeRouge(int id) throws RemoteException;
    boolean retirerDeListeRouge(int id) throws RemoteException;

    // Connexions
    boolean marquerUtilisateurConnecte(int idPersonne) throws RemoteException;
    boolean marquerUtilisateurDeconnecte(int idPersonne) throws RemoteException;
    boolean verifierEtatConnexion(int idPersonne) throws RemoteException;
    List<Personne> getUtilisateursConnectes() throws RemoteException;

    // Santé
    boolean ping() throws RemoteException;
}
