package com.example.demo.test;

import com.example.demo.dao.PersonneDAO;
import com.example.demo.dao.PersonneDAOImpl;
import Entite.Personne;
import CategorieEnum.Categorie;

public class App {
    public static void main(String[] args) throws Exception {

        /*--------------TESTE 1 POUR INSERT
         * PersonneDAOImpl personneDAO = new PersonneDAOImpl();
         *
         * Personne personne = new Personne(
         * "konan", "moiye", "0383223", "243434545", "ali.tremblay@uqtr.ca", "Réseaux",
         * "elev2024", Categorie.etudiant,
         * false
         * );
         * personneDAO.ajouterMembre(personne); // ajouter membres
         */


        /* -----------TESTE 2 POUR DELETE

         PersonneDAOImpl personneDAO = new PersonneDAOImpl();
         Personne personne = personneDAO.getMembreById(2);

        personneDAO.supprimerMembre(personne);
        //System.out.println(personne);
         */

         /* -----------TESTE 3 POUR UPDATE

         PersonneDAOImpl personneDAO = new PersonneDAOImpl();
         Personne personne = new Personne(1,
         "unePersonne", "moiye", "23233435", "243434545", "toure.koffi@uqtr.ca", "Réseaux",
          "elev2024", Categorie.etudiant,
          false
          );

        personneDAO.modifierMembre(personne);
         */


         /* -----------TESTE 4 POUR GETALL

         PersonneDAOImpl personneDAO = new PersonneDAOImpl();
         personneDAO.getAll();

         */
    }
}
