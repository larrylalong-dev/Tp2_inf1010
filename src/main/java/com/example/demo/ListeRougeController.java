package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import Entite.Personne;
import com.example.demo.service.AnnuaireServiceClient;
import com.example.demo.service.ConnexionServiceClient; // ajout pour corriger l'erreur de symbole
import com.example.demo.util.SessionManager;
import com.example.demo.util.CategorieUtil;
import com.example.demo.util.AuthorizationManager;

import java.util.List;
import java.util.stream.Collectors;

public class ListeRougeController {

    @FXML
    private ToggleGroup affichageToggleGroup; // (Conservé si lié au FXML, sinon peut être retiré)

    @FXML
    private RadioButton tousRadioButton;

    @FXML
    private RadioButton listeRougeRadioButton;

    @FXML
    private Label statutLabel;

    @FXML
    private TableView<Personne> membresTableView;

    @FXML
    private TableColumn<Personne, String> nomColumn;

    @FXML
    private TableColumn<Personne, String> prenomColumn;

    @FXML
    private TableColumn<Personne, String> categorieColumn;

    @FXML
    private TableColumn<Personne, String> courrielColumn;

    @FXML
    private TableColumn<Personne, String> statutColumn;

    @FXML
    private TableColumn<Personne, String> dateAjoutColumn;

    @FXML
    private Button ajouterListeRougeButton;

    @FXML
    private Button retirerListeRougeButton;

    @FXML
    private Label infoLabel;

    // Service pour accéder aux données
    private AnnuaireServiceClient annuaireService;
    private ObservableList<Personne> membresData;
    private List<Personne> tousLesMembres;

    @FXML
    private void initialize() {
        // Vérifier l'état de connexion de l'utilisateur
        if (!verifierEtatConnexion()) {
            return; // Ne pas continuer l'initialisation si l'utilisateur n'est pas autorisé
        }

        // Initialiser les services
        annuaireService = new AnnuaireServiceClient();

        // Vérification disponibilité serveur avant toute action
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }

        membresData = FXCollections.observableArrayList();

        // Configurer le tableau
        setupTableColumns();

        // Configurer les boutons d'affichage
        setupAffichageButtons();

        // Charger les données
        chargerTousLesMembres();

