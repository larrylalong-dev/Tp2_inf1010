package com.example.demo.util;

import com.example.demo.NavigationHelper;
import com.example.demo.client.ServerConnectionManager;
import javafx.scene.Node;
import javafx.scene.control.Alert;

/**
 * Utilitaire pour valider la connexion serveur avant chaque action
 */
public class ServerValidator {

    /**
     * Vérifie si le serveur est disponible avant d'exécuter une action
     * Si le serveur n'est pas disponible, redirige vers la page d'erreur
     *
     * @param sourceNode Le noeud source pour la navigation
     * @return true si le serveur est disponible, false sinon
     */
    public static boolean validateServerConnection(Node sourceNode) {
        ServerConnectionManager connectionManager = ServerConnectionManager.getInstance();

        // Vérifier si le serveur est disponible
        if (!connectionManager.isServerAvailable()) {
            System.err.println("[VALIDATION] ❌ Serveur indisponible - Redirection vers page d'erreur");

            // Rediriger vers la page de service indisponible
            NavigationHelper.navigateTo(
                "service-indisponible.fxml",
                "Service Indisponible - Annuaire INF1010",
                sourceNode
            );

            return false;
        }

        System.out.println("[VALIDATION] ✅ Serveur disponible - Action autorisée");
        return true;
    }

    /**
     * Vérifie la connexion et affiche une alerte si le serveur n'est pas disponible
     *
     * @return true si le serveur est disponible, false sinon
     */
    public static boolean checkServerWithAlert() {
        ServerConnectionManager connectionManager = ServerConnectionManager.getInstance();

        if (!connectionManager.isServerAvailable()) {
            showServerUnavailableAlert();
            return false;
        }

        return true;
    }

    /**
     * Affiche une alerte indiquant que le serveur n'est pas disponible
     */
    private static void showServerUnavailableAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Serveur Indisponible");
        alert.setHeaderText("Impossible de contacter le serveur");
        alert.setContentText(
            "Le serveur n'est pas accessible pour le moment.\n\n" +
            "Veuillez vérifier que :\n" +
            "• Le serveur est démarré (ServerLauncher)\n" +
            "• Le fichier port.txt existe\n" +
            "• Aucun pare-feu ne bloque la connexion\n\n" +
            "Vous allez être redirigé vers la page d'erreur."
        );
        alert.showAndWait();
    }

    /**
     * Exécute une action après avoir vérifié la disponibilité du serveur
     *
     * @param sourceNode Le noeud source pour la navigation
     * @param action L'action à exécuter si le serveur est disponible
     * @return true si l'action a été exécutée, false sinon
     */
    public static boolean executeWithServerCheck(Node sourceNode, Runnable action) {
        if (validateServerConnection(sourceNode)) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                System.err.println("[VALIDATION] Erreur lors de l'exécution de l'action: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}

