#!/bin/bash
# Script pour compiler et copier toutes les ressources nécessaires

echo " Compilation et préparation de l'application..."

# Créer les dossiers nécessaires
mkdir -p target/classes/com/example/demo

# Copier les ressources FXML et CSS
echo " Copie des ressources FXML et CSS..."
cp src/main/resources/com/example/demo/*.fxml target/classes/com/example/demo/ 2>/dev/null || true
cp src/main/resources/com/example/demo/*.css target/classes/com/example/demo/ 2>/dev/null || true

echo " Ressources copiées avec succès !"
echo ""
echo " Fichiers disponibles dans target/classes/com/example/demo/ :"
ls -la target/classes/com/example/demo/ | grep -E "\.(fxml|css)$"

echo ""
echo " Votre application est prête à être lancée !"
