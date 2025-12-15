#!/bin/bash

# Script de test du port dynamique
echo "═══════════════════════════════════════════════════════"
echo "   🧪 TEST DU PORT DYNAMIQUE RMI"
echo "═══════════════════════════════════════════════════════"
echo ""

# Vérifier si port.txt existe
if [ -f "port.txt" ]; then
    PORT=$(cat port.txt)
    echo " Fichier port.txt trouvé"
    echo " Port actuel: $PORT"
    echo ""
else
    echo "  Fichier port.txt non trouvé"
    echo "Le serveur n'a probablement pas été démarré"
    echo ""
    exit 1
fi

# Vérifier si le port est dans la bonne plage
if [ "$PORT" -ge 1099 ] && [ "$PORT" -le 1109 ]; then
    echo " Port dans la plage valide (1099-1109)"
else
    echo " Port hors de la plage attendue (1099-1109)"
fi

# Vérifier si le port est utilisé
if lsof -i :$PORT > /dev/null 2>&1; then
    echo " Port $PORT est utilisé (serveur probablement actif)"
    echo ""
    echo "Processus utilisant le port $PORT:"
    lsof -i :$PORT
else
    echo "  Port $PORT n'est pas utilisé"
    echo "Le serveur n'est peut-être pas démarré"
fi

echo ""
echo "═══════════════════════════════════════════════════════"
echo "    Ports RMI disponibles"
echo "═══════════════════════════════════════════════════════"
echo ""

for port in {1099..1109}; do
    if lsof -i :$port > /dev/null 2>&1; then
        echo "Port $port:  OCCUPÉ"
    else
        echo "Port $port:  DISPONIBLE"
    fi
done

echo ""
echo "═══════════════════════════════════════════════════════"

