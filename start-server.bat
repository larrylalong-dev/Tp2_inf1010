@echo off
REM Script de lancement du SERVEUR Annuaire INF1010 pour Windows
REM À exécuter dans une fenêtre de commande séparée

echo ═══════════════════════════════════════════════════════
echo    🚀 LANCEMENT DU SERVEUR ANNUAIRE INF1010
echo ═══════════════════════════════════════════════════════
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Maven n'est pas installé ou n'est pas dans le PATH
    echo    Installez Maven depuis: https://maven.apache.org/
    pause
    exit /b 1
)

echo 📦 Compilation du projet...
call mvn -q clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur lors de la compilation
    pause
    exit /b 1
)

echo.
echo 🔧 Démarrage du serveur...
echo    Le serveur recherchera un port disponible à partir de 445
echo    Le port utilisé sera sauvegardé dans port.txt
echo.
echo ⚠️  NE FERMEZ PAS CETTE FENÊTRE tant que vous utilisez l'application cliente
echo.
echo ═══════════════════════════════════════════════════════
echo.

REM Lancer le serveur avec invocation fully-qualified du plugin exec
call mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass="com.example.demo.server.ServerLauncher"

pause
