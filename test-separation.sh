#!/bin/bash

# Script de test rapide pour vérifier la séparation client/serveur

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║           TEST SÉPARATION CLIENT/SERVEUR                       ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

echo "📋 Vérification des fichiers créés..."
echo ""

files=(
    "src/main/java/com/example/demo/client/ServerConnectionManager.java"
    "src/main/java/com/example/demo/server/ServerLauncher.java"
    "src/main/java/com/example/demo/util/ServerValidator.java"
    "src/main/java/com/example/demo/ServiceIndisponibleController.java"
    "src/main/resources/com/example/demo/service-indisponible.fxml"
    "start-server.sh"
    "start-server.bat"
    "start-client.sh"
    "start-client.bat"
    "GUIDE-CLIENT-SERVEUR.md"
    "README-SEPARATION.md"
)

missing=0
found=0

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
        ((found++))
    else
        echo "❌ $file - MANQUANT"
        ((missing++))
    fi
done

echo ""
echo "─────────────────────────────────────────────────────────────────"
echo "📊 Résumé: $found fichiers trouvés, $missing fichiers manquants"
echo "─────────────────────────────────────────────────────────────────"
echo ""

if [ $missing -eq 0 ]; then
    echo "🎉 Tous les fichiers sont présents !"
    echo ""
    echo "📝 Prochaines étapes:"
    echo ""
    echo "1️⃣  Terminal 1 - Démarrer le serveur:"
    echo "   ./start-server.sh"
    echo ""
    echo "2️⃣  Terminal 2 - Démarrer le client:"
    echo "   ./start-client.sh"
    echo ""
    echo "📖 Documentation:"
    echo "   cat GUIDE-CLIENT-SERVEUR.md"
    echo "   cat README-SEPARATION.md"
    echo ""
else
    echo "⚠️  Certains fichiers sont manquants!"
    echo "   Vérifiez que tous les fichiers ont été créés correctement."
fi

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                    FIN DU TEST                                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"

