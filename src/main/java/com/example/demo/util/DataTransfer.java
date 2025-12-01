package com.example.demo.util;

import Entite.Personne;

/**
 * Classe utilitaire pour transférer des données entre les contrôleurs
 * Permet de passer des objets complexes lors de la navigation entre les écrans
 */
public class DataTransfer {

    // Membre à modifier (pour le formulaire d'ajout/modification)
    private static Personne membreAModifier = null;

    /**
     * Définit le membre à modifier
     * @param personne Le membre à modifier
     */
    public static void setMembreAModifier(Personne personne) {
        membreAModifier = personne;
    }

    /**
     * Récupère le membre à modifier
     * @return Le membre à modifier ou null
     */
    public static Personne getMembreAModifier() {
        return membreAModifier;
    }

    /**
     * Vérifie s'il y a un membre à modifier
     * @return true s'il y a un membre à modifier, false sinon
     */
    public static boolean hasMembreAModifier() {
        return membreAModifier != null;
    }

    /**
     * Nettoie les données de transfert du membre
     */
    public static void clearMembreAModifier() {
        membreAModifier = null;
    }

    /**
     * Nettoie toutes les données de transfert
     */
    public static void clearAll() {
        membreAModifier = null;
    }
}
