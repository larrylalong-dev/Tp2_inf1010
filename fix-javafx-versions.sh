#!/bin/bash

# Script de correction des versions JavaFX dans les fichiers FXML
# Remplace xmlns="http://javafx.com/javafx/11.0.1" par xmlns="http://javafx.com/javafx/17.0.6"

echo "═══════════════════════════════════════════════════════"
echo "   🔧 CORRECTION DES VERSIONS JAVAFX DANS LES FXML"
echo "═══════════════════════════════════════════════════════"
echo ""

# Dossier des ressources
RESOURCES_DIR="src/main/resources/com/example/demo"

if [ ! -d "$RESOURCES_DIR" ]; then
    echo " Erreur: Le dossier $RESOURCES_DIR n'existe pas"
    exit 1
fi

# Compteur de fichiers modifiés
count=0

# Trouver tous les fichiers FXML
echo " Recherche des fichiers FXML à corriger..."
echo ""

for file in "$RESOURCES_DIR"/*.fxml; do
    if [ -f "$file" ]; then
        filename=$(basename "$file")

        # Vérifier si le fichier contient la version 11.0.1 ou 21
        if grep -q 'xmlns="http://javafx.com/javafx/11.0.1"' "$file" || \
           grep -q 'xmlns="http://javafx.com/javafx/21"' "$file" || \
           grep -q 'xmlns="http://javafx.com/javafx/21.0.1"' "$file"; then

            echo " Correction de: $filename"

            # Faire une sauvegarde
            cp "$file" "$file.bak"

            # Remplacer toutes les versions par 17.0.6
            sed -i '' \
                -e 's|xmlns="http://javafx.com/javafx/11.0.1"|xmlns="http://javafx.com/javafx/17.0.6"|g' \
                -e 's|xmlns="http://javafx.com/javafx/21"|xmlns="http://javafx.com/javafx/17.0.6"|g' \
                -e 's|xmlns="http://javafx.com/javafx/21.0.1"|xmlns="http://javafx.com/javafx/17.0.6"|g' \
                "$file"

            # Vérifier si la modification a réussi
            if grep -q 'xmlns="http://javafx.com/javafx/17.0.6"' "$file"; then
                echo "    Corrigé avec succès"
                ((count++))
                rm "$file.bak"  # Supprimer la sauvegarde
            else
                echo "     Échec de la correction, restauration de la sauvegarde"
                mv "$file.bak" "$file"
            fi
        else
            echo "  $filename - Déjà à jour ou pas de version à corriger"
        fi
        echo ""
    fi
done

echo "═══════════════════════════════════════════════════════"
if [ $count -gt 0 ]; then
    echo " $count fichier(s) corrigé(s) avec succès"
else
    echo "  Aucun fichier à corriger"
fi
echo "═══════════════════════════════════════════════════════"
echo ""
echo " Pour appliquer les changements, recompiler le projet:"
echo "   - Dans IntelliJ: Build → Rebuild Project"
echo "   - En ligne de commande: mvn clean compile"
echo ""

