package com.example.demo.server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Point d'entrée dédié pour lancer le serveur
 * À exécuter séparément du client
 */
public class ServerLauncher {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   🚀 DÉMARRAGE DU SERVEUR ANNUAIRE INF1010");
        System.out.println("═══════════════════════════════════════════════════════");

        try {
            // Forcer RMI à utiliser localhost
            System.setProperty("java.rmi.server.hostname", "localhost");
            System.out.println("[RMI] Configuration hostname: localhost");

            // Démarrer le registre RMI (port par défaut 1099)
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("[RMI] Registre démarré sur le port 1099");
            } catch (Exception e) {
                System.out.println("[RMI] Registre déjà démarré, tentative d'utilisation existante");
                registry = LocateRegistry.getRegistry(1099);
            }

            // Créer et binder l'implémentation distante
            RemoteAnnuaireImpl service = new RemoteAnnuaireImpl();
            registry.rebind("AnnuaireService", service);
            System.out.println("[RMI] Service 'AnnuaireService' bindé dans le registre");

            System.out.println("[SERVER] Serveur RMI prêt. Laissez cette fenêtre ouverte.");

        } catch (IOException e) {
            System.err.println("❌ ERREUR FATALE lors du démarrage du serveur:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de l'initialisation RMI: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
