#!/bin/bash

# Script de génération d'exécutables SANS modules Java
# Version simplifiée pour Annuaire INF1010

echo "🚀 Génération des exécutables pour Annuaire INF1010 (sans modules)..."

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_step() { echo -e "${BLUE}[ÉTAPE]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCÈS]${NC} $1"; }
print_error() { echo -e "${RED}[ERREUR]${NC} $1"; }

# Vérifier Java
print_step "Vérification de Java..."
if ! command -v java &> /dev/null; then
    print_error "Java non trouvé"
    exit 1
fi
print_success "Java détecté"

# Préparation
print_step "Préparation des dossiers..."
rm -rf target/
mkdir -p target/classes target/lib target/executables

# Créer le classpath avec les dépendances Maven locales
print_step "Construction du classpath..."
CLASSPATH=""

# JavaFX (si installé via Maven local)
JAVAFX_BASE="$HOME/.m2/repository/org/openjfx"
if [ -d "$JAVAFX_BASE" ]; then
    for module in javafx-controls javafx-fxml javafx-base javafx-graphics javafx-swing javafx-media; do
        JAR_PATH="$JAVAFX_BASE/$module/17.0.6/$module-17.0.6.jar"
        if [ -f "$JAR_PATH" ]; then
            CLASSPATH="$CLASSPATH:$JAR_PATH"
            cp "$JAR_PATH" target/lib/
        fi
    done
fi

# MySQL Connector
MYSQL_JAR="$HOME/.m2/repository/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
if [ -f "$MYSQL_JAR" ]; then
    CLASSPATH="$CLASSPATH:$MYSQL_JAR"
    cp "$MYSQL_JAR" target/lib/
fi

# ControlsFX
CONTROLSFX_JAR="$HOME/.m2/repository/org/controlsfx/controlsfx/11.2.1/controlsfx-11.2.1.jar"
if [ -f "$CONTROLSFX_JAR" ]; then
    CLASSPATH="$CLASSPATH:$CONTROLSFX_JAR"
    cp "$CONTROLSFX_JAR" target/lib/
fi

if [ -z "$CLASSPATH" ]; then
    print_error "Aucune dépendance trouvée. Exécutez d'abord Maven pour télécharger les dépendances."
    print_step "Tentative de téléchargement des dépendances..."

    # Essayer avec Maven système s'il est disponible
    if command -v mvn &> /dev/null; then
        mvn dependency:copy-dependencies -DoutputDirectory=target/lib
        if [ $? -eq 0 ]; then
            print_success "Dépendances téléchargées avec Maven"
            CLASSPATH="target/lib/*"
        fi
    else
        print_error "Maven non trouvé. Installez Maven ou utilisez IntelliJ pour build le projet."
        exit 1
    fi
fi

# Compilation SANS module-info.java
print_step "Compilation des sources Java (sans modules)..."

# Exclure module-info.java de la compilation
find src/main/java -name "*.java" ! -name "module-info.java" > sources.txt

if javac -d target/classes -cp "$CLASSPATH" @sources.txt; then
    print_success "Compilation réussie"
    rm sources.txt
else
    print_error "Échec de la compilation"
    rm sources.txt
    exit 1
fi

# Copier les ressources
print_step "Copie des ressources..."
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* target/classes/ 2>/dev/null || true
fi
print_success "Ressources copiées"

# Créer le JAR exécutable
print_step "Création du JAR exécutable..."

