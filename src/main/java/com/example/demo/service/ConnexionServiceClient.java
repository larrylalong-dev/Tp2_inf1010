package com.example.demo.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import Entite.Personne;
import com.example.demo.client.ServerConnectionManager;
import com.example.demo.server.RemoteAnnuaire;

/**
 * Service client pour gérer les connexions utilisateur via RMI
 */
public class ConnexionServiceClient {

    private final ServerConnectionManager connectionManager;

    public ConnexionServiceClient() {
        this.connectionManager = ServerConnectionManager.getInstance();
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

    public boolean marquerUtilisateurConnecte(int idPersonne) {
        try {
            return getStub().marquerUtilisateurConnecte(idPersonne);
        } catch (RemoteException e) {
            System.err.println("Erreur lors du marquage de connexion: " + e.getMessage());
            return false;
        }
    }

    public boolean marquerUtilisateurDeconnecte(int idPersonne) {
        try {
            return getStub().marquerUtilisateurDeconnecte(idPersonne);
        } catch (RemoteException e) {
            System.err.println("Erreur lors du marquage de déconnexion: " + e.getMessage());
            return false;
        }
    }

    public boolean verifierEtatConnexion(int idPersonne) {
        try {
            return getStub().verifierEtatConnexion(idPersonne);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la vérification d'état de connexion: " + e.getMessage());
            return false;
        }
    }

    public List<Personne> getUtilisateursConnectes() {
        try {
            return getStub().getUtilisateursConnectes();
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs connectés: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Déconnecte tous les utilisateurs (utilisé au démarrage de l'application)
     */
    public void deconnecterTousLesUtilisateurs() {
        try {
            getStub().deconnecterTousLesUtilisateurs();
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la déconnexion de tous les utilisateurs: " + e.getMessage());
        }
    }
}
