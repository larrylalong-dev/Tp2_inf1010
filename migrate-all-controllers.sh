#!/bin/bash

# Script de migration automatique de tous les contrôleurs vers RMI

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║   🚀 MIGRATION AUTOMATIQUE DES CONTRÔLEURS VERS RMI          ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

PROJECT_DIR="/Users/larrylalong/IdeaProjects/Tp2_inf1010"
CONTROLLERS_DIR="$PROJECT_DIR/src/main/java/com/example/demo"

# Liste des contrôleurs à migrer
CONTROLLERS=(
    "ListeMembresController.java"
    "AjouterModifierMembreController.java"
    "RechercheMembreController.java"
    "ListeRougeController.java"
    "ListeProfesseursController.java"
)

migrate_controller() {
    local file="$1"
    local filename=$(basename "$file")

    echo "🔄 Migration de $filename..."

    # Créer un backup
    cp "$file" "$file.backup"

    # 1. Remplacer les imports
    sed -i '' 's/import com\.example\.demo\.service\.PersonneService;/import com.example.demo.service.AnnuaireServiceClient;/g' "$file"
    sed -i '' 's/import com\.example\.demo\.service\.ConnexionService;/import com.example.demo.service.ConnexionServiceClient;/g' "$file"

    # 2. Ajouter l'import CategorieUtil après SessionManager
    sed -i '' '/import com\.example\.demo\.util\.SessionManager;/a\
import com.example.demo.util.CategorieUtil;
' "$file"

    # 3. Remplacer les déclarations de services
    sed -i '' 's/private PersonneService personneService;/private AnnuaireServiceClient annuaireService;/g' "$file"
    sed -i '' 's/private ConnexionService connexionService;/private ConnexionServiceClient connexionService;/g' "$file"

    # 4. Remplacer les initialisations
    sed -i '' 's/personneService = new PersonneService();/annuaireService = new AnnuaireServiceClient();/g' "$file"
    sed -i '' 's/connexionService = new ConnexionService();/connexionService = new ConnexionServiceClient();/g' "$file"

    # 5. Remplacer tous les appels personneService
    sed -i '' 's/personneService\./annuaireService./g' "$file"

    # 6. Remplacer les méthodes utilitaires
    sed -i '' 's/annuaireService\.categorieToString(/CategorieUtil.categorieToString(/g' "$file"
    sed -i '' 's/annuaireService\.stringToCategorie(/CategorieUtil.stringToCategorie(/g' "$file"

    echo "   ✅ Migration terminée ($filename)"
}

# Migrer chaque contrôleur
for controller in "${CONTROLLERS[@]}"; do
    filepath="$CONTROLLERS_DIR/$controller"
    if [ -f "$filepath" ]; then
        migrate_controller "$filepath"
    else
        echo "⚠️  Fichier non trouvé: $controller"
    fi
    echo ""
done

echo "═══════════════════════════════════════════════════════════════"
echo "✅ Migration automatique terminée!"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "📝 Fichiers de backup créés (.backup)"
echo ""
echo "⚠️  ATTENTION: Vous devez maintenant:"
echo "   1. Ajouter la vérification serveur dans initialize() de chaque contrôleur"
echo "   2. Ajouter navigateToServiceIndisponible() à la fin de chaque contrôleur"
echo "   3. Recompiler le projet"
echo ""

