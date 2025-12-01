package com.example.demo.util;

import CategorieEnum.Categorie;

/**
 * Classe utilitaire pour les conversions de catégorie
 */
public class CategorieUtil {

    public static String categorieToString(Categorie categorie) {
        if (categorie == null) return "";

        switch (categorie) {
            case professeur:
                return "Professeur";
            case etudiant:
                return "Étudiant";
            case auxiliaire:
                return "Auxiliaire d'enseignement";
            case administrateur:
                return "Administrateur";
            default:
                return "";
        }
    }

    public static Categorie stringToCategorie(String str) {
        if (str == null) return null;

        switch (str) {
            case "Professeur":
                return Categorie.professeur;
            case "Étudiant":
                return Categorie.etudiant;
            case "Auxiliaire d'enseignement":
                return Categorie.auxiliaire;
            case "Administrateur":
                return Categorie.administrateur;
            default:
                return null;
        }
    }
}

