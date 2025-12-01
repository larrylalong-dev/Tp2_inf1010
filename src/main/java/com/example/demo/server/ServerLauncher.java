package com.example.demo.server;

import java.io.IOException;

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
            Server.main(args);
        } catch (IOException e) {
            System.err.println("❌ ERREUR FATALE lors du démarrage du serveur:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
