package com.example.demo.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.Node;
import com.example.demo.service.ConnexionService;
import com.example.demo.NavigationHelper;
import java.util.Optional;

/**
 * Utilitaire pour vérifier l'état de connexion des utilisateurs
 * avant d'autoriser l'accès aux fonctionnalités
 */
public class ConnexionValidator {

    private static ConnexionValidator instance;
    private ConnexionService connexionService;

    private ConnexionValidator() {
        this.connexionService = new ConnexionService();
    }

    public static ConnexionValidator getInstance() {
        if (instance == null) {
            instance = new ConnexionValidator();
        }
        return instance;
    }

    /**
     * Vérifie si l'utilisateur actuel est toujours marqué comme connecté dans la base de données
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean verifierEtatConnexion() {
        if (!SessionManager.getInstance().isConnecte()) {
            return false;
        }

        int idUtilisateur = SessionManager.getInstance().getUtilisateurConnecte().getId();
        return connexionService.verifierEtatConnexion(idUtilisateur);
    }

    /**
     * Vérifie l'état de connexion et affiche une popup si l'utilisateur est déconnecté,
     * puis redirige vers la page de login
     * @param sourceNode Le nœud source pour la navigation
     * @return true si l'utilisateur est connecté, false s'il a été déconnecté
     */
    public boolean verifierEtRedirigerSiDeconnecte(Node sourceNode) {
        if (!verifierEtatConnexion()) {
            afficherPopupDeconnexion();
            redirigerVersLogin(sourceNode);
            return false;
        }
        return true;
    }

    /**
     * Affiche une popup informant l'utilisateur qu'il a été déconnecté
     */
    private void afficherPopupDeconnexion() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Session expirée");
        alert.setHeaderText("Vous avez été déconnecté");
        alert.setContentText("Votre session a été fermée par un administrateur.\n" +
                            "Vous allez être redirigé vers la page de connexion.\n\n" +
                            "Veuillez vous reconnecter pour continuer.");

        // Personnaliser le bouton OK
        ButtonType okButton = new ButtonType("OK, me reconnecter");
        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait();
    }

    /**
     * Redirige vers la page de login
     */
    private void redirigerVersLogin(Node sourceNode) {
        // Déconnecter l'utilisateur de la session locale
        SessionManager.getInstance().deconnecter();

        // Rediriger vers la page de login
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", sourceNode);
    }

    /**
     * Méthode utilitaire pour être utilisée dans tous les contrôleurs
     * Vérifie l'état de connexion et retourne true si l'action peut continuer
     */
    public boolean peutContinuerAction(Node sourceNode) {
        return verifierEtRedirigerSiDeconnecte(sourceNode);
    }
}
