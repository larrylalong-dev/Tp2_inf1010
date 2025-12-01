package com.example.demo.util;

import Entite.Personne;

/**
 * Classe pour gérer la session de l'utilisateur connecté
 */
public class SessionManager {
    private static SessionManager instance;
    private Personne utilisateurConnecte;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUtilisateurConnecte(Personne utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public Personne getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    public String getNomCompletUtilisateur() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getPrenom() + " " + utilisateurConnecte.getNom();
        }
        return "Utilisateur inconnu";
    }

    public String getCategorieUtilisateur() {
        if (utilisateurConnecte != null) {
            return switch (utilisateurConnecte.getCategorie()) {
                case professeur -> "Professeur";
                case auxiliaire -> "Auxiliaire d'enseignement";
                case etudiant -> "Étudiant";
                case administrateur -> "Administrateur";
            };
        }
        return "Catégorie inconnue";
    }

    public String getTexteConnexion() {
        if (isConnecte()) {
            return "Connecté en tant que " + getNomCompletUtilisateur() + " (" + getCategorieUtilisateur() + ")";
        }
        return "Non connecté (inviter)";
    }

    public void deconnecter() {
        this.utilisateurConnecte = null;
    }
}
