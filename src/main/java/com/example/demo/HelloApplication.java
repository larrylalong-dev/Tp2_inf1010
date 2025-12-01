package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.demo.service.ConnexionService;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Déconnecter tous les utilisateurs au démarrage de l'application
        try {
            ConnexionService connexionService = new ConnexionService();
            connexionService.deconnecterTousLesUtilisateurs();
            System.out.println("Tous les utilisateurs ont été déconnectés au démarrage de l'application.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion des utilisateurs au démarrage : " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(HelloApplication.class.getResource("/com/example/demo/login.fxml"));
        if (fxmlLoader.getLocation() == null) {
            // Fallback si le chemin absolu ne fonctionne pas
            fxmlLoader.setLocation(HelloApplication.class.getResource("login.fxml"));
        }

        if (fxmlLoader.getLocation() == null) {
            System.err.println("ERREUR: Impossible de trouver login.fxml");
            System.err.println("Vérification des ressources disponibles:");
            // Lister les ressources disponibles pour debug
            try {
                var resource = HelloApplication.class.getResource("/");
                System.err.println("Racine des ressources: " + resource);
                resource = HelloApplication.class.getResource("/com/example/demo/");
                System.err.println("Dossier demo: " + resource);
            } catch (Exception ex) {
                System.err.println("Impossible de lister les ressources: " + ex.getMessage());
            }
            throw new RuntimeException("Fichier login.fxml introuvable");
        }

        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setTitle("Annuaire INF1010 - Connexion");
        stage.setScene(scene);
        stage.setMinWidth(800);  // Réduire la largeur minimale
        stage.setMinHeight(600); // Réduire la hauteur minimale

        // Permettre le redimensionnement et la maximisation
        stage.setResizable(true);
        stage.setMaximized(false);

        // Centrer la fenêtre sur l'écran
        stage.centerOnScreen();

        stage.show();
    }
}
