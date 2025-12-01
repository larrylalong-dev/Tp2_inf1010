package com.example.demo;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import Entite.Personne;
import com.example.demo.service.PersonneService;
import com.example.demo.service.ConnexionService;
import CategorieEnum.Categorie;
import com.example.demo.util.DataTransfer;
import com.example.demo.util.SessionManager;
import com.example.demo.util.AuthorizationManager;
import com.example.demo.enums.CategorieDisplay;
import com.example.demo.enums.DomaineActivite;

public class AjouterModifierMembreController {

    @FXML
    private Label connexionLabel;

    @FXML
    private Button deconnexionButton;

    @FXML
    private Button retourConnexionButton;

    @FXML
    private Label titreLabel;

    @FXML
    private TextField nomTextField;

    @FXML
    private TextField prenomTextField;

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private Label matriculeLabel;

    @FXML
    private TextField matriculeTextField;

    @FXML
    private TextField courrielTextField;

    @FXML
    private Label telephoneLabel;

    @FXML
    private TextField telephoneTextField;

    @FXML
    private ComboBox<String> domaineComboBox;

    @FXML
    private Label modifierMotDePasseLabel;

    @FXML
    private Button modifierMotDePasseButton;

    @FXML
    private Label motDePasseLabel;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private Label confirmerMotDePasseLabel;

    @FXML
    private PasswordField confirmerMotDePasseField;

    // Service pour accéder aux données
    private PersonneService personneService;
    private ConnexionService connexionService;
    private Personne membreAModifier; // Pour savoir si on est en mode modification
    private boolean modeModification = false;
    private boolean afficherChampsMotDePasse = true; // Pour gérer l'affichage des champs mot de passe

    @FXML
    private void initialize() {
        // Vérifier l'état de connexion de l'utilisateur
        if (!verifierEtatConnexion()) {
            return; // Ne pas continuer l'initialisation si l'utilisateur n'est pas autorisé
        }

        // Initialiser les services
        personneService = new PersonneService();
        connexionService = new ConnexionService();

        // Mettre à jour l'affichage de connexion
        updateConnectionDisplay();

        // Configurer les ComboBoxes
        setupCategorieComboBox();
        setupDomaineComboBox();

        // Vérifier s'il y a un membre à modifier
        if (DataTransfer.hasMembreAModifier()) {
            initializerModeModification(DataTransfer.getMembreAModifier());
            // Nettoyer après récupération
            DataTransfer.clearMembreAModifier();
        } else {
            // Initialiser le mode ajout par défaut
            initializerModeAjout();
        }
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
            ConnexionService connexionService = new ConnexionService();
            if (!connexionService.verifierEtatConnexion(utilisateurConnecte.getId())) {
                // L'utilisateur a été déconnecté ailleurs, déconnecter la session locale
                SessionManager.getInstance().deconnecter();
                showAlert("Session expirée", "Votre session a expiré ou vous avez été déconnecté depuis un autre appareil.", Alert.AlertType.WARNING);
                redirectToLogin();
                return false;
            }
        }

        // Vérifier les autorisations d'accès à cette fonctionnalité
        if (!AuthorizationManager.getInstance().canAddMembers()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à cette fonctionnalité.", Alert.AlertType.ERROR);
            redirectToMainMenu();
            return false;
        }

