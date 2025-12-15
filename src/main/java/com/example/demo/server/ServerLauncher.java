package com.example.demo.server;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Point d'entrée dédié pour lancer le serveur
 * À exécuter séparément du client
 */
public class ServerLauncher {

    private static final int PORT_DEBUT = 1099;
    private static final int PORT_MAX = 1109;

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("    DÉMARRAGE DU SERVEUR ANNUAIRE INF1010");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Forcer RMI à utiliser localhost
            System.setProperty("java.rmi.server.hostname", "localhost");
            System.out.println("[RMI] Configuration hostname: localhost");

            // Démarrer le registre RMI avec port dynamique
            Registry registry = null;
            int portUtilise = -1;

            // Essayer plusieurs ports si le premier est occupé
            for (int port = PORT_DEBUT; port <= PORT_MAX; port++) {
                try {
                    registry = LocateRegistry.createRegistry(port);
                    portUtilise = port;
                    System.out.println("[RMI]  Registre démarré sur le port " + port);
                    break;
                } catch (Exception e) {
                    System.out.println("[RMI] ⚠  Port " + port + " déjà occupé, essai du port suivant...");
                }
            }

            // Si aucun port n'est disponible
            if (registry == null) {
                System.err.println(" ERREUR: Impossible de trouver un port disponible entre " +
                                   PORT_DEBUT + " et " + PORT_MAX);
                System.exit(1);
            }

            // Sauvegarder le port utilisé dans un fichier
            sauvegarderPort(portUtilise);

            // Créer et binder l'implémentation distante
            RemoteAnnuaireImpl service = new RemoteAnnuaireImpl();
            registry.rebind("AnnuaireService", service);
            System.out.println("[RMI] Service 'AnnuaireService' bindé dans le registre");

            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("    SERVEUR RMI PRÊT SUR LE PORT " + portUtilise);
            System.out.println("    Port sauvegardé dans port.txt");
            System.out.println("   Laissez cette fenêtre ouverte");
            System.out.println("═══════════════════════════════════════════════════════");

        } catch (IOException e) {
            System.err.println(" ERREUR FATALE lors du démarrage du serveur:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println(" ERREUR lors de l'initialisation RMI: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Sauvegarde le port utilisé dans le fichier port.txt
     */
    private static void sauvegarderPort(int port) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("port.txt"))) {
            writer.println(port);
            System.out.println("[CONFIG] Port " + port + " sauvegardé dans port.txt");
        } catch (IOException e) {
            System.err.println("  Impossible de sauvegarder le port dans port.txt: " + e.getMessage());
        }
    }
}
