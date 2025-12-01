@echo off
REM Script de lancement du CLIENT Annuaire INF1010 pour Windows
REM À exécuter APRÈS avoir démarré le serveur

echo ═══════════════════════════════════════════════════════
echo    💻 LANCEMENT DU CLIENT ANNUAIRE INF1010
echo ═══════════════════════════════════════════════════════
echo.

REM Vérifier si le fichier port.txt existe
if not exist "port.txt" (
    echo ⚠️  ATTENTION: Le fichier port.txt n'existe pas
    echo    Assurez-vous que le serveur a été démarré au moins une fois
    echo    Le client utilisera le port par défaut (445)
    echo.
)

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Maven n'est pas installé ou n'est pas dans le PATH
    echo    Installez Maven depuis: https://maven.apache.org/
    pause
    exit /b 1
)

echo 📦 Compilation du projet...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur lors de la compilation
    pause
    exit /b 1
)

echo.
echo 🎨 Démarrage de l'interface graphique...
echo    Si le serveur n'est pas accessible, une page d'erreur s'affichera
echo.
echo ═══════════════════════════════════════════════════════
echo.

REM Lancer le client (application JavaFX)
call mvn javafx:run

pause

