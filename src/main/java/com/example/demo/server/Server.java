package com.example.demo.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.demo.dao.PersonneDAO;

public class Server {

    private static final int DEFAULT_PORT = 445;
    private static int PORT = DEFAULT_PORT;

    private static ArrayList<GestionnaireClient> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newCachedThreadPool();
    /// 1-le système supporte plusieurs clients qui accèdent en même temps
    /// au serveur.
    // PersonneDAO utilisateurDAO = new PersonneDAO();
    ServerSocket listener;
    static PrintWriter envoyeur;

    // Point d'entrée du serveur : accepte les connexions et démarre un thread par
    // client

    public static void main(String[] args) throws IOException {
        ServerSocket listener = null;
        int currentPort = PORT;
        int compteClient = 0;

        while (true) {
            try {
                listener = new ServerSocket(currentPort);
                System.out.println("[SERVER]  Serveur en écoute sur le port " + currentPort);
                break; // Port trouvé !
            } catch (BindException e) {
                System.err.println("[SERVER]  Port " + currentPort + " occupé, essai du suivant...");
                currentPort++;
            }
        }

        try (PrintWriter writer = new PrintWriter("port.txt")) {
            writer.println(currentPort);
            System.out.println("[SERVER] Port sauvegardé dans port.txt : " + currentPort);
        } catch (IOException e) {
            System.err.println("[SERVER] Impossible d'écrire le fichier port.txt : " + e.getMessage());
        }

        while (true) {

            System.out.println("[SERVER] ouvert attend nouvelle connection cliente ...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Connecté a " + ++compteClient + "client!");
            // envoyeur.println("END_MENU");
            GestionnaireClient clientThread = new GestionnaireClient(client); // 2- Creation de thread pour chak
                                                                              // client
            clients.add(clientThread);
            pool.execute(clientThread);// Lance le thread pour ce client

        }

    }

}
