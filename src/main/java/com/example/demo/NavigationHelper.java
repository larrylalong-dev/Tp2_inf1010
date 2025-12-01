package com.example.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import com.example.demo.service.AnnuaireServiceClient;

import java.io.IOException;

public class NavigationHelper {

    /**
     * Vérifie si le serveur est disponible et navigue vers la page appropriée
     * @return true si le serveur est disponible, false sinon
     */
    public static boolean checkServerAndNavigate(Node currentNode) {
        AnnuaireServiceClient service = new AnnuaireServiceClient();
        if (!service.isServerAvailable()) {
            navigateTo("service-indisponible.fxml", "Service Indisponible", currentNode);
            return false;
        }
        return true;
    }

    /**
     * Navigation avec vérification automatique du serveur
     */
    public static void navigateToWithServerCheck(String fxmlFile, String title, Node currentNode) {
        if (checkServerAndNavigate(currentNode)) {
            navigateTo(fxmlFile, title, currentNode);
        }
    }

    public static void navigateTo(String fxmlFile, String title, Node currentNode) {
        try {
            // Charger le nouveau contenu FXML avec une méthode robuste
            FXMLLoader loader = new FXMLLoader();

            // Essayer d'abord le chemin absolu
            loader.setLocation(NavigationHelper.class.getResource("/com/example/demo/" + fxmlFile));
            if (loader.getLocation() == null) {
                // Fallback avec le chemin relatif
                loader.setLocation(NavigationHelper.class.getResource(fxmlFile));
            }

            if (loader.getLocation() == null) {
                System.err.println("ERREUR: Impossible de trouver " + fxmlFile);
                throw new RuntimeException("Fichier FXML introuvable: " + fxmlFile);
            }

            Parent root = loader.load();

            Stage stage = (Stage) currentNode.getScene().getWindow();

            // Sauvegarder complètement l'état actuel de la fenêtre
            WindowState currentState = new WindowState(stage);

            // Créer la nouvelle scène en préservant les dimensions actuelles
            Scene currentScene = stage.getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            // Appliquer la nouvelle scène
            stage.setScene(newScene);
            stage.setTitle("Annuaire INF1010 - " + title);

            // Restaurer l'état de la fenêtre après un court délai pour éviter les conflits
            javafx.application.Platform.runLater(() -> {
                currentState.restore(stage);
            });

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible de charger la page : " + title, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur inattendue", "Une erreur s'est produite lors de la navigation.");
        }
    }

    // Classe pour sauvegarder l'état de la fenêtre
    private static class WindowState {
        private final double width;
        private final double height;
        private final double x;
        private final double y;
        private final boolean maximized;
        private final boolean iconified;
        private final boolean fullScreen;

        public WindowState(Stage stage) {
            this.width = stage.getWidth();
            this.height = stage.getHeight();
            this.x = stage.getX();
            this.y = stage.getY();
            this.maximized = stage.isMaximized();
            this.iconified = stage.isIconified();
            this.fullScreen = stage.isFullScreen();
        }

        public void restore(Stage stage) {
            try {
                // Restaurer d'abord les dimensions si pas maximisé
                if (!maximized && !fullScreen) {
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.setX(x);
                    stage.setY(y);
                }

                // Puis restaurer les états spéciaux
                if (maximized) {
                    stage.setMaximized(true);
                }

                if (fullScreen) {
                    stage.setFullScreen(true);
                }

                if (iconified) {
                    stage.setIconified(true);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la restauration de l'état de la fenêtre: " + e.getMessage());
            }
        }
    }

    private static void showError(String header, String content) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
