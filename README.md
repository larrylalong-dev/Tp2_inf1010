# TP2 INF1010 – Annuaire Client/Serveur RMI

Ce document explique **uniquement comment lancer l’application** (serveur et client) sur macOS/Linux et Windows.

---

## 1. Prérequis

- **Java 17** installé (vérifier avec `java -version`).
- **Maven** installé (optionnel mais recommandé).
- Une base de données **MySQL** accessible, déjà configurée pour le TP.

Place‑toi toujours dans le dossier racine du projet, par exemple :

```bash
cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
```

---

## 2. Lancer avec les scripts (recommandé)

### 2.1. Sur macOS / Linux

Ouvrir **deux terminaux**.

#### Terminal 1 – Serveur

```bash
cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
./start-server.sh
```

Attendre le message indiquant que le serveur RMI est prêt (port indiqué, fichier `port.txt` mis à jour). **Laisser ce terminal ouvert.**

#### Terminal 2 – Client

```bash
cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
./start-client.sh
```

Une fenêtre JavaFX s’ouvre : c’est le **client** (lancé via la classe `Launcher`).

### 2.2. Sur Windows

Ouvrir **deux fenêtres de commande** (cmd ou PowerShell).

#### Fenêtre 1 – Serveur

```cmd
cd C:\chemin\vers\Tp2_inf1010
start-server.bat
```

Attendre que le serveur affiche qu’il écoute sur un port. Ne pas fermer cette fenêtre.

#### Fenêtre 2 – Client

```cmd
cd C:\chemin\vers\Tp2_inf1010
start-client.bat
```

L’interface graphique du client se lance (classe `Launcher` → `HelloApplication`).

---

## 3. Lancer depuis l’IDE (IntelliJ IDEA)

1. Importer le projet Maven (`pom.xml`).
2. Configurer la JDK en **Java 17**.
3. Dans l’arborescence `src/main/java` :
   - Clic droit sur `com.example.demo.server.ServerLauncher` → **Run 'ServerLauncher.main()'**.
   - Attendre que le serveur soit « prêt ».
   - Clic droit sur `com.example.demo.Launcher` → **Run 'Launcher.main()'**.

L’interface JavaFX du client se lance et se connecte au serveur RMI.

---

## 4. Lancer manuellement avec Maven (optionnel)

Depuis la racine du projet :

### 4.1. Compiler le projet

```bash
mvn clean compile
```

### 4.2. Lancer le serveur

```bash
mvn exec:java -Dexec.mainClass="com.example.demo.server.ServerLauncher"
```

### 4.3. Lancer le client

Dans un autre terminal :

```bash
mvn javafx:run
```

---

## 5. Si ça ne marche pas

- Vérifier que le **serveur** est bien démarré **avant** le client.
- Vérifier que le fichier `port.txt` existe à la racine et contient un numéro de port valide.
- Fermer toutes les anciennes fenêtres Java et relancer les scripts :

```bash
./restart-all.sh
```

- Si une erreur RMI liée à l’IP/hostname apparaît, vérifier la configuration réseau ou voir `GUIDE-DEPANNAGE-RMI.md`.

Ce README est volontairement **simple** : il sert uniquement de guide rapide pour **lancer** l’application (serveur + client). Pour tous les détails techniques, se référer à `rapport-du-projet-2-INF1010.md` et aux autres documents du projet.