        // Afficher tous les membres par défaut
        afficherTousLesMembres();
    }

    /**
     * Vérifie si l'utilisateur actuel est toujours connecté et autorisé
     */
    private boolean verifierEtatConnexion() {
        // Vérifier si l'utilisateur est connecté dans la session
        if (!SessionManager.getInstance().isConnecte()) {
            redirectToLogin();
            return false;
        }

        // Vérifier l'état de connexion dans la base de données
        Personne utilisateurConnecte = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateurConnecte != null) {
            ConnexionServiceClient localConnexionService = new ConnexionServiceClient();
            if (!localConnexionService.verifierEtatConnexion(utilisateurConnecte.getId())) {
                SessionManager.getInstance().deconnecter();
                showAlert("Session expirée", "Votre session a expiré ou vous avez été déconnecté depuis un autre appareil.", Alert.AlertType.WARNING);
                redirectToLogin();
                return false;
            }
        }

        // Vérifier les autorisations d'accès à cette fonctionnalité
        if (!AuthorizationManager.getInstance().canManageBlacklist()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à cette fonctionnalité.", Alert.AlertType.ERROR);
            redirectToMainMenu();
            return false;
        }

        return true;
    }

    private void redirectToLogin() {
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", tousRadioButton);
    }

    private void redirectToMainMenu() {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal - Annuaire INF1010", tousRadioButton);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTableColumns() {
        // Configuration des colonnes avec les propriétés de l'entité Personne
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        categorieColumn.setCellValueFactory(cellData -> {
            Personne personne = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                CategorieUtil.categorieToString(personne.getCategorie())
            );
        });
        courrielColumn.setCellValueFactory(new PropertyValueFactory<>("adresseCourriel"));
        statutColumn.setCellValueFactory(cellData -> {
            Personne personne = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                personne.isListeRouge() ? "Liste Rouge" : "Actif"
            );
        });
        dateAjoutColumn.setCellValueFactory(cellData -> {
            Personne personne = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                personne.isListeRouge() ? "Non disponible" : "N/A"
            );
        });

        // Ajouter le style visuel pour les membres en liste rouge
        membresTableView.setRowFactory(tv -> {
            TableRow<Personne> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isListeRouge()) {
                    // Style renforcé pour les membres en liste rouge dans la gestion de liste rouge
                    row.setStyle("-fx-background-color: #ffcdd2; -fx-border-color: #d32f2f; -fx-border-width: 0 0 0 6px; -fx-text-fill: #b71c1c;");
                } else {
                    // Style normal avec une légère couleur verte pour les actifs
                    row.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #4caf50; -fx-border-width: 0 0 0 2px;");
                }
            });
            return row;
        });

        // CRUCIAL : Ajouter l'écouteur de sélection pour activer/désactiver les boutons
        membresTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonsState();
        });

        // Lier les données au tableau
        membresTableView.setItems(membresData);
    }

    private void setupAffichageButtons() {
        // Sélectionner "Tous" par défaut
        tousRadioButton.setSelected(true);

        // Configurer les actions des boutons radio
        tousRadioButton.setOnAction(this::onAffichageChanged);
        listeRougeRadioButton.setOnAction(this::onAffichageChanged);
    }

    private void chargerTousLesMembres() {
        // Vérifier serveur avant de charger
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }
        try {
            tousLesMembres = annuaireService.getAllMembres();
        } catch (Exception e) {
            showErrorMessage("Erreur de chargement", "Impossible de charger les membres : " + e.getMessage());
            tousLesMembres = FXCollections.observableArrayList();
        }
    }

    @FXML
    private void onAffichageChanged(ActionEvent event) {
        if (tousRadioButton.isSelected()) {
            afficherTousLesMembres();
        } else if (listeRougeRadioButton.isSelected()) {
            afficherListeRougeUniquement();
        }
    }

    private void afficherTousLesMembres() {
        membresData.clear();
        membresData.addAll(tousLesMembres);

        long nbListeRouge = tousLesMembres.stream().mapToLong(p -> p.isListeRouge() ? 1 : 0).sum();
        statutLabel.setText("Affichage : Tous les membres (" + tousLesMembres.size() + " total, " + nbListeRouge + " en liste rouge)");

        updateButtonsState();
        updateInfoLabel();
    }

    private void afficherListeRougeUniquement() {
        List<Personne> membresListeRouge = tousLesMembres.stream()
            .filter(Personne::isListeRouge)
            .toList(); // simplification

        membresData.clear();
        membresData.addAll(membresListeRouge);

        statutLabel.setText("Affichage : Liste rouge uniquement (" + membresListeRouge.size() + " membre(s))");

        updateButtonsState();
        updateInfoLabel();
    }

    private void updateButtonsState() {
        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();

        if (selectedMembre != null) {
            ajouterListeRougeButton.setDisable(selectedMembre.isListeRouge());
            retirerListeRougeButton.setDisable(!selectedMembre.isListeRouge());
        } else {
            ajouterListeRougeButton.setDisable(true);
            retirerListeRougeButton.setDisable(true);
        }
    }

    private void updateInfoLabel() {
        long totalMembres = tousLesMembres.size();
        long membresListeRouge = tousLesMembres.stream().mapToLong(p -> p.isListeRouge() ? 1 : 0).sum();
        double pourcentage = totalMembres > 0 ? (membresListeRouge * 100.0 / totalMembres) : 0;

        infoLabel.setText(String.format("Total : %d membres | Liste rouge : %d (%.1f%%)",
            totalMembres, membresListeRouge, pourcentage));
    }

    @FXML
    private void onSelectionChanged() {
        updateButtonsState();
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    @FXML
    private void onAjouterListeRougeClicked(ActionEvent event) {
        // Vérifier serveur
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }
        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un membre à ajouter à la liste rouge.");
            return;
        }
        if (selectedMembre.isListeRouge()) {
            showWarningMessage("Déjà en liste rouge", "Ce membre est déjà dans la liste rouge.");
            return;
        }
        if (showConfirmationDialog("Ajouter à la liste rouge",
            "Êtes-vous sûr de vouloir ajouter " + selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " à la liste rouge ?")) {
            try {
                boolean success = annuaireService.ajouterAListeRouge((int) selectedMembre.getId());
                if (success) {
                    showInfoMessage("Succès", selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " a été ajouté à la liste rouge.");
                    rafraichirDonnees();
                } else {
                    showErrorMessage("Erreur", "Impossible d'ajouter le membre à la liste rouge.");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur", "Erreur lors de l'ajout à la liste rouge : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onRetirerListeRougeClicked(ActionEvent event) {
        // Vérifier serveur
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }
        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un membre à retirer de la liste rouge.");
            return;
        }
        if (!selectedMembre.isListeRouge()) {
            showWarningMessage("Pas en liste rouge", "Ce membre n'est pas dans la liste rouge.");
            return;
        }
        if (showConfirmationDialog("Retirer de la liste rouge",
            "Êtes-vous sûr de vouloir retirer " + selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " de la liste rouge ?")) {
            try {
                boolean success = annuaireService.retirerDeListeRouge((int) selectedMembre.getId());
                if (success) {
                    showInfoMessage("Succès", selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " a été retiré de la liste rouge.");
                    rafraichirDonnees();
                } else {
                    showErrorMessage("Erreur", "Impossible de retirer le membre de la liste rouge.");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur", "Erreur lors du retrait de la liste rouge : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onActualiserClicked(ActionEvent event) {
        rafraichirDonnees();
        showInfoMessage("Actualisation", "Données actualisées.");
    }

    private void rafraichirDonnees() {
        // Vérifier serveur
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }
        chargerTousLesMembres();
        if (tousRadioButton.isSelected()) {
            afficherTousLesMembres();
        } else {
            afficherListeRougeUniquement();
        }
    }

    @FXML
    private void onExporterClicked(ActionEvent event) {
        // TODO: Implémenter l'export de la liste rouge
        showInfoMessage("Export", "Fonctionnalité d'export à implémenter.");
    }

    @FXML
    private void onVoirDetailsClicked(ActionEvent event) {
        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un membre pour voir ses détails.");
            return;
        }
        // TODO: Implémenter la navigation vers les détails avec l'ID du membre
        // Pour l'instant, on affiche les détails dans une boîte de dialogue
        showInfoMessage("Détails du membre",
            "Nom: " + selectedMembre.getNom() + "\n" +
            "Prénom: " + selectedMembre.getPrenom() + "\n" +
            "Catégorie: " + CategorieUtil.categorieToString(selectedMembre.getCategorie()) + "\n" +
            "Email: " + selectedMembre.getAdresseCourriel() + "\n" +
            "Téléphone: " + (selectedMembre.getTelephone() != null ? selectedMembre.getTelephone() : "N/A") + "\n" +
            "Matricule: " + (selectedMembre.getMatricule() != null ? selectedMembre.getMatricule() : "N/A") + "\n" +
            "Domaine: " + (selectedMembre.getDomaineActivite() != null ? selectedMembre.getDomaineActivite() : "N/A") + "\n" +
            "Statut: " + (selectedMembre.isListeRouge() ? "Liste Rouge" : "Actif"));
    }

    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void navigateToServiceIndisponible() {
        Node currentNode = tousRadioButton != null ? tousRadioButton : membresTableView;
        NavigationHelper.navigateTo("service-indisponible.fxml", "Service Indisponible", currentNode);
    }
}
