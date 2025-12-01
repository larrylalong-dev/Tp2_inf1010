# Guide de Génération d'Exécutables - Annuaire INF1010

Ce guide explique comment créer des exécutables pour Windows (.exe) et Mac Intel à partir de votre application JavaFX.

## 🎯 Méthodes Disponibles

### 1. **Méthode Automatique (Recommandée)**
Utilisation des scripts que j'ai créés pour vous.

### 2. **Méthode Manuelle**
Utilisation directe des commandes Maven et jpackage.

### 3. **JAR Universel**
Fichier .jar qui fonctionne sur tous les systèmes avec Java installé.

---

## 🚀 Méthode 1 : Scripts Automatiques

### Pour Mac (votre système actuel) :
```bash
# Exécuter le script de build
./build-executables.sh
```

### Pour Windows :
```cmd
# Exécuter sur un système Windows
build-executables.bat
```

**Résultats attendus :**
- `target/executables/annuaire-inf1010.jar` - JAR universel
- `target/executables/AnnuaireINF1010.app` - Application Mac
- `target/executables/AnnuaireINF1010-1.0.0.dmg` - Installeur Mac

---

## 🔧 Méthode 2 : Commandes Manuelles

### Étape 1 : Créer le JAR Fat
```bash
./mvnw clean package -DskipTests
```

### Étape 2 : Créer l'exécutable Mac (.app)
```bash
jpackage \
  --input target \
  --name "AnnuaireINF1010" \
  --main-jar annuaire-inf1010.jar \
  --main-class com.example.demo.Launcher \
  --type app-image \
  --dest target/executables \
  --vendor "Département INF1010" \
  --app-version "1.0.0"
```

### Étape 3 : Créer l'installeur DMG pour Mac
```bash
jpackage \
  --input target \
  --name "AnnuaireINF1010" \
  --main-jar annuaire-inf1010.jar \
  --main-class com.example.demo.Launcher \
  --type dmg \
  --dest target/executables \
  --vendor "Département INF1010" \
  --app-version "1.0.0"
```

### Pour Windows (à exécuter sur un PC Windows) :
```cmd
# Créer l'exécutable .exe
jpackage ^
  --input target ^
  --name "AnnuaireINF1010" ^
  --main-jar annuaire-inf1010.jar ^
  --main-class com.example.demo.Launcher ^
  --type app-image ^
  --dest target\executables ^
  --vendor "Département INF1010" ^
  --app-version "1.0.0" ^
  --win-console

# Créer l'installeur MSI
jpackage ^
  --input target ^
  --name "AnnuaireINF1010" ^
  --main-jar annuaire-inf1010.jar ^
  --main-class com.example.demo.Launcher ^
  --type msi ^
  --dest target\executables ^
  --vendor "Département INF1010" ^
  --app-version "1.0.0" ^
  --win-menu ^
  --win-shortcut
```

---

## 📋 Prérequis

### Pour tous les systèmes :
- Java 17 ou supérieur avec JDK complet
- Maven (ou utiliser ./mvnw inclus)

### Pour créer des exécutables Windows :
- Système Windows OU
- Machine virtuelle Windows OU
- Service de CI/CD avec Windows

### Pour créer des exécutables Mac :
- Système macOS (ce que vous avez)

---

## 🎮 Types d'Exécutables Générés

### 1. **JAR Universel** (`annuaire-inf1010.jar`)
- ✅ Fonctionne sur Windows, Mac, Linux
- ✅ Inclut toutes les dépendances
- ❌ Nécessite Java installé sur le système cible
- **Utilisation :** `java -jar annuaire-inf1010.jar`

### 2. **Application Mac** (`.app`)
- ✅ Application native Mac
- ✅ Inclut le runtime Java
- ✅ Peut être distribuée sans Java préinstallé
- ❌ Fonctionne uniquement sur Mac

### 3. **Installeur Mac** (`.dmg`)
- ✅ Installeur professionnel
- ✅ Glisser-déposer dans Applications
- ✅ Inclut l'icône et les métadonnées

### 4. **Exécutable Windows** (`.exe`)
- ✅ Application native Windows
- ✅ Inclut le runtime Java
- ✅ Peut être distribuée sans Java préinstallé
- ❌ Fonctionne uniquement sur Windows

### 5. **Installeur Windows** (`.msi`)
- ✅ Installeur professionnel Windows
- ✅ Intégration menu Démarrer
- ✅ Raccourcis bureau automatiques

---

## 🚦 Instructions de Test

### Test du JAR :
```bash
java -jar target/executables/annuaire-inf1010.jar
```

### Test de l'app Mac :
```bash
open target/executables/AnnuaireINF1010.app
```

### Test sur Windows :
```cmd
target\executables\AnnuaireINF1010\AnnuaireINF1010.exe
```

---

## 📁 Structure des Fichiers Générés

```
target/executables/
├── annuaire-inf1010.jar              # JAR universel
├── AnnuaireINF1010.app/              # App Mac
├── AnnuaireINF1010-1.0.0.dmg         # Installeur Mac
├── AnnuaireINF1010/                  # App Windows (si créée)
│   └── AnnuaireINF1010.exe
└── AnnuaireINF1010-1.0.0.msi         # Installeur Windows (si créé)
```

---

## 🔧 Dépannage

### Si jpackage n'est pas trouvé :
```bash
# Vérifier que vous avez un JDK complet
java --version
javac --version

# Sur Mac, installer JDK 17+ complet
brew install openjdk@17
```

### Si l'application ne démarre pas :
1. Vérifier que MySQL est accessible
2. Tester d'abord avec le JAR : `java -jar annuaire-inf1010.jar`
3. Vérifier les logs dans la console

### Pour debug :
```bash
# Lancer avec debug activé
java -Djavafx.verbose=true -jar annuaire-inf1010.jar
```

---

## 🎯 Recommandations

1. **Pour distribution simple :** Utilisez le JAR universel
2. **Pour utilisateurs non-techniques :** Créez les installeurs natifs (.dmg, .msi)
3. **Pour tester :** Commencez par le JAR puis les exécutables natifs
4. **Pour distribution professionnelle :** Signez les exécutables (certificat développeur)

---

## 📝 Notes Importantes

- Les exécutables natifs sont plus volumineux (~100MB) car ils incluent Java
- Le JAR est plus petit (~50MB) mais nécessite Java préinstallé
- Testez toujours sur le système cible avant distribution
- Vérifiez que la base de données MySQL est accessible depuis les machines cibles
