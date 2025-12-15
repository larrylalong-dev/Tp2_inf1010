@echo off
REM Script de génération d'exécutables pour Windows
REM Annuaire INF1010

echo  Génération des exécutables Windows pour Annuaire INF1010...

REM Vérifier que Java 17+ est installé
echo [ÉTAPE] Vérification de la version Java...
java -version 2>nul
if errorlevel 1 (
    echo [ERREUR] Java n'est pas installé ou n'est pas dans le PATH
    pause
    exit /b 1
)

REM Nettoyer les builds précédents
echo [ÉTAPE] Nettoyage des builds précédents...
if exist target rmdir /s /q target
mkdir target\executables

REM Créer le JAR Fat
echo [ÉTAPE] Création du JAR exécutable...
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo [ERREUR] Échec de la création du JAR
    pause
    exit /b 1
)

copy target\annuaire-inf1010.jar target\executables\
echo [SUCCÈS] JAR créé: target\annuaire-inf1010.jar

REM Créer l'exécutable Windows avec jpackage
echo [ÉTAPE] Création de l'exécutable Windows (.exe)...
jpackage --version >nul 2>&1
if errorlevel 1 (
    echo [ATTENTION] jpackage non disponible, seul le JAR a été créé
    goto :end
)

jpackage ^
    --input target\executables ^
    --name "AnnuaireINF1010" ^
    --main-jar annuaire-inf1010.jar ^
    --main-class com.example.demo.Launcher ^
    --type app-image ^
    --dest target\executables ^
    --vendor "Département INF1010" ^
    --app-version "1.0.0" ^
    --java-options "-Dfile.encoding=UTF-8" ^
    --win-console

if errorlevel 1 (
    echo [ERREUR] Échec de la création de l'exécutable Windows
    goto :end
)

echo [SUCCÈS] Exécutable Windows créé: target\executables\AnnuaireINF1010\

REM Créer l'installeur MSI
echo [ÉTAPE] Création de l'installeur MSI...
jpackage ^
    --input target\executables ^
    --name "AnnuaireINF1010" ^
    --main-jar annuaire-inf1010.jar ^
    --main-class com.example.demo.Launcher ^
    --type msi ^
    --dest target\executables ^
    --vendor "Département INF1010" ^
    --app-version "1.0.0" ^
    --win-menu ^
    --win-menu-group "Département INF1010" ^
    --win-shortcut

if errorlevel 1 (
    echo [ATTENTION] Échec de la création de l'installeur MSI
) else (
    echo [SUCCÈS] Installeur MSI créé: target\executables\AnnuaireINF1010-1.0.0.msi
)

:end
echo.
echo  RÉSUMÉ DES EXÉCUTABLES GÉNÉRÉS:
echo ==================================
dir target\executables

echo.
echo  INSTRUCTIONS D'UTILISATION:
echo ==============================
echo • JAR universel: java -jar target\executables\annuaire-inf1010.jar
if exist target\executables\AnnuaireINF1010\ (
    echo • Exécutable Windows: target\executables\AnnuaireINF1010\AnnuaireINF1010.exe
)
if exist target\executables\AnnuaireINF1010-1.0.0.msi (
    echo • Installeur Windows: Double-cliquer sur AnnuaireINF1010-1.0.0.msi
)

echo.
echo [SUCCÈS] Génération terminée !
pause
