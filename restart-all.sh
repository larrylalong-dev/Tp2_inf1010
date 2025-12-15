#!/bin/bash

# Script pour redémarrer complètement le système (nettoie les processus RMI)

echo "═══════════════════════════════════════════════════════"
echo "    REDÉMARRAGE COMPLET DU SYSTÈME"
echo "═══════════════════════════════════════════════════════"
echo ""

echo "🧹 Nettoyage des processus Java en cours..."

# Tuer tous les processus Java liés au projet (sauf celui-ci)
pkill -f "com.example.demo.server.ServerLauncher" 2>/dev/null
pkill -f "com.example.demo.HelloApplication" 2>/dev/null
pkill -f "rmiregistry" 2>/dev/null

# Attendre que les processus se terminent
sleep 2

echo " Processus nettoyés"
echo ""
echo " Instructions:"
echo "   1. Ouvrez un PREMIER terminal et exécutez: ./start-server.sh"
echo "   2. Attendez que le serveur affiche 'Serveur RMI prêt'"
echo "   3. Ouvrez un SECOND terminal et exécutez: ./start-client.sh"
echo ""
echo "═══════════════════════════════════════════════════════"

