#!/bin/bash

# Script de redémarrage après correction Serializable

echo "═══════════════════════════════════════════════════════"
echo "    REDÉMARRAGE APRÈS CORRECTION SERIALIZABLE"
echo "═══════════════════════════════════════════════════════"
echo ""

echo " Nettoyage des processus en cours..."
pkill -f "com.example.demo.server.ServerLauncher" 2>/dev/null
pkill -f "com.example.demo.HelloApplication" 2>/dev/null
sleep 2
echo " Processus nettoyés"
echo ""

echo " Recompilation du projet..."
echo "   (Utilisez IntelliJ IDEA: Build → Rebuild Project)"
echo ""

echo "═══════════════════════════════════════════════════════"
echo "    INSTRUCTIONS DE REDÉMARRAGE"
echo "═══════════════════════════════════════════════════════"
echo ""
echo "  Dans IntelliJ IDEA:"
echo "   Menu → Build → Rebuild Project"
echo ""
echo "  Démarrer le SERVEUR:"
echo "   Run 'ServerLauncher.main()'"
echo "   Attendez: [SERVER] Serveur RMI prêt"
echo ""
echo "  Démarrer le CLIENT:"
echo "   Run 'HelloApplication.main()'"
echo "    Interface graphique s'ouvre"
echo ""
echo "═══════════════════════════════════════════════════════"
echo ""
echo " La correction Serializable est appliquée!"
echo "   Plus d'erreur NotSerializableException après redémarrage."
echo ""
echo "═══════════════════════════════════════════════════════"