        return true;
    }

    private void redirectToLogin() {
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", nomTextField != null ? nomTextField : deconnexionButton);
    }

    private void redirectToMainMenu() {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal - Annuaire INF1010", nomTextField != null ? nomTextField : deconnexionButton);
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

    @FXML
    private void onRetourConnexionClicked(ActionEvent event) {
        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
    }

    @FXML
    private void onDeconnexionClicked(ActionEvent event) {
        // Déconnecter l'utilisateur
        SessionManager.getInstance().deconnecter();

        // Rediriger vers la page de connexion
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010", (Node) event.getSource());
    }

    private void setupCategorieComboBox() {
        categorieComboBox.setItems(FXCollections.observableArrayList(
            CategorieDisplay.getAllDisplayTexts()
        ));
        categorieComboBox.getSelectionModel().selectFirst();
        onCategorieChanged(); // Appliquer les changements de visibilité
    }

    private void setupDomaineComboBox() {
        domaineComboBox.setItems(FXCollections.observableArrayList(
            DomaineActivite.getAllDisplayTexts()
        ));
        domaineComboBox.getSelectionModel().selectFirst();
    }

    private void initializerModeAjout() {
        modeModification = false;
        titreLabel.setText("➕ Nouveau Membre");
        membreAModifier = null;
        afficherChampsMotDePasse = true; // Afficher les champs en mode ajout
        clearForm();
        updatePasswordFieldsVisibility(); // Mettre à jour la visibilité des champs
    }

    private void initializerModeModification(Personne personne) {
        modeModification = true;
        titreLabel.setText("✏️ Modifier le Membre");
        membreAModifier = personne;
        afficherChampsMotDePasse = false; // Masquer les champs par défaut en mode modification
        populateForm(personne);
        updatePasswordFieldsVisibility(); // Mettre à jour la visibilité des champs
    }

    /**
     * Méthode appelée quand l'utilisateur clique sur le bouton "Modifier le mot de passe"
     */
    @FXML
    private void onModifierMotDePasseClicked(ActionEvent event) {
        afficherChampsMotDePasse = !afficherChampsMotDePasse;
        updatePasswordFieldsVisibility();

        // Vider les champs quand on les affiche pour éviter la confusion
        if (afficherChampsMotDePasse) {
            motDePasseField.clear();
            confirmerMotDePasseField.clear();
        }
    }

    /**
     * Met à jour la visibilité des champs de mot de passe selon le mode et l'état
     */
    private void updatePasswordFieldsVisibility() {
        if (modeModification) {
            // En mode modification, afficher le bouton et masquer/afficher les champs selon l'état
            modifierMotDePasseButton.setVisible(true);
            motDePasseLabel.setVisible(afficherChampsMotDePasse);
            motDePasseField.setVisible(afficherChampsMotDePasse);
            confirmerMotDePasseLabel.setVisible(afficherChampsMotDePasse);
            confirmerMotDePasseField.setVisible(afficherChampsMotDePasse);

            // Mettre à jour le texte du bouton
            if (afficherChampsMotDePasse) {
                modifierMotDePasseButton.setText("🔒 Annuler modification mot de passe");
                modifierMotDePasseButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 15px; -fx-cursor: hand;");
            } else {
                modifierMotDePasseButton.setText("🔐 Modifier le mot de passe");
                modifierMotDePasseButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 15px; -fx-cursor: hand;");
            }
        } else {
            // En mode ajout, masquer le bouton et afficher les champs
            modifierMotDePasseButton.setVisible(false);
            motDePasseLabel.setVisible(true);
            motDePasseField.setVisible(true);
            confirmerMotDePasseLabel.setVisible(true);
            confirmerMotDePasseField.setVisible(true);
            afficherChampsMotDePasse = true;
        }
    }

    private void populateForm(Personne personne) {
        nomTextField.setText(personne.getNom() != null ? personne.getNom() : "");
        prenomTextField.setText(personne.getPrenom() != null ? personne.getPrenom() : "");

        // Définir la catégorie avec les emojis
        String categorieStr = getCategorieDisplayText(personne.getCategorie());
        categorieComboBox.setValue(categorieStr);

        matriculeTextField.setText(personne.getMatricule() != null ? personne.getMatricule() : "");
        courrielTextField.setText(personne.getAdresseCourriel() != null ? personne.getAdresseCourriel() : "");
        telephoneTextField.setText(personne.getTelephone() != null ? personne.getTelephone() : "");

        // Définir le domaine
        if (personne.getDomaineActivite() != null && !personne.getDomaineActivite().isEmpty()) {
            domaineComboBox.setValue(personne.getDomaineActivite());
        } else {
            domaineComboBox.getSelectionModel().selectFirst();
        }

        // Pour la modification, on affiche un placeholder pour le mot de passe
        if (modeModification) {
            motDePasseField.setPromptText("Laisser vide pour conserver le mot de passe actuel");
            confirmerMotDePasseField.setPromptText("Laisser vide pour conserver le mot de passe actuel");
        }

        onCategorieChanged(); // Mise à jour de la visibilité des champs
    }

    private String getCategorieDisplayText(Categorie categorie) {
        return CategorieDisplay.fromCategorie(categorie).getDisplayText();
    }

    private Categorie getCategorieFromDisplayText(String displayText) {
        return CategorieDisplay.fromDisplayText(displayText).getCategorie();
    }

    private void clearForm() {
        nomTextField.clear();
        prenomTextField.clear();
        categorieComboBox.getSelectionModel().selectFirst();
        matriculeTextField.clear();
        courrielTextField.clear();
        telephoneTextField.clear();
        domaineComboBox.getSelectionModel().selectFirst();
        motDePasseField.clear();
        confirmerMotDePasseField.clear();

        onCategorieChanged();
    }

    @FXML
    private void onRetourClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    @FXML
    private void onCategorieChanged(ActionEvent event) {
        onCategorieChanged();
    }

    private void onCategorieChanged() {
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            // Afficher/masquer les champs selon la catégorie
            boolean isEtudiant = selectedCategory.contains("Étudiant");
            boolean isProfesseurOuAuxiliaire = selectedCategory.contains("Professeur") ||
                                               selectedCategory.contains("Auxiliaire");

            // Champ matricule visible seulement pour les étudiants
            matriculeLabel.setVisible(isEtudiant);
            matriculeTextField.setVisible(isEtudiant);

            // Champ téléphone visible seulement pour professeurs et auxiliaires
            telephoneLabel.setVisible(isProfesseurOuAuxiliaire);
            telephoneTextField.setVisible(isProfesseurOuAuxiliaire);
        }
    }

    @FXML
    private void onEnregistrerClicked(ActionEvent event) {
        if (validateForm()) {
            try {
                Personne personne = createPersonneFromForm();
                boolean success;

                if (modeModification && membreAModifier != null) {
                    // Mode modification
                    personne.setId(membreAModifier.getId());
                    personne.setListeRouge(membreAModifier.isListeRouge()); // Préserver le statut liste rouge
                    success = personneService.modifierMembre(personne);

                    if (success) {
                        showInfoMessage("Modification réussie", "Le membre a été modifié avec succès!");
                    } else {
                        showErrorMessage("Erreur de modification", "Impossible de modifier le membre.");
                        return;
                    }
                } else {
                    // Mode ajout
                    personne.setListeRouge(false); // Nouveau membre pas sur liste rouge par défaut
                    success = personneService.ajouterMembre(personne);

                    if (success) {
                        showInfoMessage("Ajout réussi", "Le membre a été ajouté avec succès!");
                    } else {
                        showErrorMessage("Erreur d'ajout", "Impossible d'ajouter le membre.");
                        return;
                    }
                }

                // Retourner au menu principal après succès
                NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());

            } catch (Exception e) {
                showErrorMessage("Erreur", "Une erreur s'est produite : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onAnnulerClicked(ActionEvent event) {
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal", (Node) event.getSource());
    }

    private boolean validateForm() {
        StringBuilder erreurs = new StringBuilder();

        // Validation du nom
        if (nomTextField.getText() == null || nomTextField.getText().trim().isEmpty()) {
            erreurs.append("- Le nom est obligatoire\n");
        }

        // Validation du prénom
        if (prenomTextField.getText() == null || prenomTextField.getText().trim().isEmpty()) {
            erreurs.append("- Le prénom est obligatoire\n");
        }

        // Validation du courriel
        if (courrielTextField.getText() == null || courrielTextField.getText().trim().isEmpty()) {
            erreurs.append("- Le courriel est obligatoire\n");
        } else if (!isValidEmail(courrielTextField.getText().trim())) {
            erreurs.append("- Le format du courriel n'est pas valide\n");
        }

        // Validation du matricule pour les étudiants
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null && selectedCategory.contains("Étudiant")) {
            if (matriculeTextField.getText() == null || matriculeTextField.getText().trim().isEmpty()) {
                erreurs.append("- Le matricule est obligatoire pour les étudiants\n");
            }
        }

        // Validation du téléphone pour professeurs et auxiliaires
        if (selectedCategory != null && (selectedCategory.contains("Professeur") || selectedCategory.contains("Auxiliaire"))) {
            if (telephoneTextField.getText() == null || telephoneTextField.getText().trim().isEmpty()) {
                erreurs.append("- Le téléphone est obligatoire pour les professeurs et auxiliaires\n");
            } else if (!isValidPhoneNumber(telephoneTextField.getText().trim())) {
                erreurs.append("- Le format du téléphone n'est pas valide\n");
            }
        }

        // Validation du mot de passe - différente selon le mode et la visibilité
        if (modeModification) {
            // En mode modification, valider seulement si les champs sont visibles et remplis
            if (afficherChampsMotDePasse && motDePasseField.isVisible()) {
                if (motDePasseField.getText() != null && !motDePasseField.getText().trim().isEmpty()) {
                    if (motDePasseField.getText().length() < 6) {
                        erreurs.append("- Le mot de passe doit contenir au moins 6 caractères\n");
                    }
                    // Validation de la confirmation seulement si un nouveau mot de passe est saisi
                    if (!confirmerMotDePasseField.getText().equals(motDePasseField.getText())) {
                        erreurs.append("- Les mots de passe ne correspondent pas\n");
                    }
                }
            }
        } else {
            // En mode ajout, le mot de passe est obligatoire
            if (motDePasseField.getText() == null || motDePasseField.getText().trim().isEmpty()) {
                erreurs.append("- Le mot de passe est obligatoire\n");
            } else if (motDePasseField.getText().length() < 6) {
                erreurs.append("- Le mot de passe doit contenir au moins 6 caractères\n");
            }

            // Validation de la confirmation du mot de passe
            if (confirmerMotDePasseField.getText() == null || confirmerMotDePasseField.getText().trim().isEmpty()) {
                erreurs.append("- La confirmation du mot de passe est obligatoire\n");
            } else if (!confirmerMotDePasseField.getText().equals(motDePasseField.getText())) {
                erreurs.append("- Les mots de passe ne correspondent pas\n");
            }
        }

        if (!erreurs.isEmpty()) {
            showErrorMessage("Erreurs de validation", erreurs.toString());
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Valide le format d'un numéro de téléphone
     * Accepte les formats : 1234567890, 123-456-7890, (123) 456-7890, 123.456.7890
     * @param phone Le numéro de téléphone à valider
     * @return true si le format est valide, false sinon
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Nettoyer le numéro en gardant seulement les chiffres
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        // Vérifier que nous avons exactement 10 chiffres (format nord-américain)
        if (cleanPhone.length() != 10) {
            return false;
        }

        // Vérifier les formats acceptés avec regex
        String phonePattern = "^(\\(?[0-9]{3}\\)?[-. ]?[0-9]{3}[-. ]?[0-9]{4})$";
        return phone.matches(phonePattern);
    }

    private Personne createPersonneFromForm() {
        Personne personne = new Personne();

        personne.setNom(nomTextField.getText().trim());
        personne.setPrenom(prenomTextField.getText().trim());
        personne.setAdresseCourriel(courrielTextField.getText().trim());

        // Convertir la catégorie
        String selectedCategory = categorieComboBox.getSelectionModel().getSelectedItem();
        personne.setCategorie(getCategorieFromDisplayText(selectedCategory));

        // Matricule (seulement pour étudiants)
        if (matriculeTextField.isVisible() && matriculeTextField.getText() != null) {
            personne.setMatricule(matriculeTextField.getText().trim());
        }

        // Téléphone (seulement pour professeurs et auxiliaires)
        if (telephoneTextField.isVisible() && telephoneTextField.getText() != null) {
            personne.setTelephone(telephoneTextField.getText().trim());
        }

        // Domaine d'activité
        if (domaineComboBox.getValue() != null) {
            personne.setDomaineActivite(domaineComboBox.getValue());
        }

        // Gestion du mot de passe selon le mode
        if (modeModification) {
            // En mode modification, conserver le mot de passe actuel si les champs ne sont pas visibles
            // ou si ils sont vides
            if (afficherChampsMotDePasse && motDePasseField.isVisible() &&
                motDePasseField.getText() != null && !motDePasseField.getText().trim().isEmpty()) {
                personne.setMotDePasse(motDePasseField.getText().trim());
            } else {
                // Conserver le mot de passe existant
                personne.setMotDePasse(membreAModifier.getMotDePasse());
            }
        } else {
            // En mode ajout, le mot de passe est obligatoire
            if (motDePasseField.getText() != null && !motDePasseField.getText().trim().isEmpty()) {
                personne.setMotDePasse(motDePasseField.getText().trim());
            }
        }

        return personne;
    }

    private void showInfoMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
