package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;
import com.example.demo.client.ServerConnectionManager;

/**
 * Contrôleur pour la page de service indisponible
 * Affichée quand le serveur n'est pas accessible
 */
public class ServiceIndisponibleController {

    @FXML
    private Label messageLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button reessayerButton;

    @FXML
    private Button quitterButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private Timeline checkTimeline;
    private int tentativeCount = 0;

    @FXML
    private void initialize() {
        ServerConnectionManager connectionManager = ServerConnectionManager.getInstance();

        // Afficher les détails de connexion
        detailsLabel.setText(String.format(
            "Serveur: %s:%d\nVérifiez que le serveur est démarré.",
            connectionManager.getServerHost(),
            connectionManager.getServerPort()
        ));

        // Démarrer la vérification automatique toutes les 5 secondes
        startAutoCheck();
    }

    /**
     * Démarre la vérification automatique de la disponibilité du serveur
     */
    private void startAutoCheck() {
        checkTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            checkServerAvailability();
        }));
        checkTimeline.setCycleCount(Timeline.INDEFINITE);
        checkTimeline.play();
    }

    /**
     * Vérifie la disponibilité du serveur
     */
    private void checkServerAvailability() {
        tentativeCount++;
        statusLabel.setText("Vérification... (tentative " + tentativeCount + ")");

        ServerConnectionManager connectionManager = ServerConnectionManager.getInstance();

        if (connectionManager.isServerAvailable()) {
            statusLabel.setText("✅ Serveur détecté ! Reconnexion...");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Arrêter la vérification automatique
            if (checkTimeline != null) {
                checkTimeline.stop();
            }

            // Retourner au menu principal après un court délai
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                retournerAuMenuPrincipal();
            }));
            delay.play();
        } else {
            statusLabel.setText("❌ Serveur toujours indisponible (tentative " + tentativeCount + ")");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void onReessayerClicked(ActionEvent event) {
        progressIndicator.setVisible(true);
        reessayerButton.setDisable(true);

        statusLabel.setText("Tentative de reconnexion...");
        statusLabel.setStyle("-fx-text-fill: blue;");

        // Vérifier dans un thread séparé pour ne pas bloquer l'UI
        new Thread(() -> {
            ServerConnectionManager connectionManager = ServerConnectionManager.getInstance();

            if (connectionManager.isServerAvailable()) {
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("✅ Connexion rétablie !");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    retournerAuMenuPrincipal();
                });
            } else {
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("❌ Serveur toujours indisponible");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    progressIndicator.setVisible(false);
                    reessayerButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onQuitterClicked(ActionEvent event) {
        if (checkTimeline != null) {
            checkTimeline.stop();
        }
        System.exit(0);
    }

    /**
     * Retourne au menu principal une fois la connexion rétablie
     */
    private void retournerAuMenuPrincipal() {
        if (checkTimeline != null) {
            checkTimeline.stop();
        }

        // Rediriger vers le menu principal
        NavigationHelper.navigateTo("main-menu.fxml", "Menu Principal - Annuaire INF1010",
                                    messageLabel);
    }

    @FXML
    private void onRetourConnexionClicked(ActionEvent event) {
        if (checkTimeline != null) {
            checkTimeline.stop();
        }
        NavigationHelper.navigateTo("login.fxml", "Connexion - Annuaire INF1010",
                                    (Node) event.getSource());
    }
}

