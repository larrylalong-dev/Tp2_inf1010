#!/bin/bash

# Script de lancement du SERVEUR Annuaire INF1010
# À exécuter dans un terminal séparé

echo "═══════════════════════════════════════════════════════"
echo "    LANCEMENT DU SERVEUR ANNUAIRE INF1010"
echo "═══════════════════════════════════════════════════════"
echo ""

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo " Maven n'est pas installé ou n'est pas dans le PATH"
    echo "   Installez Maven depuis: https://maven.apache.org/"
    exit 1
fi

echo " Compilation du projet..."
mvn -q clean compile || { echo " Erreur lors de la compilation"; exit 1; }

echo ""
echo " Démarrage du serveur..."
echo "   Configuration RMI: localhost:1099"
echo ""
echo "  NE FERMEZ PAS CETTE FENÊTRE tant que vous utilisez l'application cliente"
echo ""
echo "═══════════════════════════════════════════════════════"
echo ""

# Lancer le serveur avec la configuration RMI pour localhost
mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java \
  -Dexec.mainClass="com.example.demo.server.ServerLauncher" \
  -Djava.rmi.server.hostname=localhost
