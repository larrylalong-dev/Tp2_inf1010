# Guide Simple : Générer des Exécutables avec IntelliJ IDEA

## 🚀 Méthode 1 : JAR Exécutable via IntelliJ (RECOMMANDÉE)

### Étape 1 : Configurer les Artifacts dans IntelliJ

1. **Ouvrir IntelliJ IDEA** avec votre projet
2. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
3. **Artifacts** → **+** → **JAR** → **From modules with dependencies...**
4. **Main Class** : Sélectionner `com.example.demo.Launcher`
5. **Extract to the target JAR** (recommandé pour inclure toutes les dépendances)
6. **OK** → **Apply** → **OK**

### Étape 2 : Générer le JAR

1. **Build** → **Build Artifacts...**
2. **Sélectionner votre artifact** → **Build**
3. Le JAR sera créé dans `out/artifacts/demo_jar/demo.jar`

### Étape 3 : Tester le JAR

```bash
java -jar out/artifacts/demo_jar/demo.jar
```

---

## 🚀 Méthode 2 : Exécutables Natifs avec jpackage

### Prérequis
- JDK 17+ complet (pas juste JRE)
- Votre JAR créé avec la Méthode 1

### Pour Mac (.app et .dmg)

```bash
# 1. Créer l'application Mac
jpackage \
  --input out/artifacts/demo_jar \
  --name "AnnuaireINF1010" \
  --main-jar demo.jar \
  --main-class com.example.demo.Launcher \
  --type app-image \
  --dest executables \
  --vendor "Département INF1010" \
  --app-version "1.0.0" \
  --java-options "-Dfile.encoding=UTF-8"

# 2. Créer l'installeur DMG
jpackage \
  --input out/artifacts/demo_jar \
  --name "AnnuaireINF1010" \
  --main-jar demo.jar \
  --main-class com.example.demo.Launcher \
  --type dmg \
  --dest executables \
  --vendor "Département INF1010" \
  --app-version "1.0.0"
```

### Pour Windows (.exe et .msi)

**Sur un PC Windows :**

```cmd
REM 1. Créer l'exécutable Windows
jpackage ^
  --input out\artifacts\demo_jar ^
  --name "AnnuaireINF1010" ^
  --main-jar demo.jar ^
  --main-class com.example.demo.Launcher ^
  --type app-image ^
  --dest executables ^
  --vendor "Département INF1010" ^
  --app-version "1.0.0" ^
  --win-console

REM 2. Créer l'installeur MSI
jpackage ^
  --input out\artifacts\demo_jar ^
  --name "AnnuaireINF1010" ^
  --main-jar demo.jar ^
  --main-class com.example.demo.Launcher ^
  --type msi ^
  --dest executables ^
  --vendor "Département INF1010" ^
  --app-version "1.0.0" ^
  --win-menu ^
  --win-shortcut
```

---

## 🎯 Méthode 3 : Automatisée avec Script IntelliJ

Créez ce script après avoir généré le JAR avec IntelliJ :

### Script Mac (`build-from-intellij.sh`)
```bash
#!/bin/bash
echo "🚀 Génération d'exécutables depuis IntelliJ..."

# Vérifier que le JAR existe
if [ ! -f "out/artifacts/demo_jar/demo.jar" ]; then
    echo "❌ JAR non trouvé. Générez d'abord le JAR avec IntelliJ :"
    echo "   Build → Build Artifacts → demo:jar → Build"
    exit 1
fi

mkdir -p executables

# Copier le JAR
cp out/artifacts/demo_jar/demo.jar executables/annuaire-inf1010.jar

# Créer app Mac
if command -v jpackage &> /dev/null; then
    echo "📱 Création de l'app Mac..."
    jpackage \
      --input out/artifacts/demo_jar \
      --name "AnnuaireINF1010" \
      --main-jar demo.jar \
      --main-class com.example.demo.Launcher \
      --type app-image \
      --dest executables \
      --vendor "Département INF1010" \
      --app-version "1.0.0"
    
    echo "💿 Création du DMG..."
    jpackage \
      --input out/artifacts/demo_jar \
      --name "AnnuaireINF1010" \
      --main-jar demo.jar \
      --main-class com.example.demo.Launcher \
      --type dmg \
      --dest executables \
      --vendor "Département INF1010" \
      --app-version "1.0.0"
fi

echo "✅ Terminé ! Fichiers dans le dossier 'executables/'"
ls -la executables/
```

---

## 📋 Résultats Attendus

Après avoir suivi ces étapes, vous aurez :

### JAR Universel
- `annuaire-inf1010.jar` - Fonctionne sur tous les OS avec Java
- **Usage :** `java -jar annuaire-inf1010.jar`

### Exécutables Mac
- `AnnuaireINF1010.app` - Application Mac native
- `AnnuaireINF1010-1.0.0.dmg` - Installeur Mac professionnel

### Exécutables Windows (si créés sur PC Windows)
- `AnnuaireINF1010/AnnuaireINF1010.exe` - Application Windows native
- `AnnuaireINF1010-1.0.0.msi` - Installeur Windows professionnel

---

## 🔧 Dépannage

### Si jpackage n'est pas trouvé
```bash
# Vérifier que vous avez un JDK complet
which jpackage
java --list-modules | grep jdk.jpackage

# Sur Mac, installer JDK complet si nécessaire
brew install openjdk@17
```

### Si l'app ne démarre pas
1. Tester d'abord le JAR : `java -jar annuaire-inf1010.jar`
2. Vérifier que MySQL est accessible
3. Regarder les logs console

---

## 🎯 Recommandations

1. **Commencez par la Méthode 1** (JAR via IntelliJ) - c'est le plus simple
2. **Testez le JAR** avant de créer les exécutables natifs
3. **Pour distribution simple :** Utilisez le JAR + instructions Java
4. **Pour utilisateurs non-techniques :** Créez les installeurs natifs (.app, .dmg, .msi)
5. **Pour Windows :** Utilisez une VM Windows ou demandez à quelqu'un avec un PC

L'avantage de cette méthode est qu'IntelliJ gère automatiquement toutes les dépendances et la configuration des modules JavaFX.
