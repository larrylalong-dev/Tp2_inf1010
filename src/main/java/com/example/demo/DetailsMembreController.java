package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

public class DetailsMembreController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label categorieLabel;

    @FXML
    private Label matriculeTitleLabel;

    @FXML
    private Label matriculeLabel;

    @FXML
    private Label courrielLabel;

    @FXML
    private Label telephoneTitleLabel;

    @FXML
    private Label telephoneLabel;

    @FXML
    private Label domaineLabel;

    @FXML
    private TextArea informationsTextArea;

    @FXML
    private Label statutLabel;

    @FXML
    private Button listeRougeButton;

    @FXML
    private void initialize() {
        // Simuler l'affichage d'un membre exemple
        afficherMembreExemple();
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    @FXML
    private void onModifierClicked(ActionEvent event) {
        NavigationHelper.navigateTo("ajouter-modifier-membre.fxml", "Modifier le membre", (Node) event.getSource());
    }

    @FXML
    private void onSupprimerClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer le membre");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce membre ?\n\nCette action est irréversible.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            showInfoMessage("Suppression", "Membre supprimé avec succès.\n(Fonctionnalité à implémenter par l'équipe backend)");
            NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
        }
    }

    @FXML
    private void onListeRougeClicked(ActionEvent event) {
        String buttonText = listeRougeButton.getText();

        if (buttonText.contains("Ajouter")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmer l'ajout");
            alert.setHeaderText("Ajouter à la liste rouge");
            alert.setContentText("Êtes-vous sûr de vouloir ajouter ce membre à la liste rouge ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                listeRougeButton.setText("Retirer de la liste rouge");
                statutLabel.setText("Sur la liste rouge");
                statutLabel.setStyle("-fx-text-fill: #c62828; -fx-background-color: #ffebee; -fx-padding: 4px 8px; -fx-background-radius: 12px;");
                showInfoMessage("Liste rouge", "Membre ajouté à la liste rouge.\n(Fonctionnalité à implémenter par l'équipe backend)");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmer le retrait");
            alert.setHeaderText("Retirer de la liste rouge");
            alert.setContentText("Êtes-vous sûr de vouloir retirer ce membre de la liste rouge ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                listeRougeButton.setText("Ajouter à la liste rouge");
                statutLabel.setText("Actif");
                statutLabel.setStyle("-fx-text-fill: #2e7d32; -fx-background-color: #e8f5e8; -fx-padding: 4px 8px; -fx-background-radius: 12px;");
                showInfoMessage("Liste rouge", "Membre retiré de la liste rouge.\n(Fonctionnalité à implémenter par l'équipe backend)");
            }
        }
    }

    private void afficherMembreExemple() {
        // Afficher des données d'exemple pour démonstration
        nomLabel.setText("Dupont");
        prenomLabel.setText("Jean");
        categorieLabel.setText("Professeur");
        courrielLabel.setText("jean.dupont@exemple.com");

        // Afficher les champs spécifiques aux professeurs
        telephoneTitleLabel.setVisible(true);
        telephoneLabel.setVisible(true);
        telephoneLabel.setText("(514) 123-4567");

        // Masquer les champs étudiants
        matriculeTitleLabel.setVisible(false);
        matriculeLabel.setVisible(false);

        domaineLabel.setText("Intelligence artificielle");
        informationsTextArea.setText("Professeur spécialisé en apprentissage automatique et réseaux de neurones. " +
                                    "Recherche active dans le domaine de l'IA explicable.");

        statutLabel.setText("Actif");
        statutLabel.setStyle("-fx-text-fill: #2e7d32; -fx-background-color: #e8f5e8; -fx-padding: 4px 8px; -fx-background-radius: 12px;");

        listeRougeButton.setText("Ajouter à la liste rouge");

        showInfoMessage("Données d'exemple", "Affichage d'un membre exemple.\nLes vraies données seront chargées par l'équipe backend.");
    }

    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
