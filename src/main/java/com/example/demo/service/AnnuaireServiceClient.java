package com.example.demo.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import Entite.Personne;
import CategorieEnum.Categorie;
import com.example.demo.client.ServerConnectionManager;
import com.example.demo.server.RemoteAnnuaire;

/**
 * Service client qui utilise RMI pour communiquer avec le serveur.
 * Remplace l'utilisation directe de PersonneService côté client.
 */
public class AnnuaireServiceClient {

    private final ServerConnectionManager connectionManager;

    public AnnuaireServiceClient() {
        this.connectionManager = ServerConnectionManager.getInstance();
    }

    /**
     * Vérifie si le serveur est disponible et connecté
     */
    public boolean isServerAvailable() {
        if (!connectionManager.isConnected()) {
            return connectionManager.connect();
        }
        return connectionManager.pingServer();
    }

    /**
     * Récupère le stub RMI avec vérification de connexion
     */
    private RemoteAnnuaire getStub() throws RemoteException {
        if (!connectionManager.isConnected()) {
            if (!connectionManager.connect()) {
                throw new RemoteException("Impossible de se connecter au serveur");
            }
        }
        RemoteAnnuaire stub = connectionManager.getStub();
        if (stub == null) {
            throw new RemoteException("Stub RMI non disponible");
        }
        return stub;
    }

    // ========== CRUD OPERATIONS ==========

    public List<Personne> getAllMembres() {
        try {
            return getStub().getAll();
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération de tous les membres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Personne getMembreById(int id) {
        try {
            return getStub().getMembreById(id);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération du membre #" + id + ": " + e.getMessage());
            return null;
        }
    }

    public boolean ajouterMembre(Personne personne) {
        try {
            return getStub().ajouterMembre(personne);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de l'ajout d'un membre: " + e.getMessage());
            return false;
        }
    }

    public boolean modifierMembre(Personne personne) {
        try {
            return getStub().modifierMembre(personne);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la modification du membre: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerMembre(Personne personne) throws RemoteException {
        return getStub().supprimerMembre(personne);
    }

    // ========== RECHERCHE ==========

    public Personne rechercherUnmembre(Personne personne) throws RemoteException {
        return getStub().rechercherUnmembre(personne);
    }

    public List<Personne> getMembresParCategorie(Categorie categorie) throws RemoteException {
        return getStub().getMembresParCategorie(categorie);
    }

    public List<Personne> getProfesseursParDomaine(int domaine) throws RemoteException {
        return getStub().getProfesseursParDomaine(domaine);
    }

    // ========== LISTE ROUGE ==========

    public int gererListeRouge(int identifiant, String metSurLrouge) throws RemoteException {
        return getStub().gererListeRouge(identifiant, metSurLrouge);
    }

    public boolean ajouterAListeRouge(int id) throws RemoteException {
        return getStub().ajouterAListeRouge(id);
    }

    public boolean retirerDeListeRouge(int id) throws RemoteException {
        return getStub().retirerDeListeRouge(id);
    }

    // ========== CONNEXIONS ==========

    public boolean marquerUtilisateurConnecte(int idPersonne) throws RemoteException {
        return getStub().marquerUtilisateurConnecte(idPersonne);
    }

    public boolean marquerUtilisateurDeconnecte(int idPersonne) throws RemoteException {
        return getStub().marquerUtilisateurDeconnecte(idPersonne);
    }
}

