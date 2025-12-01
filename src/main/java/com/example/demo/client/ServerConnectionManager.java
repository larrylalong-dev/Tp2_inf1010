package com.example.demo.client;

import java.io.*;
import java.net.Socket;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Gestionnaire de connexion au serveur
 * Vérifie la disponibilité et gère la communication
 */
public class ServerConnectionManager {

    private static ServerConnectionManager instance;
    private String serverHost = "localhost";
    private int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isConnected = false;

    private static final int CONNECTION_TIMEOUT = 3000; // 3 secondes
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private ServerConnectionManager() {
        loadServerPort();
    }

    public static ServerConnectionManager getInstance() {
        if (instance == null) {
            instance = new ServerConnectionManager();
        }
        return instance;
    }

    /**
     * Charge le port du serveur depuis le fichier port.txt
     */
    private void loadServerPort() {
        try (BufferedReader reader = new BufferedReader(new FileReader("port.txt"))) {
            serverPort = Integer.parseInt(reader.readLine().trim());
            System.out.println("[CLIENT] Port du serveur chargé: " + serverPort);
        } catch (IOException | NumberFormatException e) {
            serverPort = 445; // Port par défaut
            System.err.println("[CLIENT] Impossible de lire port.txt, utilisation du port par défaut: " + serverPort);
        }
    }

    /**
     * Vérifie si le serveur est disponible
     * @return true si le serveur répond, false sinon
     */
    public boolean isServerAvailable() {
        Socket testSocket = null;
        try {
            testSocket = new Socket();
            testSocket.connect(new java.net.InetSocketAddress(serverHost, serverPort), CONNECTION_TIMEOUT);
            testSocket.close();
            return true;
        } catch (ConnectException e) {
            System.err.println("[CLIENT] Serveur non disponible: connexion refusée");
            return false;
        } catch (SocketTimeoutException e) {
            System.err.println("[CLIENT] Serveur non disponible: timeout");
            return false;
        } catch (IOException e) {
            System.err.println("[CLIENT] Erreur de connexion au serveur: " + e.getMessage());
            return false;
        } finally {
            if (testSocket != null && !testSocket.isClosed()) {
                try {
                    testSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Établit une connexion avec le serveur
     * @return true si la connexion est établie, false sinon
     */
    public boolean connect() {
        int attempts = 0;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                socket = new Socket(serverHost, serverPort);
                socket.setSoTimeout(CONNECTION_TIMEOUT);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                isConnected = true;
                System.out.println("[CLIENT] ✅ Connexion établie avec le serveur");
                return true;

            } catch (IOException e) {
                attempts++;
                System.err.println("[CLIENT] Tentative " + attempts + "/" + MAX_RETRY_ATTEMPTS + " échouée");

                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(1000); // Attendre 1 seconde avant de réessayer
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        isConnected = false;
        System.err.println("[CLIENT] ❌ Impossible de se connecter au serveur après " + MAX_RETRY_ATTEMPTS + " tentatives");
        return false;
    }

    /**
     * Envoie une requête au serveur et attend la réponse
     * @param request La requête à envoyer
     * @return La réponse du serveur, ou null si erreur
     */
    public String sendRequest(String request) {
        if (!isConnected) {
            System.err.println("[CLIENT] Pas de connexion active");
            return null;
        }

        try {
            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("[CLIENT] Erreur lors de l'envoi de la requête: " + e.getMessage());
            isConnected = false;
            return null;
        }
    }

    /**
     * Ferme la connexion avec le serveur
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.println("quit");
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            isConnected = false;
            System.out.println("[CLIENT] Déconnexion du serveur effectuée");
        } catch (IOException e) {
            System.err.println("[CLIENT] Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * Ping le serveur pour vérifier qu'il est toujours actif
     */
    public boolean pingServer() {
        if (!isConnected) {
            return false;
        }

        try {
            // Envoyer un simple message de test
            out.println("PING");
            String response = in.readLine();
            return response != null;
        } catch (IOException e) {
            isConnected = false;
            return false;
        }
    }
}

