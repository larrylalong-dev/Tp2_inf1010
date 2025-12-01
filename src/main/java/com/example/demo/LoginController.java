package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import Entite.Personne;
import com.example.demo.service.PersonneService;
import com.example.demo.service.ConnexionService;
import com.example.demo.util.SessionManager;
import CategorieEnum.Categorie;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Service pour l'authentification
    private PersonneService personneService;
    private ConnexionService connexionService;

    public void initialize() {
        personneService = new PersonneService();
        connexionService = new ConnexionService();
        errorLabel.setVisible(false);
    }

    @FXML
    private void onLoginClicked(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        // Authentification avec la base de données uniquement
        try {
            authenticateUser(username, password);
        } catch (Exception e) {
            showError("Erreur de connexion : " + e.getMessage());
        }
    }

    private boolean authenticateUser(String username, String password) {
        try {
            // Récupérer tous les membres pour chercher celui qui correspond
            List<Personne> tousLesMembres = personneService.getAllMembres();

            for (Personne personne : tousLesMembres) {
                // Vérifier si le nom correspond au username et le mot de passe correspond
                // Le mot de passe est stocké dans le champ informations
                if (personne.getNom() != null &&
                    personne.getNom().equalsIgnoreCase(username) &&
                    personne.getInformations() != null &&
                    personne.getInformations().equals(password)) {

                    // VÉRIFICATION : Vérifier si l'utilisateur est en liste rouge
                    if (personne.isListeRouge()) {
                        showError("🚫 Accès refusé : Votre compte a été suspendu.\n" +
                                "Vous n'avez plus accès à l'application.\n" +
                                "Si vous pensez qu'il s'agit d'une erreur, contactez l'administrateur.");
                        return false;
                    }

                    // Vérifier que c'est un utilisateur autorisé (toutes les catégories sauf liste rouge)
                    if (personne.getCategorie() == Categorie.administrateur ||
                        personne.getCategorie() == Categorie.professeur ||
                        personne.getCategorie() == Categorie.auxiliaire ||
                        personne.getCategorie() == Categorie.etudiant) {

                        // Marquer l'utilisateur comme connecté dans la base de données
                        boolean connexionReussie = connexionService.marquerUtilisateurConnecte(personne.getId());

                        if (connexionReussie) {
                            // Enregistrer l'utilisateur connecté dans la session
                            SessionManager.getInstance().setUtilisateurConnecte(personne);
                            navigateToMainMenu();
                            return true;
                        } else {
                            showError("Erreur lors de la mise à jour du statut de connexion.");
                            return false;
                        }
                    } else {
                        showError("🚫 Accès refusé : Vous n'avez pas les permissions nécessaires pour accéder à cette application.");
                        return false;
                    }
                }
            }

            // Aucun utilisateur trouvé avec ces identifiants
            showError("❌ Nom d'utilisateur ou mot de passe incorrect.");
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            showError("Erreur lors de l'authentification : " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void navigateToMainMenu() {
        try {
            errorLabel.setVisible(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Préserver les dimensions actuelles
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(scene);
            stage.setTitle("Annuaire INF1010 - Menu Principal");

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du menu principal.");
        }
    }
}