# Créer le manifest
cat > target/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.example.demo.Launcher
Class-Path: $(ls target/lib/*.jar 2>/dev/null | xargs -I {} basename {} | tr '\n' ' ' | sed 's/[ ]*$//')

EOF

# Créer le JAR principal
jar cfm target/executables/annuaire-inf1010.jar target/MANIFEST.MF -C target/classes .

# Copier toutes les dépendances
cp target/lib/*.jar target/executables/ 2>/dev/null || true

print_success "JAR créé: target/executables/annuaire-inf1010.jar"

# Créer des scripts de lancement
print_step "Création des scripts de lancement..."

# Script Mac/Linux
cat > target/executables/run-annuaire.sh << 'EOF'
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Lancer avec JavaFX
java --module-path "$DIR" \
     --add-modules javafx.controls,javafx.fxml \
     -cp "$DIR/*" \
     -Dfile.encoding=UTF-8 \
     com.example.demo.Launcher "$@"
EOF

chmod +x target/executables/run-annuaire.sh

# Script Windows
cat > target/executables/run-annuaire.bat << 'EOF'
@echo off
set DIR=%~dp0

java --module-path "%DIR%" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "%DIR%*" ^
     -Dfile.encoding=UTF-8 ^
     com.example.demo.Launcher %*
EOF

# Script de lancement simple (sans modules)
cat > target/executables/run-simple.sh << 'EOF'
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -cp "$DIR/*" -Dfile.encoding=UTF-8 com.example.demo.Launcher "$@"
EOF

chmod +x target/executables/run-simple.sh

cat > target/executables/run-simple.bat << 'EOF'
@echo off
set DIR=%~dp0
java -cp "%DIR%*" -Dfile.encoding=UTF-8 com.example.demo.Launcher %*
EOF

print_success "Scripts de lancement créés"

# Tenter jpackage si disponible
if command -v jpackage &> /dev/null; then
    print_step "Tentative de création d'une application native..."

    # Créer un JAR fat pour jpackage
    mkdir -p target/fat-jar
    cd target/classes

    # Extraire toutes les dépendances dans le JAR fat
    for jar in ../lib/*.jar; do
        if [ -f "$jar" ]; then
            jar xf "$jar" 2>/dev/null || true
        fi
    done

    # Nettoyer les fichiers de signature qui causent des conflits
    rm -rf META-INF/*.SF META-INF/*.DSA META-INF/*.RSA 2>/dev/null || true

    cd ../..

    # Créer le JAR fat
    jar cfe target/fat-jar/annuaire-fat.jar com.example.demo.Launcher -C target/classes .

    # Utiliser jpackage
    if jpackage \
        --input target/fat-jar \
        --name "AnnuaireINF1010" \
        --main-jar annuaire-fat.jar \
        --type app-image \
        --dest target/executables \
        --vendor "Département INF1010" \
        --app-version "1.0.0" \
        --java-options "-Dfile.encoding=UTF-8" 2>/dev/null; then

        print_success "Application Mac créée: target/executables/AnnuaireINF1010.app"

        # Tenter de créer un DMG
        if jpackage \
            --input target/fat-jar \
            --name "AnnuaireINF1010" \
            --main-jar annuaire-fat.jar \
            --type dmg \
            --dest target/executables \
            --vendor "Département INF1010" \
            --app-version "1.0.0" 2>/dev/null; then
            print_success "DMG créé: target/executables/AnnuaireINF1010-1.0.0.dmg"
        fi
    else
        print_step "jpackage a échoué, mais les JARs et scripts fonctionnent"
    fi
fi

# Afficher les résultats
echo ""
echo " EXÉCUTABLES GÉNÉRÉS:"
echo "======================"
ls -la target/executables/

echo ""
echo " COMMENT UTILISER:"
echo "===================="
echo ""
echo "1. JAR avec scripts (RECOMMANDÉ):"
echo "   Mac/Linux: ./target/executables/run-annuaire.sh"
echo "   Windows:   target\\executables\\run-annuaire.bat"
echo ""
echo "2. JAR simple (si JavaFX pose problème):"
echo "   Mac/Linux: ./target/executables/run-simple.sh"
echo "   Windows:   target\\executables\\run-simple.bat"
echo ""
echo "3. JAR manuel:"
echo "   java -cp 'target/executables/*' com.example.demo.Launcher"
echo ""

if [ -d "target/executables/AnnuaireINF1010.app" ]; then
    echo "4. Application Mac native:"
    echo "   Double-cliquer sur AnnuaireINF1010.app"
    echo ""
fi

echo " NOTES IMPORTANTES:"
echo "====================="
echo "• Les scripts incluent toutes les dépendances nécessaires"
echo "• Testez d'abord run-simple.sh/bat si run-annuaire ne fonctionne pas"
echo "• Assurez-vous que MySQL est accessible depuis la machine cible"
echo "• Pour Windows: copiez tout le dossier target/executables/"

echo ""
print_success "Génération terminée avec succès !"
