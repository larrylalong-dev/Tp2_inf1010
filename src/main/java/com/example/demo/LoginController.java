package com.example.demo;

import com.example.demo.service.ServerMonitorService;
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

        // Désactiver le bouton pour éviter les doubles clics
        errorLabel.setVisible(false);

        // Effectuer l'authentification dans un thread séparé pour ne pas bloquer l'UI
        javafx.concurrent.Task<Boolean> authTask = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Vérifier la disponibilité du serveur avant de continuer
                if (!annuaireService.isServerAvailable()) {
                    return null; // null signifie serveur indisponible
                }

                // Authentification via RMI (dans le thread séparé)
                return authenticateUser(username, password);
            }
        };

        // Gérer le résultat de l'authentification
        authTask.setOnSucceeded(e -> {
            Boolean result = authTask.getValue();
            if (result == null) {
                // Serveur indisponible
                navigateToServiceIndisponible();
            } else if (!result) {
                // Authentification échouée (message déjà affiché par authenticateUser)
                // L'erreur est déjà affichée dans authenticateUser
            }
            // Si true, la navigation est déjà faite dans authenticateUser
        });

        authTask.setOnFailed(e -> {
            Throwable exception = authTask.getException();
            showError("Erreur de connexion : " + exception.getMessage());
        });

        // Démarrer le thread d'authentification
        new Thread(authTask).start();
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
                        javafx.application.Platform.runLater(() ->
                            showError("🚫 Accès refusé : Votre compte a été suspendu.\n" +
                                    "Vous n'avez plus accès à l'application.\n" +
                                    "Si vous pensez qu'il s'agit d'une erreur, contactez l'administrateur.")
                        );
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

                            // Démarrer la surveillance du serveur
                            ServerMonitorService.getInstance().startMonitoring(() -> {
                                // Redirection vers la page de reconnexion en cas de déconnexion
                                javafx.application.Platform.runLater(() -> {
                                    navigateToServiceIndisponible();
                                });
                            });

                            javafx.application.Platform.runLater(() -> navigateToMainMenu());
                            return true;
                        } else {
                            javafx.application.Platform.runLater(() ->
                                showError("Erreur lors de la mise à jour du statut de connexion.")
                            );
                            return false;
                        }
                    } else {
                        javafx.application.Platform.runLater(() ->
                            showError("🚫 Accès refusé : Vous n'avez pas les permissions nécessaires pour accéder à cette application.")
                        );
                        return false;
                    }
                }
            }

            // Aucun utilisateur trouvé avec ces identifiants
            javafx.application.Platform.runLater(() ->
                showError("❌ Nom d'utilisateur ou mot de passe incorrect.")
            );
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            javafx.application.Platform.runLater(() ->
                showError("Erreur lors de l'authentification : " + e.getMessage())
            );
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

            // Vérification de la disponibilité de la scène
            if (usernameField == null || usernameField.getScene() == null || usernameField.getScene().getWindow() == null) {
                showError("Erreur : Impossible de charger le menu principal.");
                return;
            }

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
            // Vérification de la disponibilité de la scène
            if (usernameField == null || usernameField.getScene() == null || usernameField.getScene().getWindow() == null) {
                showError("❌ Serveur indisponible. Veuillez démarrer le serveur et réessayer.");
                return;
            }

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
