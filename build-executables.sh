#!/bin/bash

# Script de génération d'exécutables pour Annuaire INF1010
# Supporte Windows (.exe), Mac Intel (.app), et JAR universel

echo "🚀 Génération des exécutables pour Annuaire INF1010..."

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
print_step() {
    echo -e "${BLUE}[ÉTAPE]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCÈS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[ATTENTION]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERREUR]${NC} $1"
}

# Vérifier que Java 17+ est installé
print_step "Vérification de la version Java..."
if ! command -v java &> /dev/null; then
    print_error "Java n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 ou supérieur est requis. Version détectée: $JAVA_VERSION"
    exit 1
fi
print_success "Java $JAVA_VERSION détecté"

# Nettoyer les builds précédents
print_step "Nettoyage des builds précédents..."
rm -rf target/
mkdir -p target/executables

# 1. Créer le JAR Fat (universel)
print_step "Création du JAR exécutable universel..."
if ./mvnw clean package -DskipTests; then
    print_success "JAR créé: target/annuaire-inf1010.jar"
    cp target/annuaire-inf1010.jar target/executables/
else
    print_error "Échec de la création du JAR"
    exit 1
fi

# 2. Créer l'image runtime avec jlink
print_step "Création de l'image runtime Java..."
if ./mvnw javafx:jlink; then
    print_success "Image runtime créée: target/app/"
else
    print_warning "Échec de jlink, utilisation du JAR fat pour jpackage"
fi

# 3. Détecter la plateforme et créer l'exécutable natif
PLATFORM=$(uname)
case $PLATFORM in
    "Darwin")
        print_step "Création de l'exécutable Mac (.app)..."
        if command -v jpackage &> /dev/null; then
            jpackage \
                --input target/executables \
                --name "AnnuaireINF1010" \
                --main-jar annuaire-inf1010.jar \
                --main-class com.example.demo.Launcher \
                --type app-image \
                --dest target/executables \
                --vendor "Département INF1010" \
                --app-version "1.0.0" \
                --java-options "-Dfile.encoding=UTF-8"

            if [ $? -eq 0 ]; then
                print_success "Application Mac créée: target/executables/AnnuaireINF1010.app"

                # Créer aussi un installeur DMG
                print_step "Création de l'installeur DMG..."
                jpackage \
                    --input target/executables \
                    --name "AnnuaireINF1010" \
                    --main-jar annuaire-inf1010.jar \
                    --main-class com.example.demo.Launcher \
                    --type dmg \
                    --dest target/executables \
                    --vendor "Département INF1010" \
                    --app-version "1.0.0"

                if [ $? -eq 0 ]; then
                    print_success "Installeur DMG créé: target/executables/AnnuaireINF1010-1.0.0.dmg"
                fi
            else
                print_error "Échec de la création de l'application Mac"
            fi
        else
            print_warning "jpackage non disponible, seul le JAR a été créé"
        fi
        ;;
    "Linux")
        print_step "Création de l'exécutable Linux..."
        if command -v jpackage &> /dev/null; then
            jpackage \
                --input target/executables \
                --name "AnnuaireINF1010" \
                --main-jar annuaire-inf1010.jar \
                --main-class com.example.demo.Launcher \
                --type app-image \
                --dest target/executables \
                --vendor "Département INF1010" \
                --app-version "1.0.0"

            if [ $? -eq 0 ]; then
                print_success "Application Linux créée: target/executables/AnnuaireINF1010/"
            fi
        else
            print_warning "jpackage non disponible, seul le JAR a été créé"
        fi
        ;;
    *)
        print_warning "Plateforme non reconnue: $PLATFORM"
        print_warning "Seul le JAR universel a été créé"
        ;;
esac

# Afficher les résultats
echo ""
echo "📦 RÉSUMÉ DES EXÉCUTABLES GÉNÉRÉS:"
echo "=================================="
ls -la target/executables/

echo ""
echo "🎯 INSTRUCTIONS D'UTILISATION:"
echo "=============================="
echo "• JAR universel (tous OS): java -jar target/executables/annuaire-inf1010.jar"
if [ -d "target/executables/AnnuaireINF1010.app" ]; then
    echo "• Application Mac: Double-cliquer sur AnnuaireINF1010.app"
fi
if [ -f "target/executables/AnnuaireINF1010-1.0.0.dmg" ]; then
    echo "• Installeur Mac: Monter le DMG et glisser l'app dans Applications"
fi
if [ -d "target/executables/AnnuaireINF1010" ]; then
    echo "• Application Linux: ./target/executables/AnnuaireINF1010/bin/AnnuaireINF1010"
fi

echo ""
print_success "Génération terminée !"
