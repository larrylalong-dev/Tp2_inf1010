package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import Entite.Personne;
import com.example.demo.service.ConnexionService;
import com.example.demo.util.AuthorizationManager;

import java.util.List;
import java.util.Optional;

public class UtilisateursConnectesController {

    @FXML
    private TableView<Personne> tableUtilisateursConnectes;

    @FXML
    private TableColumn<Personne, String> colNom;

    @FXML
    private TableColumn<Personne, String> colPrenom;

    @FXML
    private TableColumn<Personne, String> colMatricule;

    @FXML
    private TableColumn<Personne, String> colCategorie;

    @FXML
    private TableColumn<Personne, String> colEmail;

    @FXML
    private Button actualiserButton;

    @FXML
    private Button deconnecterButton;

    @FXML
    private Button retourButton;

    @FXML
    private Label statutLabel;

    private ConnexionService connexionService;
    private ObservableList<Personne> utilisateursConnectes;

    @FXML
    private void initialize() {
        connexionService = new ConnexionService();
        utilisateursConnectes = FXCollections.observableArrayList();

        // Configuration des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colCategorie.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCategorie() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getCategorie().name().substring(0, 1).toUpperCase() +
                    cellData.getValue().getCategorie().name().substring(1)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colEmail.setCellValueFactory(new PropertyValueFactory<>("adresseCourriel"));

        // Configuration du tableau
        tableUtilisateursConnectes.setItems(utilisateursConnectes);

        // Permettre la sélection multiple pour déconnecter plusieurs utilisateurs
        tableUtilisateursConnectes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Charger les données initiales
        chargerUtilisateursConnectes();

        // Vérifier les autorisations
        verifierAutorisations();
    }

    private void verifierAutorisations() {
        if (!AuthorizationManager.getInstance().canViewConnectedUsers()) {
            // Rediriger vers le menu principal si pas d'autorisation
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            retournerAuMenu();
        }
    }

    @FXML
    private void onActualiserClicked(ActionEvent event) {
        chargerUtilisateursConnectes();
    }

    @FXML
    private void onDeconnecterClicked(ActionEvent event) {
        ObservableList<Personne> selectedItems = tableUtilisateursConnectes.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText("Veuillez sélectionner un utilisateur");
            alert.setContentText("Sélectionnez au moins un utilisateur à déconnecter de la liste.");
            alert.showAndWait();
            return;
        }

        // Confirmation de déconnexion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la déconnexion");
        confirmation.setHeaderText("Déconnecter les utilisateurs sélectionnés ?");

        String message = "Êtes-vous sûr de vouloir déconnecter ";
        if (selectedItems.size() == 1) {
            Personne personne = selectedItems.get(0);
            message += personne.getPrenom() + " " + personne.getNom() + " ?";
        } else {
            message += selectedItems.size() + " utilisateurs ?";
        }

        confirmation.setContentText(message);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deconnecterUtilisateurs(selectedItems);
        }
    }

    private void deconnecterUtilisateurs(ObservableList<Personne> utilisateurs) {
        int deconnexionsReussies = 0;
        int deconnexionsEchouees = 0;

        for (Personne personne : utilisateurs) {
            boolean succes = connexionService.marquerUtilisateurDeconnecte(personne.getId());
            if (succes) {
                deconnexionsReussies++;
            } else {
                deconnexionsEchouees++;
            }
        }

        // Afficher le résultat
        Alert resultat = new Alert(Alert.AlertType.INFORMATION);
        resultat.setTitle("Déconnexion terminée");
        resultat.setHeaderText("Résultat de l'opération");

        String message = deconnexionsReussies + " utilisateur(s) déconnecté(s) avec succès.";
        if (deconnexionsEchouees > 0) {
            message += "\n" + deconnexionsEchouees + " échec(s) de déconnexion.";
        }

        resultat.setContentText(message);
        resultat.showAndWait();

        // Actualiser la liste
        chargerUtilisateursConnectes();
    }

    private void chargerUtilisateursConnectes() {
        try {
            List<Personne> listeConnectes = connexionService.getUtilisateursConnectes();

            utilisateursConnectes.clear();
            if (listeConnectes != null) {
                utilisateursConnectes.addAll(listeConnectes);
            }

            // Mettre à jour le statut
            if (statutLabel != null) {
                int nombreConnectes = utilisateursConnectes.size();
                statutLabel.setText("Utilisateurs connectés : " + nombreConnectes);

                if (nombreConnectes == 0) {
                    statutLabel.setStyle("-fx-text-fill: #666666;");
                } else {
                    statutLabel.setStyle("-fx-text-fill: #2e7d32;");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement");
            alert.setContentText("Impossible de charger la liste des utilisateurs connectés : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        retournerAuMenu();
    }

    private void retournerAuMenu() {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal - Annuaire INF1010", retourButton);
    }
}
