package com.example.demo.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import com.example.demo.client.ServerConnectionManager;
import com.example.demo.util.SessionManager;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service de surveillance de la connexion au serveur.
 * Vérifie toutes les 3 secondes si le serveur est toujours disponible.
 */
public class ServerMonitorService {

    private static ServerMonitorService instance;
    private Timer monitorTimer;
    private final ServerConnectionManager connectionManager;
    private final ConnexionServiceClient connexionService;
    private final AtomicBoolean isMonitoring = new AtomicBoolean(false);
    private final AtomicBoolean alertShown = new AtomicBoolean(false);
    private Runnable onServerDisconnected;

    private static final int CHECK_INTERVAL_MS = 3000; // 3 secondes

    private ServerMonitorService() {
        this.connectionManager = ServerConnectionManager.getInstance();
        this.connexionService = new ConnexionServiceClient();
    }

    public static ServerMonitorService getInstance() {
        if (instance == null) {
            synchronized (ServerMonitorService.class) {
                if (instance == null) {
                    instance = new ServerMonitorService();
                }
            }
        }
        return instance;
    }

    /**
     * Démarre la surveillance de la connexion serveur
     * @param onServerDisconnected Callback à exécuter si le serveur se déconnecte
     */
    public void startMonitoring(Runnable onServerDisconnected) {
        if (isMonitoring.get()) {
            System.out.println("[MONITOR] Surveillance déjà active");
            return;
        }

        this.onServerDisconnected = onServerDisconnected;
        this.alertShown.set(false);

        monitorTimer = new Timer("ServerMonitor", true);
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkServerConnection();
            }
        }, CHECK_INTERVAL_MS, CHECK_INTERVAL_MS);

        isMonitoring.set(true);
        System.out.println("[MONITOR] Surveillance du serveur démarrée (vérification toutes les 3 secondes)");
    }

    /**
     * Arrête la surveillance de la connexion serveur
     */
    public void stopMonitoring() {
        if (!isMonitoring.get()) {
            return;
        }

        if (monitorTimer != null) {
            monitorTimer.cancel();
            monitorTimer = null;
        }

        isMonitoring.set(false);
        alertShown.set(false);
        System.out.println("[MONITOR] Surveillance du serveur arrêtée");
    }

    /**
     * Vérifie la connexion au serveur
     */
    private void checkServerConnection() {
        try {
            // Vérifier si l'utilisateur est connecté
            if (!SessionManager.getInstance().isConnecte()) {
                // Aucun utilisateur connecté, pas besoin de surveiller
                return;
            }

            // Tester la connexion au serveur
            boolean serverAvailable = connectionManager.pingServer();

            if (!serverAvailable) {
                // Essayer de reconnecter une fois
                serverAvailable = connectionManager.connect() && connectionManager.pingServer();
            }

            if (!serverAvailable && !alertShown.get()) {
                // Le serveur n'est plus disponible et on n'a pas encore affiché l'alerte
                handleServerDisconnection();
            }

        } catch (Exception e) {
            System.err.println("[MONITOR] Erreur lors de la vérification: " + e.getMessage());
            if (!alertShown.get()) {
                handleServerDisconnection();
            }
        }
    }

    /**
     * Gère la déconnexion du serveur
     */
    private void handleServerDisconnection() {
        if (!alertShown.compareAndSet(false, true)) {
            // Alerte déjà affichée
            return;
        }

        // Arrêter la surveillance temporairement
        stopMonitoring();

        // Déconnecter l'utilisateur de la session
        int userId = SessionManager.getInstance().getUtilisateurConnecte() != null
            ? SessionManager.getInstance().getUtilisateurConnecte().getId()
            : -1;

        if (userId > 0) {
            try {
                // Tenter de marquer l'utilisateur comme déconnecté (peut échouer si le serveur est down)
                connexionService.marquerUtilisateurDeconnecte(userId);
            } catch (Exception e) {
                // Ignorer l'erreur, le serveur est probablement indisponible
            }
        }

        // Afficher le dialogue sur le thread JavaFX
        Platform.runLater(() -> {
            showServerDisconnectedDialog();
        });
    }

    /**
     * Affiche le dialogue de déconnexion du serveur
     */
    private void showServerDisconnectedDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Serveur Déconnecté");
        alert.setHeaderText("⚠️ Connexion au serveur perdue");
        alert.setContentText(
            "Le serveur n'est plus disponible.\n\n" +
            "Votre session a été interrompue.\n\n" +
            "Cliquez sur OK pour accéder à la page de reconnexion.\n" +
            "L'application vérifiera automatiquement si le serveur est de nouveau disponible."
        );

        // Personnaliser le bouton
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);

        // Bloquer jusqu'à ce que l'utilisateur clique sur OK
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == okButton) {
            // Déconnecter la session
            SessionManager.getInstance().deconnecter();
            connectionManager.disconnect();

            // Rediriger vers la page "service indisponible"
            redirectToServiceIndisponible();
        }
    }

    /**
     * Redirige vers la page "service indisponible"
     */
    private void redirectToServiceIndisponible() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/example/demo/service-indisponible.fxml")
            );
            javafx.scene.Parent root = loader.load();

            // Trouver la fenêtre active
            javafx.stage.Stage stage = findActiveStage();

            if (stage != null) {
                double currentWidth = stage.getWidth();
                double currentHeight = stage.getHeight();

                javafx.scene.Scene scene = new javafx.scene.Scene(root, currentWidth, currentHeight);
                stage.setScene(scene);
                stage.setTitle("Service Temporairement Indisponible");

                System.out.println("[MONITOR] Redirection vers page service indisponible");
            } else {
                System.err.println("[MONITOR] Impossible de trouver la fenêtre active");
                // Fallback: exécuter le callback s'il existe
                if (onServerDisconnected != null) {
                    onServerDisconnected.run();
                }
            }
        } catch (Exception e) {
            System.err.println("[MONITOR] Erreur lors de la redirection: " + e.getMessage());
            e.printStackTrace();
            // Fallback: exécuter le callback s'il existe
            if (onServerDisconnected != null) {
                onServerDisconnected.run();
            }
        }
    }

    /**
     * Trouve la fenêtre (Stage) active de l'application
     */
    private javafx.stage.Stage findActiveStage() {
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window instanceof javafx.stage.Stage && window.isShowing()) {
                return (javafx.stage.Stage) window;
            }
        }
        return null;
    }

    /**
     * Vérifie si la surveillance est active
     */
    public boolean isMonitoring() {
        return isMonitoring.get();
    }
}

