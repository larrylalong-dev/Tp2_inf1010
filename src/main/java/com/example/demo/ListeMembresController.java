package com.example.demo;

import com.example.demo.service.ConnexionServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import Entite.Personne;
import com.example.demo.service.AnnuaireServiceClient;
import CategorieEnum.Categorie;
import com.example.demo.util.DataTransfer;
import com.example.demo.util.SessionManager;
import com.example.demo.util.CategorieUtil;
import com.example.demo.util.AuthorizationManager;

import java.util.List;
import java.util.stream.Collectors;

public class ListeMembresController {

    @FXML
    private Label connexionLabel;

    @FXML
    private Button deconnexionButton;

    @FXML
    private Button retourConnexionButton;

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private TableView<Personne> membresTableView;

    @FXML
    private TableColumn<Personne, String> nomColumn;

    @FXML
    private TableColumn<Personne, String> prenomColumn;

    @FXML
    private TableColumn<Personne, String> categorieColumn;

    @FXML
    private TableColumn<Personne, String> matriculeColumn;

    @FXML
    private TableColumn<Personne, String> courrielColumn;

    @FXML
    private TableColumn<Personne, String> telephoneColumn;

    @FXML
    private TableColumn<Personne, String> domaineColumn;

    @FXML
    private TableColumn<Personne, String> statutColumn;

    @FXML
    private Label compteurLabel;

    // Boutons d'action - pour appliquer les restrictions
    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button ajouterListeRougeButton;

    @FXML
    private Button retirerListeRougeButton;

    // Service pour accéder aux données
    private AnnuaireServiceClient annuaireService;
    private ConnexionServiceClient connexionService;
    private ObservableList<Personne> membresData;
    private ObservableList<Personne> tousLesMembres;

    @FXML
    private void initialize() {
        // Vérifier l'état de connexion de l'utilisateur
        if (!verifierEtatConnexion()) {
            return; // Ne pas continuer l'initialisation si l'utilisateur n'est pas autorisé
        }

        // Mettre à jour le label de connexion et la visibilité des boutons
        updateConnectionDisplay();

        // Appliquer les restrictions d'accès
        applyAccessRestrictions();

        // Initialiser les services
        annuaireService = new AnnuaireServiceClient();
        connexionService = new ConnexionServiceClient();
        membresData = FXCollections.observableArrayList();

        // Configurer les colonnes du tableau
        setupTableColumns();

        // Initialiser la ComboBox avec les catégories
        setupCategorieComboBox();

        // Charger tous les membres au démarrage
        chargerTousLesMembres();
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
            ConnexionServiceClient localConnexionService = new ConnexionServiceClient(); // correction du type
            if (!localConnexionService.verifierEtatConnexion(utilisateurConnecte.getId())) {
                // L'utilisateur a été déconnecté ailleurs, déconnecter la session locale
                SessionManager.getInstance().deconnecter();
                showAlert("Session expirée", "Votre session a expiré ou vous avez été déconnecté depuis un autre appareil.", Alert.AlertType.WARNING);
                redirectToLogin();
                return false;
            }
        }

        // Vérifier les autorisations d'accès à cette fonctionnalité
        if (!AuthorizationManager.getInstance().canAccessMemberManagement()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à cette fonctionnalité.", Alert.AlertType.ERROR);
            redirectToMainMenu();
            return false;
        }

