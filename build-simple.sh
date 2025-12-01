#!/bin/bash

# Script de génération d'exécutables simplifié pour Annuaire INF1010
# Version sans Maven wrapper

echo "🚀 Génération des exécutables pour Annuaire INF1010..."

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}[ÉTAPE]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCÈS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERREUR]${NC} $1"
}

# Vérifier Java
print_step "Vérification de Java..."
if ! command -v java &> /dev/null; then
    print_error "Java non trouvé"
    exit 1
fi
print_success "Java détecté: $(java -version 2>&1 | head -n 1)"

# Nettoyer et créer dossiers
print_step "Préparation des dossiers..."
rm -rf target/
mkdir -p target/classes target/lib target/executables

# Compiler manuellement les sources Java
print_step "Compilation des sources Java..."
# Obtenir le classpath avec les JARs JavaFX et dépendances
JAVAFX_PATH="$HOME/.m2/repository/org/openjfx"
MYSQL_PATH="$HOME/.m2/repository/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"

# Créer le classpath
CLASSPATH=""
if [ -d "$JAVAFX_PATH" ]; then
    for jar in $(find "$JAVAFX_PATH" -name "*.jar" | grep -E "(controls|fxml|base|graphics|swing|media)" | grep "17.0.6" | head -6); do
        CLASSPATH="$CLASSPATH:$jar"
    done
fi

if [ -f "$MYSQL_PATH" ]; then
    CLASSPATH="$CLASSPATH:$MYSQL_PATH"
fi

# Ajouter autres dépendances si disponibles
CONTROLFX_PATH="$HOME/.m2/repository/org/controlsfx/controlsfx/11.2.1/controlsfx-11.2.1.jar"
if [ -f "$CONTROLFX_PATH" ]; then
    CLASSPATH="$CLASSPATH:$CONTROLFX_PATH"
fi

# Compiler
if javac -d target/classes -cp "$CLASSPATH" $(find src/main/java -name "*.java"); then
    print_success "Compilation réussie"
else
    print_error "Échec de la compilation"
    exit 1
fi

# Copier les ressources
print_step "Copie des ressources..."
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* target/classes/
    print_success "Ressources copiées"
fi

# Télécharger les JARs manquants si nécessaire
print_step "Vérification des dépendances..."
mkdir -p target/lib

# Copier les JARs existants
if [ -d "$JAVAFX_PATH" ]; then
    find "$JAVAFX_PATH" -name "*.jar" | grep "17.0.6" | grep -E "(controls|fxml|base|graphics|swing|media)" | head -6 | while read jar; do
        cp "$jar" target/lib/
    done
fi

if [ -f "$MYSQL_PATH" ]; then
    cp "$MYSQL_PATH" target/lib/
fi

if [ -f "$CONTROLFX_PATH" ]; then
    cp "$CONTROLFX_PATH" target/lib/
fi

# Créer le JAR exécutable
print_step "Création du JAR exécutable..."

# Créer le manifest
cat > target/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.example.demo.Launcher
Class-Path: $(ls target/lib/*.jar | xargs -I {} basename {} | tr '\n' ' ' | sed 's/ $//')

EOF

# Créer le JAR avec le manifest et les classes
if jar cfm target/executables/annuaire-inf1010.jar target/MANIFEST.MF -C target/classes . && \
   cp target/lib/*.jar target/executables/; then
    print_success "JAR créé: target/executables/annuaire-inf1010.jar"
else
    print_error "Échec de la création du JAR"
    exit 1
fi

# Créer un script de lancement simple
print_step "Création du script de lancement..."
cat > target/executables/run-annuaire.sh << 'EOF'
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -cp "$DIR/*" com.example.demo.Launcher "$@"
EOF

chmod +x target/executables/run-annuaire.sh

cat > target/executables/run-annuaire.bat << 'EOF'
@echo off
set DIR=%~dp0
java -cp "%DIR%*" com.example.demo.Launcher %*
EOF

print_success "Scripts de lancement créés"

# Tenter jpackage si disponible
if command -v jpackage &> /dev/null; then
    print_step "Création de l'application native Mac..."

    # Créer un JAR fat temporaire pour jpackage
    print_step "Préparation pour jpackage..."
    mkdir -p target/jpackage-input

    # Fusionner tous les JARs en un seul
    cd target/classes
    for jar in ../lib/*.jar; do
        jar xf "$jar"
    done
    cd ../..

    # Créer le JAR fat
    jar cfe target/jpackage-input/annuaire-inf1010-fat.jar com.example.demo.Launcher -C target/classes .

    # Utiliser jpackage
    if jpackage \
        --input target/jpackage-input \
        --name "AnnuaireINF1010" \
        --main-jar annuaire-inf1010-fat.jar \
        --main-class com.example.demo.Launcher \
        --type app-image \
        --dest target/executables \
        --vendor "Département INF1010" \
        --app-version "1.0.0" \
        --java-options "-Dfile.encoding=UTF-8"; then

        print_success "Application Mac créée: target/executables/AnnuaireINF1010.app"

        # Créer DMG
        print_step "Création du DMG..."
        if jpackage \
            --input target/jpackage-input \
            --name "AnnuaireINF1010" \
            --main-jar annuaire-inf1010-fat.jar \
            --main-class com.example.demo.Launcher \
            --type dmg \
            --dest target/executables \
            --vendor "Département INF1010" \
            --app-version "1.0.0"; then
            print_success "DMG créé: target/executables/AnnuaireINF1010-1.0.0.dmg"
        fi
    else
        print_error "Échec jpackage, mais le JAR fonctionne"
    fi
else
    print_step "jpackage non disponible, JAR et scripts créés"
fi

# Résumé
echo ""
echo "📦 EXÉCUTABLES GÉNÉRÉS:"
echo "======================"
ls -la target/executables/

echo ""
echo "🎯 UTILISATION:"
echo "==============="
echo "• JAR simple: java -cp 'target/executables/*' com.example.demo.Launcher"
echo "• Script Mac/Linux: ./target/executables/run-annuaire.sh"
echo "• Script Windows: target/executables/run-annuaire.bat"
if [ -d "target/executables/AnnuaireINF1010.app" ]; then
    echo "• App Mac: Double-cliquer sur AnnuaireINF1010.app"
fi

echo ""
print_success "Génération terminée !"
