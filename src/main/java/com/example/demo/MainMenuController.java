package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.demo.util.SessionManager;
import com.example.demo.util.AuthorizationManager;
import com.example.demo.util.ServerValidator;
import com.example.demo.service.ConnexionService;
import com.example.demo.service.ServerMonitorService;

public class MainMenuController {

    @FXML
    private Label connexionLabel;

    @FXML
    private Button deconnexionButton;

    @FXML
    private Button retourConnexionButton;

    // Boutons des fonctionnalités
    @FXML
    private Button listerMembresButton;

    @FXML
    private Button listerProfesseursButton;

    @FXML
    private Button rechercherButton;

    @FXML
    private Button ajouterMembreButton;

    @FXML
    private Button gererListeRougeButton;

    @FXML
    private Button voirConnectesButton;

    private ConnexionService connexionService;

    @FXML
    private void initialize() {
        connexionService = new ConnexionService();

        // Mettre à jour le label de connexion et la visibilité des boutons
        updateConnectionDisplay();

        // Appliquer les restrictions d'accès
        applyAccessRestrictions();
    }

    private void updateConnectionDisplay() {
        if (connexionLabel != null && deconnexionButton != null && retourConnexionButton != null) {
            boolean isConnected = SessionManager.getInstance().isConnecte();
            connexionLabel.setText(SessionManager.getInstance().getTexteConnexion());

            // Afficher le bon bouton selon l'état de connexion
            deconnexionButton.setVisible(isConnected);
            retourConnexionButton.setVisible(!isConnected);
        }
    }

    private void applyAccessRestrictions() {
        AuthorizationManager authManager = AuthorizationManager.getInstance();

        // Gestion des membres - accessible aux administrateurs, professeurs et auxiliaires
        if (listerMembresButton != null) {
            boolean canAccessMembers = authManager.canAccessMemberManagement();
            listerMembresButton.setDisable(!canAccessMembers);
            if (!canAccessMembers) {
                listerMembresButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        // Professeurs par domaine - réservé aux administrateurs
        if (listerProfesseursButton != null) {
            boolean canAccessProfessors = authManager.canAccessProfessorsByDomain();
            listerProfesseursButton.setDisable(!canAccessProfessors);
            if (!canAccessProfessors) {
                listerProfesseursButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        // Recherche - réservée aux administrateurs
        if (rechercherButton != null) {
            boolean canAccessSearch = authManager.canAccessSearch();
            rechercherButton.setDisable(!canAccessSearch);
            if (!canAccessSearch) {
                rechercherButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        // Ajouter membre - réservé aux administrateurs
        if (ajouterMembreButton != null) {
            boolean canAddMembers = authManager.canAddMembers();
            ajouterMembreButton.setDisable(!canAddMembers);
            if (!canAddMembers) {
                ajouterMembreButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        // Gérer liste rouge - réservé aux administrateurs
        if (gererListeRougeButton != null) {
            boolean canManageBlacklist = authManager.canManageBlacklist();
            gererListeRougeButton.setDisable(!canManageBlacklist);
            if (!canManageBlacklist) {
                gererListeRougeButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        // Voir les utilisateurs connectés - réservé aux administrateurs
        if (voirConnectesButton != null) {
            boolean canViewConnectedUsers = authManager.canViewConnectedUsers();
            voirConnectesButton.setDisable(!canViewConnectedUsers);
            if (!canViewConnectedUsers) {
                voirConnectesButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }
    }

    @FXML
    private void onRetourConnexionClicked(ActionEvent event) {
        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
    }

    @FXML
    private void onListerMembresClicked(ActionEvent event) {
        if (!AuthorizationManager.getInstance().canAccessMemberManagement()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("liste-membres.fxml", "Liste des membres", (Node) event.getSource());
    }

    @FXML
    private void onListerProfesseursClicked(ActionEvent event) {
        if (!AuthorizationManager.getInstance().canAccessProfessorsByDomain()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("liste-professeurs.fxml", "Professeurs par domaine", (Node) event.getSource());
    }

    @FXML
    private void onRechercherMembreClicked(ActionEvent event) {
        if (!AuthorizationManager.getInstance().canAccessSearch()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("recherche-membre.fxml", "Rechercher un membre", (Node) event.getSource());
    }

    @FXML
    private void onAjouterMembreClicked(ActionEvent event) {
        if (!AuthorizationManager.getInstance().canAddMembers()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("ajouter-modifier-membre.fxml", "Ajouter un membre", (Node) event.getSource());
    }

    @FXML
    private void onGererListeRougeClicked(ActionEvent event) {
        if (!AuthorizationManager.getInstance().canManageBlacklist()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("liste-rouge.fxml", "Gérer la liste rouge", (Node) event.getSource());
    }

    @FXML
    private void onVoirConnectesClicked(ActionEvent event) {
        // Vérifier la connexion serveur d'abord
        if (!ServerValidator.validateServerConnection((Node) event.getSource())) {
            return;
        }

        if (!AuthorizationManager.getInstance().canViewConnectedUsers()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }
        NavigationHelper.navigateTo("utilisateurs-connectes.fxml", "Utilisateurs connectés", (Node) event.getSource());
    }

    @FXML
    private void onQuitterClicked(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onAProposClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Annuaire INF1010");
        alert.setContentText("Application d'annuaire pour le département INF1010\n\n" +
                "Cette application permet de gérer un annuaire des professeurs,\n" +
                "auxiliaires d'enseignement et étudiants du cours INF1010.");
        alert.showAndWait();
    }

    @FXML
    private void onDeconnexionClicked(ActionEvent event) {
        // Arrêter la surveillance du serveur
        ServerMonitorService.getInstance().stopMonitoring();

        // Marquer l'utilisateur comme déconnecté dans la base de données
        if (SessionManager.getInstance().isConnecte()) {
            int idUtilisateur = SessionManager.getInstance().getUtilisateurConnecte().getId();
            connexionService.marquerUtilisateurDeconnecte(idUtilisateur);
        }

        // Déconnecter l'utilisateur
        SessionManager.getInstance().deconnecter();

        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
    }
}
