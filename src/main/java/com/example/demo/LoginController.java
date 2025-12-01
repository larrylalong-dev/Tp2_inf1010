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
import com.example.demo.service.AnnuaireServiceClient;
import com.example.demo.service.ConnexionServiceClient;
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

    // Services clients pour la communication RMI
    private AnnuaireServiceClient annuaireService;
    private ConnexionServiceClient connexionService;

    public void initialize() {
        annuaireService = new AnnuaireServiceClient();
        connexionService = new ConnexionServiceClient();
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

        // Vérifier la disponibilité du serveur avant de continuer
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }

        // Authentification via RMI
        try {
            authenticateUser(username, password);
        } catch (Exception e) {
            showError("Erreur de connexion : " + e.getMessage());
        }
    }

    private boolean authenticateUser(String username, String password) {
        try {
            // Récupérer tous les membres pour chercher celui qui correspond
            List<Personne> tousLesMembres = annuaireService.getAllMembres();

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

    private void navigateToServiceIndisponible() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("service-indisponible.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Préserver les dimensions actuelles
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(scene);
            stage.setTitle("Service Indisponible");

        } catch (IOException e) {
            e.printStackTrace();
            showError("❌ Serveur indisponible. Veuillez démarrer le serveur et réessayer.");
        }
    }
}