        return true;
    }

    private void redirectToLogin() {
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", deconnexionButton != null ? deconnexionButton : retourConnexionButton);
    }

    private void redirectToMainMenu() {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal - Annuaire INF1010", deconnexionButton != null ? deconnexionButton : retourConnexionButton);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

        // Seuls les administrateurs peuvent modifier, supprimer et gérer la liste rouge
        boolean isAdmin = authManager.isAdministrator();

        if (modifierButton != null) {
            modifierButton.setDisable(!isAdmin);
            if (!isAdmin) {
                modifierButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        if (supprimerButton != null) {
            supprimerButton.setDisable(!isAdmin);
            if (!isAdmin) {
                supprimerButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        if (ajouterListeRougeButton != null) {
            ajouterListeRougeButton.setDisable(!isAdmin);
            if (!isAdmin) {
                ajouterListeRougeButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        if (retirerListeRougeButton != null) {
            retirerListeRougeButton.setDisable(!isAdmin);
            if (!isAdmin) {
                retirerListeRougeButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }
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
        matriculeColumn.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        courrielColumn.setCellValueFactory(new PropertyValueFactory<>("adresseCourriel"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaineActivite"));
        statutColumn.setCellValueFactory(cellData -> {
            Personne personne = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                personne.isListeRouge() ? "Liste Rouge" : "Actif"
            );
        });

        // Ajouter le style visuel pour les membres en liste rouge
        membresTableView.setRowFactory(tv -> {
            TableRow<Personne> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isListeRouge()) {
                    // Style pour les membres en liste rouge
                    row.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef5350; -fx-border-width: 0 0 0 4px;");
                } else {
                    // Style normal
                    row.setStyle("");
                }
            });
            return row;
        });

        // Lier les données au tableau
        membresTableView.setItems(membresData);
    }

    private void setupCategorieComboBox() {
        categorieComboBox.setItems(FXCollections.observableArrayList(
            "Toutes les catégories",
            "Professeur",
            "Étudiant",
            "Auxiliaire d'enseignement",
            "Administrateur"
        ));
        categorieComboBox.getSelectionModel().selectFirst();
    }

    private void chargerTousLesMembres() {
        try {
            List<Personne> membres = annuaireService.getAllMembres();
            tousLesMembres = FXCollections.observableArrayList(membres);
            membresData.clear();
            membresData.addAll(tousLesMembres);
            updateLabels("Tous les membres", tousLesMembres.size());
        } catch (Exception e) {
            showErrorMessage("Erreur de chargement", "Impossible de charger les membres : " + e.getMessage());
            tousLesMembres = FXCollections.observableArrayList();
        }
    }

    private void filtrerParCategorie(String categorieStr) {
        try {
            List<Personne> membresFiltres;
            if ("Toutes les catégories".equals(categorieStr)) {
                membresFiltres = tousLesMembres;
            } else {
                Categorie categorie = CategorieUtil.stringToCategorie(categorieStr);
                membresFiltres = tousLesMembres.stream()
                    .filter(p -> p.getCategorie() == categorie)
                    .toList(); // simplification
            }

            membresData.clear();
            membresData.addAll(membresFiltres);

            updateLabels("Catégorie : " + categorieStr, membresFiltres.size());
        } catch (Exception e) {
            showErrorMessage("Erreur de filtrage", "Erreur lors du filtrage par catégorie : " + e.getMessage());
        }
    }

    private void updateLabels(String filter, int count) {
        if (compteurLabel != null) {
            compteurLabel.setText(String.format("%s - %d membre(s)", filter, count));
        }
    }

    private void rafraichirDonnees() {
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();
        if ("Toutes les catégories".equals(selectedCategory)) {
            chargerTousLesMembres();
        } else {
            chargerMembresParCategorie(selectedCategory);
        }
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    @FXML
    private void onCategorieSelected(ActionEvent event) {
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            if ("Toutes les catégories".equals(selectedCategory)) {
                chargerTousLesMembres();
            } else {
                chargerMembresParCategorie(selectedCategory);
            }
        }
    }

    private void chargerMembresParCategorie(String categorieStr) {
        try {
            Categorie categorie = CategorieUtil.stringToCategorie(categorieStr);
            if (categorie != null) {
                List<Personne> membres = annuaireService.getMembresParCategorie(categorie);
                membresData.clear();
                membresData.addAll(membres);
                updateCompteur(membres.size(), categorieStr);
            }
        } catch (Exception e) {
            showErrorMessage("Erreur de filtrage", "Impossible de filtrer par catégorie : " + e.getMessage());
        }
    }

    private void updateCompteur(int count, String filter) {
        if (compteurLabel != null) {
            compteurLabel.setText(count + " membre(s) trouvé(s) - " + filter);
        }
    }

    @FXML
    private void onActualiserClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();
        if ("Toutes les catégories".equals(selectedCategory)) {
            chargerTousLesMembres();
        } else {
            chargerMembresParCategorie(selectedCategory);
        }
        showInfoMessage("Actualisation", "Liste des membres actualisée.");
    }

    @FXML
    private void onModifierClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        if (!AuthorizationManager.getInstance().isAdministrator()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }

        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un membre à modifier.");
            return;
        }

        // Utiliser le système DataTransfer pour passer les données du membre à modifier
        DataTransfer.setMembreAModifier(selectedMembre);
        NavigationHelper.navigateTo("ajouter-modifier-membre.fxml", "Modifier le membre", (Node) event.getSource());
    }

    @FXML
    private void onSupprimerClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        if (!AuthorizationManager.getInstance().isAdministrator()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
            return;
        }

        Personne selectedMembre = membresTableView.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un membre à supprimer.");
            return;
        }

        if (showConfirmationDialog("Supprimer le membre",
            "Êtes-vous sûr de vouloir supprimer " + selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " ?")) {

            try {
                boolean success = annuaireService.supprimerMembre(selectedMembre); // cast (int) retiré si getId() retourne int
                if (success) {
                    showInfoMessage("Suppression réussie", "Le membre a été supprimé avec succès.");
                    onActualiserClicked(event); // Rafraîchir la liste
                } else {
                    showErrorMessage("Erreur de suppression", "Impossible de supprimer le membre.");
                }
            } catch (Exception e) {
                showErrorMessage("Erreur de suppression", "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onAjouterListeRougeClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        if (!AuthorizationManager.getInstance().isAdministrator()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
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
                boolean success = annuaireService.ajouterAListeRouge(selectedMembre.getId()); // cast redondant retiré
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
        if (!AuthorizationManager.getInstance().isAdministrator()) {
            AuthorizationManager.getInstance().showAccessDeniedMessage();
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
                boolean success = annuaireService.retirerDeListeRouge(selectedMembre.getId());
                if (success) {
                    showInfoMessage("Succès", selectedMembre.getNom() + " " + selectedMembre.getPrenom() + " a été retiré de la liste rouge.");
                    rafraichirDonnees();
                } else {
                    showErrorMessage("Erreur", "Impossible de retirer le membre de la liste rouge.");
                }
            } catch (Exception e) {
                // Serveur non disponible → redirection sans alert
                navigateToServiceIndisponible();
            }
        }
    }

    @FXML
    private void onDeconnexionClicked(ActionEvent event) {
        // Déconnecter l'utilisateur
        SessionManager.getInstance().deconnecter();

        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
    }

    @FXML
    private void onRetourConnexionClicked(ActionEvent event) {
        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
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
        Node currentNode = deconnexionButton != null ? deconnexionButton :
                          (retourConnexionButton != null ? retourConnexionButton : membresTableView);
        NavigationHelper.navigateTo("service-indisponible.fxml", "Service Indisponible", currentNode);
    }
}
