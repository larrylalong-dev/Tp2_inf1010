package com.example.demo.util;

import Entite.Personne;
import CategorieEnum.Categorie;

/**
 * Classe utilitaire pour gérer les autorisations basées sur les catégories d'utilisateurs
 */
public class AuthorizationManager {

    private static AuthorizationManager instance;

    private AuthorizationManager() {}

    public static AuthorizationManager getInstance() {
        if (instance == null) {
            instance = new AuthorizationManager();
        }
        return instance;
    }

    /**
     * Vérifie si l'utilisateur connecté est un administrateur
     */
    public boolean isAdministrator() {
        Personne utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        return utilisateur != null && utilisateur.getCategorie() == Categorie.administrateur;
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à la gestion des membres
     */
    public boolean canAccessMemberManagement() {
        Personne utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) return false;

        Categorie categorie = utilisateur.getCategorie();
        return categorie == Categorie.administrateur ||
               categorie == Categorie.professeur ||
               categorie == Categorie.auxiliaire ||
               categorie == Categorie.etudiant;
    }

    /**
     * Vérifie si l'utilisateur connecté a accès aux professeurs par domaine
     */
    public boolean canAccessProfessorsByDomain() {
        Personne utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) return false;

        Categorie categorie = utilisateur.getCategorie();
        return categorie == Categorie.administrateur ||
               categorie == Categorie.professeur ||
               categorie == Categorie.auxiliaire ||
               categorie == Categorie.etudiant;
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à la recherche
     */
    public boolean canAccessSearch() {
        Personne utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) return false;

        Categorie categorie = utilisateur.getCategorie();
        return categorie == Categorie.administrateur ||
               categorie == Categorie.professeur ||
               categorie == Categorie.auxiliaire ||
               categorie == Categorie.etudiant;
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à l'ajout de membres
     */
    public boolean canAddMembers() {
        return isAdministrator();
    }

    /**
     * Vérifie si l'utilisateur connecté a accès à la gestion de la liste rouge
     */
    public boolean canManageBlacklist() {
        return isAdministrator();
    }

    /**
     * Vérifie si l'utilisateur connecté peut voir les utilisateurs connectés
     */
    public boolean canViewConnectedUsers() {
        return isAdministrator();
    }

    /**
     * Affiche un message d'erreur d'accès refusé
     */
    public void showAccessDeniedMessage() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Accès refusé");
        alert.setHeaderText("Autorisation insuffisante");
        alert.setContentText("Cette fonctionnalité est réservée aux administrateurs.\n\n" +
                            "Votre catégorie actuelle ne vous permet pas d'accéder à cette section.");
        alert.showAndWait();
    }
}
