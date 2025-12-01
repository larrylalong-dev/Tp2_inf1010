#!/bin/bash

# Script final de migration RMI pour tous les contrôleurs

echo "🚀 Migration finale RMI - Tous les contrôleurs"
echo ""

cd /Users/larrylalong/IdeaProjects/Tp2_inf1010/src/main/java/com/example/demo

# Fonction pour migrer un contrôleur
migrate() {
    local file="$1"
    echo "📝 Migration de $(basename $file)..."

    # Remplacer tous les appels
    perl -pi -e 's/\bpersonneService\./annuaireService./g' "$file"
    perl -pi -e 's/annuaireService\.categorieToString\(/CategorieUtil.categorieToString(/g' "$file"
    perl -pi -e 's/annuaireService\.stringToCategorie\(/CategorieUtil.stringToCategorie(/g' "$file"

    echo "   ✅ Terminé"
}

# Migrer les 4 contrôleurs restants
migrate "AjouterModifierMembreController.java"
migrate "RechercheMembreController.java"
migrate "ListeRougeController.java"
migrate "ListeProfesseursController.java"

echo ""
echo "✅ Migration terminée pour tous les contrôleurs!"
echo ""
echo "📋 Prochaines étapes:"
echo "   1. Ajouter manuellement les vérifications serveur si nécessaire"
echo "   2. Recompiler le projet"
echo "   3. Tester"

