package com.example.demo.database;

import java.sql.*;

public class DatabaseConnection {

    // Configuration de la connexion Railway
    private static final String URL = "jdbc:mysql://gondola.proxy.rlwy.net:53080/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "jWIzIwduQXjkbYzBDRGqOGBxKutFqJeW";

    public DatabaseConnection(){

    }
    /**
     * Obtient une connexion à la base de données Railway
     * @return Connection object
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion avec paramètres supplémentaires
            Connection con = DriverManager.getConnection(
                URL + "?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                USER,
                PASSWORD
            );

            return con;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable", e);
        }
    }


     /** Ferme la connexion */
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    /**  Ferme le PreparedStatement */
    public static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.err.println(" Erreur lors de la fermeture du PreparedStatement : " + e.getMessage());
            }
        }
    }

    /**  Ferme le ResultSet */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println(" Erreur lors de la fermeture du ResultSet : " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Obtenir la connexion
            con = getConnection();

            // Création du statement
            stmt = con.createStatement();

            // Exécution de la requête
            System.out.println("\n Liste des personnes dans la base de données :\n");
            System.out.println("─".repeat(120));
            System.out.printf("%-5s | %-15s | %-15s | %-12s | %-15s | %-20s | %-20s | %-15s | %-12s%n",
                "ID", "Nom", "Prénom", "Matricule", "Téléphone", "Email", "Domaine", "Catégorie", "Liste Rouge");
            System.out.println("─".repeat(120));

            rs = stmt.executeQuery("SELECT * FROM personne");

            // Affichage des résultats
            while (rs.next()) {
                System.out.printf("%-5d | %-15s | %-15s | %-12s | %-15s | %-20s | %-20s | %-15s | %-12s%n",
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("matricule") != null ? rs.getString("matricule") : "N/A",
                    rs.getString("telephone") != null ? rs.getString("telephone") : "N/A",
                    rs.getString("adresse_courriel") != null ? rs.getString("adresse_courriel") : "N/A",
                    rs.getString("domaine_activite") != null ? rs.getString("domaine_activite") : "N/A",
                    rs.getString("categorie"),
                    rs.getBoolean("liste_rouge") ? "OUI" : "NON"
                );
            }

            System.out.println("─".repeat(120));
            System.out.println("\n Données récupérées avec succès !");

        } catch (SQLException e) {
            System.err.println("\n Erreur SQL : " + e.getMessage());
            System.err.println(" Code d'erreur : " + e.getErrorCode());
            System.err.println(" État SQL : " + e.getSQLState());
            e.printStackTrace();

        } finally {
            // Fermeture des ressources dans l'ordre inverse
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) {
                    con.close();
                    System.out.println("\n Connexion fermée.");
                }
            } catch (SQLException e) {
                System.err.println(" Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }

    /**
     * Teste si la connexion fonctionne
     * @return true si la connexion est OK, false sinon
     */
    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            System.err.println("Test de connexion échoué : " + e.getMessage());
            return false;
        }
    }
}
