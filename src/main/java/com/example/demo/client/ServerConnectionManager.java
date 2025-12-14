package com.example.demo.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.example.demo.server.RemoteAnnuaire;

/**
 * Gestionnaire de connexion au serveur via RMI.
 */
public class ServerConnectionManager {

    private static ServerConnectionManager instance;
    private String serverHost = "localhost";
    private int serverPort;
    private RemoteAnnuaire stub;

    private ServerConnectionManager() {
        this.serverPort = lirePortDepuisFichier();
    }

    public static ServerConnectionManager getInstance() {
        if (instance == null) {
            instance = new ServerConnectionManager();
        }
        return instance;
    }

    /** Vérifie si le serveur RMI est disponible */
    public boolean isServerAvailable() {
        try {
            Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
            String[] names = registry.list();
            for (String name : names) {
                if ("AnnuaireService".equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /** Établit la connexion et récupère le stub RMI */
    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
            this.stub = (RemoteAnnuaire) registry.lookup("AnnuaireService");
            return true;
        } catch (Exception e) {
            this.stub = null;
            return false;
        }
    }

    /** Ping le serveur via RMI */
    public boolean pingServer() {
        try {
            return stub != null && stub.ping();
        } catch (Exception e) {
            return false;
        }
    }

    public void disconnect() {
        this.stub = null;
    }

    public boolean isConnected() {
        return this.stub != null;
    }

    public RemoteAnnuaire getStub() {
        return this.stub;
    }

    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }

    /**
     * Lit le port depuis le fichier port.txt
     * Retourne 1099 par défaut si le fichier n'existe pas ou est invalide
     */
    private int lirePortDepuisFichier() {
        try (BufferedReader reader = new BufferedReader(new FileReader("port.txt"))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int port = Integer.parseInt(line.trim());
                System.out.println("[CLIENT] Port lu depuis port.txt: " + port);
                return port;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("[CLIENT] Impossible de lire port.txt, utilisation du port par défaut 1099");
        }
        return 1099; // Port par défaut
    }
}
