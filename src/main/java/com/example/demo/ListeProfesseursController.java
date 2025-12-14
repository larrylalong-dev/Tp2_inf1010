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
import CategorieEnum.Categorie;
import com.example.demo.util.DataTransfer;
import com.example.demo.util.AuthorizationManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.CategorieUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ListeProfesseursController {

    @FXML
    private ComboBox<String> domaineComboBox;

    @FXML
    private TextField domainePersonnaliseTextField;

    @FXML
    private Label resultatsLabel;

    @FXML
    private TableView<Personne> professeursTableView;

    @FXML
    private TableColumn<Personne, String> nomColumn;

    @FXML
    private TableColumn<Personne, String> prenomColumn;

    @FXML
    private TableColumn<Personne, String> categorieColumn;

    @FXML
    private TableColumn<Personne, String> courrielColumn;

    @FXML
    private TableColumn<Personne, String> telephoneColumn;

    @FXML
    private TableColumn<Personne, String> domaineColumn;

    @FXML
    private TableColumn<Personne, String> statutColumn;

    @FXML
    private Label statistiquesLabel;

    // Boutons d'action - pour appliquer les restrictions d'accès
    @FXML
    private Button voirDetailsButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button exporterButton;

    // Service pour accéder aux données
    private AnnuaireServiceClient annuaireService;
    private ObservableList<Personne> professeursData;
    private List<Personne> tousProfesseurs;

    @FXML
    private void initialize() {
        // Initialiser le service
        annuaireService = new AnnuaireServiceClient();

        // Vérifier disponibilité serveur immédiate
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }

        professeursData = FXCollections.observableArrayList();

        // Appliquer les restrictions d'accès selon le rôle de l'utilisateur
        applyAccessRestrictions();

        // Configurer le tableau
        setupTableColumns();

        // Configurer la ComboBox des domaines
        setupDomaineComboBox();

        // Charger les professeurs (l'affichage se fera automatiquement après le chargement)
        chargerTousProfesseurs();
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
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaineActivite"));
        statutColumn.setCellValueFactory(cellData -> {
            Personne personne = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                personne.isListeRouge() ? "Liste Rouge" : "Actif"
            );
        });

        // Ajouter le style visuel pour les membres en liste rouge
        professeursTableView.setRowFactory(tv -> {
            TableRow<Personne> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isListeRouge()) {
                    // Style pour les professeurs en liste rouge
                    row.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef5350; -fx-border-width: 0 0 0 4px;");
                } else {
                    // Style normal
                    row.setStyle("");
                }
            });
            return row;
        });

        // Lier les données au tableau
        professeursTableView.setItems(professeursData);
    }

    private void setupDomaineComboBox() {
        domaineComboBox.setItems(FXCollections.observableArrayList(
            "Tous les domaines",
            "Informatique",
            "Réseaux",
                "math",
            "Intelligence artificielle",
            "Bases de données",
            "Génie logiciel",
            "Sécurité informatique",
            "Autre (personnalisé)"
        ));
        domaineComboBox.getSelectionModel().selectFirst();
    }

    private void chargerTousProfesseurs() {
        if (!annuaireService.isServerAvailable()) {
            navigateToServiceIndisponible();
            return;
        }

        // Charger les données en arrière-plan
        javafx.concurrent.Task<ObservableList<Personne>> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected ObservableList<Personne> call() throws Exception {
                // Charger tous les professeurs et auxiliaires
                List<Personne> professeurs = annuaireService.getMembresParCategorie(Categorie.professeur);
                List<Personne> auxiliaires = annuaireService.getMembresParCategorie(Categorie.auxiliaire);

                ObservableList<Personne> result = FXCollections.observableArrayList();
                result.addAll(professeurs);
                result.addAll(auxiliaires);
                return result;
            }
        };

        loadTask.setOnSucceeded(e -> {
            tousProfesseurs = loadTask.getValue();
            afficherTousProfesseurs();
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            showErrorMessage("Erreur de chargement", "Impossible de charger les professeurs : " + exception.getMessage());
            tousProfesseurs = FXCollections.observableArrayList();
        });

        new Thread(loadTask).start();
    }

    private void afficherTousProfesseurs() {
        professeursData.clear();
        professeursData.addAll(tousProfesseurs);

        updateLabels("Tous les professeurs", tousProfesseurs.size());
    }

    private void updateLabels(String filter, int count) {
        resultatsLabel.setText(filter);

        long professeurs = tousProfesseurs.stream().filter(p -> p.getCategorie() == Categorie.professeur).count();
        long auxiliaires = tousProfesseurs.stream().filter(p -> p.getCategorie() == Categorie.auxiliaire).count();
        long listeRouge = tousProfesseurs.stream().filter(Personne::isListeRouge).count();

        statistiquesLabel.setText(String.format(
            "Affichés: %d | Professeurs: %d | Auxiliaires: %d | Liste Rouge: %d",
            count, professeurs, auxiliaires, listeRouge));
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    @FXML
    private void onDomaineSelected(ActionEvent event) {
        String selectedDomaine = domaineComboBox.getSelectionModel().getSelectedItem();

        if ("Autre (personnalisé)".equals(selectedDomaine)) {
            domainePersonnaliseTextField.setVisible(true);
        } else {
            domainePersonnaliseTextField.setVisible(false);
            if (selectedDomaine != null && !"Tous les domaines".equals(selectedDomaine)) {
                filtrerParDomaine(selectedDomaine);
            } else if ("Tous les domaines".equals(selectedDomaine)) {
                afficherTousProfesseurs();
            }
        }
    }

    private void filtrerParDomaine(String domaine) {
        try {
            List<Personne> professeursFiltres = tousProfesseurs.stream()
                .filter(p -> p.getDomaineActivite() != null &&
                           p.getDomaineActivite().toLowerCase().contains(domaine.toLowerCase()))
                .collect(Collectors.toList());

            professeursData.clear();
            professeursData.addAll(professeursFiltres);

            updateLabels("Professeurs en " + domaine, professeursFiltres.size());
        } catch (Exception e) {
            showErrorMessage("Erreur de filtrage", "Erreur lors du filtrage par domaine : " + e.getMessage());
        }
    }

    @FXML
    private void onRechercherClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        String domaine = domaineComboBox.getSelectionModel().getSelectedItem();

        if (domainePersonnaliseTextField.isVisible() && !domainePersonnaliseTextField.getText().trim().isEmpty()) {
            domaine = domainePersonnaliseTextField.getText().trim();
            filtrerParDomaine(domaine);
        } else if ("Autre (personnalisé)".equals(domaine)) {
            showWarningMessage("Domaine non spécifié", "Veuillez saisir un domaine d'activité personnalisé.");
        } else {
            // Déjà filtré par onDomaineSelected
            showInfoMessage("Recherche", "Recherche effectuée pour le domaine : \"" + domaine + "\"");
        }
    }

    @FXML
    private void onActualiserClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        chargerTousProfesseurs();
        String selectedDomaine = domaineComboBox.getSelectionModel().getSelectedItem();

        if ("Tous les domaines".equals(selectedDomaine)) {
            afficherTousProfesseurs();
        } else if (!"Autre (personnalisé)".equals(selectedDomaine)) {
            filtrerParDomaine(selectedDomaine);
        }

        showInfoMessage("Actualisation", "Liste des professeurs actualisée.");
    }

    @FXML
    private void onVoirDetailsClicked(ActionEvent event) {
        Personne selectedProfesseur = professeursTableView.getSelectionModel().getSelectedItem();
        if (selectedProfesseur == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un professeur pour voir ses détails.");
            return;
        }

        // Afficher les détails dans une boîte de dialogue pour l'instant
        showInfoMessage("Détails du professeur",
            "Nom: " + selectedProfesseur.getNom() + "\n" +
            "Prénom: " + selectedProfesseur.getPrenom() + "\n" +
            "Catégorie: " + CategorieUtil.categorieToString(selectedProfesseur.getCategorie()) + "\n" +
            "Email: " + selectedProfesseur.getAdresseCourriel() + "\n" +
            "Téléphone: " + (selectedProfesseur.getTelephone() != null ? selectedProfesseur.getTelephone() : "N/A") + "\n" +
            "Domaine: " + (selectedProfesseur.getDomaineActivite() != null ? selectedProfesseur.getDomaineActivite() : "N/A") + "\n" +
            "Statut: " + (selectedProfesseur.isListeRouge() ? "Liste Rouge" : "Actif"));
    }

    @FXML
    private void onModifierClicked(ActionEvent event) {
        if (!annuaireService.isServerAvailable()) { navigateToServiceIndisponible(); return; }
        Personne selectedProfesseur = professeursTableView.getSelectionModel().getSelectedItem();
        if (selectedProfesseur == null) {
            showWarningMessage("Aucune sélection", "Veuillez sélectionner un professeur à modifier.");
            return;
        }

        // Utiliser le système DataTransfer pour passer les données du membre à modifier
        DataTransfer.setMembreAModifier(selectedProfesseur);
        NavigationHelper.navigateTo("ajouter-modifier-membre.fxml", "Modifier le professeur", (Node) event.getSource());
    }

    @FXML
    private void onExporterClicked(ActionEvent event) {
        if (professeursData.isEmpty()) {
            showWarningMessage("Liste vide", "Aucun professeur à exporter.");
            return;
        }

        // TODO: Implémenter l'export réel
        showInfoMessage("Export", "Export de " + professeursData.size() + " professeur(s).\n(Fonctionnalité d'export à implémenter)");
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

    private void applyAccessRestrictions() {
        AuthorizationManager authManager = AuthorizationManager.getInstance();

        // Seuls les administrateurs peuvent modifier et exporter
        boolean isAdmin = authManager.isAdministrator();

        if (modifierButton != null) {
            modifierButton.setDisable(!isAdmin);
            if (!isAdmin) {
                modifierButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }

        if (exporterButton != null) {
            exporterButton.setDisable(!isAdmin);
            if (!isAdmin) {
                exporterButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-opacity: 0.6;");
            }
        }
    }

    private void navigateToServiceIndisponible() {
        NavigationHelper.navigateTo("service-indisponible.fxml", "Service Indisponible", professeursTableView);
    }
}
