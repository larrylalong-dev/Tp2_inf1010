#!/bin/bash

# Script de lancement du CLIENT Annuaire INF1010
# À exécuter APRÈS avoir démarré le serveur

echo "═══════════════════════════════════════════════════════"
echo "    LANCEMENT DU CLIENT ANNUAIRE INF1010"
echo "═══════════════════════════════════════════════════════"
echo ""

# Vérifier si le fichier port.txt existe
if [ ! -f "port.txt" ]; then
    echo "  ATTENTION: Le fichier port.txt n'existe pas"
    echo "   Assurez-vous que le serveur a été démarré au moins une fois"
    echo "   Le client utilisera le port par défaut (445)"
    echo ""
fi

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo " Maven n'est pas installé ou n'est pas dans le PATH"
    echo "   Installez Maven depuis: https://maven.apache.org/"
    exit 1
fi

echo " Compilation du projet..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo " Erreur lors de la compilation"
    exit 1
fi

echo ""
echo " Démarrage de l'interface graphique..."
echo "   Connexion au serveur: localhost:1099"
echo "   Si le serveur n'est pas accessible, une page d'erreur s'affichera"
echo ""
echo "═══════════════════════════════════════════════════════"
echo ""

# Lancer le client (application JavaFX) avec configuration RMI
mvn javafx:run -Djava.rmi.server.hostname=localhost

