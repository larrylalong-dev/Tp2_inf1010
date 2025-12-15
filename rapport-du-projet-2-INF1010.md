# Rapport du projet 2 INF1010 – Annuaire client‑serveur RMI

## Page de couverture (à adapter pour la version Word/PDF)

- **Cours** : INF1010 – Programmation orientée objet
- **Travail pratique** : TP2 – Application d’annuaire client‑serveur RMI
- **Titre du projet** : Annuaire INF1010 – Client JavaFX & Serveur RMI
- **Équipe** : _[Nom(s) des étudiant(e)s à compléter]_  
- **Matricule(s)** : _[À compléter]_  
- **Groupe** : _[À compléter]_  
- **Enseignant(e)** : _[À compléter]_  
- **Date de remise** : _[À compléter]_  

> **Remarque** : Pour la remise officielle, recopier ces informations sur une vraie page de couverture (Word ou LaTeX) en ajoutant le logo de l’établissement si nécessaire.

---

## Table des matières

1. Introduction et contexte du projet  
2. Vue d’ensemble de l’architecture  
3. Communication et protocoles : RMI, ports et configuration  
4. Fonctionnement normal lorsque le serveur est disponible  
5. Comportement lorsque le serveur est indisponible  
6. Fonctionnalités principales côté client  
7. Captures d’écran et cas d’utilisation  
8. Spécification du système réalisé  
9. Distribution des tâches entre les membres de l’équipe  
10. Lancement du serveur et du client (macOS / Linux / Windows)  
11. Schémas de flux de fonctionnement  
12. Bilan du projet et conclusion  
13. Bibliographie  

---

## 1. Introduction et contexte du projet

### 1.1. Contexte du cours INF1010

Ce projet correspond au TP2 du cours **INF1010** et met en œuvre une application d’**annuaire** répartie en deux parties : un **client graphique JavaFX** et un **serveur RMI**. L’objectif est de passer d’une application monolithique (ou basée sur sockets) à une véritable **architecture client‑serveur**.

### 1.2. Objectifs pédagogiques et techniques

- Séparer clairement **client** et **serveur**.
- Interdire tout **accès direct à la base de données** depuis le client.
- Utiliser **RMI (Remote Method Invocation)** comme protocole de communication.
- Gérer proprement la **disponibilité du serveur** et les **erreurs réseau**.
- Proposer une interface JavaFX **stable** et **responsive**.
- Documenter les corrections effectuées et les comportements attendus.

### 1.3. Périmètre fonctionnel de l’annuaire

L’application permet notamment :
- l’**authentification** d’un utilisateur ;
- la **consultation de la liste des membres** ;
- la **recherche** de membres ;
- l’**affichage des professeurs** par catégorie / domaine ;
- la gestion d’une **liste rouge** (blocage, retrait) ;
- la **visualisation des utilisateurs connectés**.

Toutes ces fonctionnalités passent désormais par le **serveur RMI**.

---

## 2. Vue d’ensemble de l’architecture

### 2.1. Architecture globale client‑serveur RMI

L’architecture suit le schéma général suivant :

```
CLIENT (JavaFX)                      SERVEUR (RMI)                 BASE DE DONNÉES
-----------------                    -----------------             -----------------
HelloApplication / Launcher   ↔   ServerLauncher (RMI)   ↔   MySQL (JDBC, tables personnes,...)
Contrôleurs JavaFX            ↔   RemoteAnnuaireImpl     ↔   Scripts SQL (fix_database.sql, ...)
Services clients RMI          ↔   Services métier (PersonneService, ConnexionService, ...)
```

- Le **client** offre l’interface graphique JavaFX et n’appelle que des **services RMI**.
- Le **serveur** implémente la logique métier et l’accès à la base de données MySQL.
- La **base de données** est accessible uniquement depuis le serveur via **JDBC**.

### 2.2. Composants principaux côté client JavaFX

Principaux éléments :
- `Launcher` : point d’entrée pour démarrer le client JavaFX.
- `HelloApplication` : configuration JavaFX et chargement des premières vues.
- Contrôleurs JavaFX (`LoginController`, `ListeMembresController`, etc.) : logique de présentation.
- Services clients RMI (par exemple `ConnexionServiceClient`, `AnnuaireServiceClient`) : proxies qui invoquent les méthodes distantes du serveur.

Le client **ne parle jamais directement à la base de données** : il passe exclusivement par les services RMI.

### 2.3. Composants principaux côté serveur RMI

Côté serveur, les éléments clés sont :
- `ServerLauncher` : point d’entrée du serveur, configuration de RMI, démarrage du registre et publication des services.
- `RemoteAnnuaire` : **interface RMI** qui définit le contrat (méthodes distantes exposées).
- `RemoteAnnuaireImpl` : **implémentation** de l’interface, qui délègue aux services métier.
- Services métier : `PersonneService`, `ConnexionService`, `PersonneDAO`, etc.

Les anciennes classes de type serveur socket (par exemple `Server.java`, `GestionnaireClient.java`) sont **dépréciées** et ne sont plus utilisées dans l’architecture finale.

### 2.4. Base de données MySQL et couche de persistance

- La base MySQL contient les informations des personnes (identité, rôle, informations diverses) et, selon la configuration, des informations de connexion.
- L’accès à la base se fait uniquement via des classes DAO côté serveur (`PersonneDAO`, etc.).
- Des scripts SQL comme `fix_database.sql` ou `add_connection_column.sql` permettent d’ajuster la structure de la base.

### 2.5. Technologies et frameworks utilisés

- **Java 17** (langage et JVM)
- **JavaFX 17** (UI, FXML, contrôles graphiques)
- **RMI (Remote Method Invocation)** pour la communication réseau
- **MySQL** + **JDBC** pour la persistance
- **Maven** pour la gestion de projet et des dépendances (voir `pom.xml`)
- **Scripts shell (.sh) et batch (.bat)** pour simplifier le lancement du client et du serveur

---

## 3. Communication et protocoles : RMI, ports et configuration

### 3.1. RMI : interface `RemoteAnnuaire` et services clients

La communication entre client et serveur se fait via RMI :
- côté serveur :
  - `RemoteAnnuaire` définit les méthodes accessibles à distance ;
  - `RemoteAnnuaireImpl` implémente ces méthodes et appelle les services métier ;
- côté client :
  - des classes comme `AnnuaireServiceClient` et `ConnexionServiceClient` récupèrent le **stub RMI** via `ServerConnectionManager` et appellent les méthodes distantes.

Les méthodes RMI déclarent typiquement `throws RemoteException` afin que les contrôleurs puissent distinguer les **erreurs réseau** des autres erreurs métier.

### 3.2. Gestion des ports et fichier `port.txt`

Le serveur RMI utilise un **port dynamique** :
- au démarrage, `ServerLauncher` choisit un port libre (par exemple à partir de `445` ou `1099` selon la configuration) ;
- ce port est ensuite **écrit dans le fichier** `port.txt` à la racine du projet ;
- au lancement, le client lit `port.txt` pour savoir sur quel port se connecter.

Ce mécanisme permet d’éviter les collisions de ports et de rendre la configuration plus souple.

### 3.3. Configuration RMI et aspects réseau

Pour éviter les problèmes de résolution d’IP (par exemple une IP de réseau interne non joignable), le serveur est configuré avec :

```bash
-Djava.rmi.server.hostname=localhost
```

Cela force RMI à utiliser **localhost**, ce qui convient pour un TP local.

> Remarque : il n’y a pas encore de chiffrement TLS/SSL ni de mécanisme d’authentification RMI avancé ; ces aspects sont mentionnés comme pistes d’amélioration.

---

## 4. Fonctionnement normal lorsque le serveur est disponible

### 4.1. Démarrage du serveur et initialisation RMI

1. L’utilisateur lance `ServerLauncher` (via script ou IDE).
2. Le serveur démarre le registre RMI et publie l’instance de `RemoteAnnuaireImpl` sous un nom logique (par ex. `AnnuaireService`).
3. Un port libre est choisi et écrit dans `port.txt`.
4. La console serveur affiche un message du type :
   - « Serveur RMI prêt, en écoute sur le port XXX ».

Tant que la fenêtre/console du serveur est ouverte, le serveur est disponible.

### 4.2. Démarrage du client JavaFX et séquence de connexion

1. L’utilisateur lance `Launcher` (client JavaFX) via script ou IDE.
2. `HelloApplication` lit `port.txt` pour récupérer le port du serveur.
3. `ServerConnectionManager` tente d’obtenir le stub RMI auprès du registre RMI.
4. Si la connexion réussit, la **page de connexion** (`login.fxml`, `LoginController`) s’affiche.

Le client **ne peut pas fonctionner** si le serveur n’est pas joignable : il ne fait pas d’accès direct à la base de données.

### 4.3. Cycle utilisateur : login, navigation, opérations

Scénario type :

1. L’utilisateur saisit son identifiant et son mot de passe sur la page de connexion.
2. La requête d’authentification est envoyée au serveur via RMI (service de connexion).
3. En cas de succès, l’utilisateur est redirigé vers le **menu principal**.
4. À partir du menu principal, il peut :
   - lister les membres,
   - consulter la liste rouge,
   - voir les professeurs par catégorie,
   - rechercher un membre,
   - afficher les utilisateurs connectés, etc.

Toutes ces actions déclenchent des **appels RMI** vers `RemoteAnnuaireImpl`.

### 4.4. Surveillance continue du serveur (`ServerMonitorService`)

Une fois connecté :
- un **service de surveillance** (`ServerMonitorService`) est lancé côté client ;
- il vérifie périodiquement (toutes les **3 secondes**) si le serveur répond encore ;
- en cas de perte, il déclenche une **alerte** puis la logique de redirection décrite dans la section suivante.

---

## 5. Comportement lorsque le serveur est indisponible

Cette partie résume la logique détaillée dans `COMPORTEMENT-FINAL-SERVICE-INDISPONIBLE.md` et `MISE-A-JOUR-REDIRECTION.md`.

### 5.1. Cas 1 : serveur indisponible dès le lancement du client

Si le client est lancé alors que le serveur ne tourne pas :
- la tentative initiale de connexion RMI échoue (erreur réseau / `RemoteException`) ;
- le client peut afficher directement une page ou un message indiquant que le **service est indisponible** ;
- l’utilisateur est invité à démarrer le serveur puis à **réessayer**.

### 5.2. Cas 2 : perte de connexion en cours d’utilisation

Scénario complet :

1. Le serveur fonctionne et l’utilisateur est connecté.
2. `ServerMonitorService` vérifie périodiquement la disponibilité du serveur.
3. Si le serveur tombe :
   - une alerte s’affiche (« Connexion au serveur perdue ») ;
   - après validation (OK), le client **redirige vers la page** « Service temporairement indisponible ».

### 5.3. Page "Service temporairement indisponible"

Sur cette page (gérée par `ServiceIndisponibleController` et `service-indisponible.fxml`) :
- une **vérification automatique** du serveur a lieu toutes les **3 secondes** ;
- un **compteur** peut afficher le nombre de tentatives ;
- un bouton **« Réessayer »** permet de relancer manuellement un test de connexion ;
- un bouton **« Quitter »** ferme l’application proprement ;
- des **conseils de dépannage** indiquent quoi vérifier (serveur démarré, base disponible, etc.).

Dès que le serveur redevient joignable, l’application :
- détecte la **reconnexion**,
- affiche éventuellement un message de succès,
- **redirige automatiquement vers la page de connexion**.

### 5.4. Flux détaillé : de la coupure à la reconnexion

Schéma de comportement (inspiré de la documentation existante) :

```
[Utilisateur connecté] --(ServerMonitorService actif)-------------------------
          |
          | Vérification toutes les 3s
          v
   [Serveur KO détecté]
          |
          v
  [Alerte affichée]
  "⚠ Connexion au serveur perdue"
          |
          | Bouton OK
          v
 [Page "Service indisponible"]
          |
          | Boucle:
          |  - Vérification auto (3s)
          |  - Bouton "Réessayer" (test immédiat)
          v
  +----------------------+----------------------+
  | Serveur toujours KO  | Serveur redevenu OK  |
  v                      v                      
 [Rester sur la page]   [Message "Connexion rétablie"]
                         puis redirection vers Login
```

### 5.5. Flux de démarrage client/serveur avec port dynamique


```
Démarrage du serveur (ServerLauncher)
    |
    | 1. Démarre registre RMI
    | 2. Choisit un port libre (445+ ou 1099+)
    | 3. Écrit le port dans port.txt
    v
Serveur RMI prêt
    |
    | (dans un autre terminal)
    v
Démarrage du client (Launcher)
    |
    | 1. Lit port.txt
    | 2. Tente une connexion RMI
    v
+-----------------------------+------------------------------+
| Connexion OK                | Connexion impossible         |
+-----------------------------+------------------------------+
| Afficher Login/Main menu    | Afficher page "Service ind."|
+-----------------------------+------------------------------+
```

---

## 6. Fonctionnalités principales côté client

### 6.1. Authentification et gestion de session

- Saisie d’un identifiant et mot de passe.
- Vérification côté serveur (via RMI) à partir des données de la base.
- En cas de succès, démarrage de `ServerMonitorService` pour surveiller la connexion.
- En cas d’échec (identifiants invalides, utilisateur en liste rouge, etc.), affichage d’un message approprié.

> Remarque : dans l’état actuel, les mots de passe peuvent être stockés en clair dans la base (information importante pour les perspectives de sécurité).

### 6.2. Gestion des membres

- **Liste des membres** : affichage d’un tableau avec les principales informations (nom, prénom, catégorie, etc.).
- **Ajout / modification de membre** : formulaire JavaFX (`ajouter-modifier-membre.fxml`) rendu responsive (corrections de `RowConstraints`, `hgrow`, etc.).
- Toutes les opérations CRUD passent par des appels RMI au serveur.

### 6.3. Recherche et filtrage

- Recherche d’un membre par différents critères.
- Filtrage des **professeurs** par catégorie ou domaine.
- Les résultats sont récupérés via les services RMI (`AnnuaireServiceClient`, etc.) et affichés dans des `TableView` JavaFX.

### 6.4. Liste rouge et gestion des connexions

- Possibilité de placer un utilisateur en **liste rouge** (interdiction de connexion) ou de l’en retirer.
- Affichage des **utilisateurs connectés** dans une vue dédiée (corrections appliquées sur `utilisateurs-connectes.fxml`).
- Ces fonctionnalités s’appuient sur des méthodes RMI supplémentaires et sur la table de connexions côté base.

### 6.5. Interface utilisateur et responsive

Les corrections réalisées incluent :
- suppression des chevauchements de champs dans le formulaire "Nouveau Membre" ;
- ajout de contraintes de croissance (`hgrow="ALWAYS"`, `maxWidth="Infinity"`, etc.) ;
- correction de plusieurs fichiers FXML pour les adapter à JavaFX 17 (versions d’espace de noms, `columnResizePolicy`, ...).

L’interface se redimensionne maintenant correctement lorsque la fenêtre est agrandie ou réduite.

---

## 7. Captures d’écran et cas d’utilisation

> **Note pour la version finale Word/PDF** : insérer ici les captures d’écran avec des légendes claires. Les descriptions ci‑dessous servent de guide pour documenter chaque capture.

### 7.1. Écran de connexion (Login)

- Capture 1 : page de connexion avec champs identifiant / mot de passe.  
- Description : expliquer le scénario nominal (saisie correcte, erreurs affichées, comportement si l’utilisateur est en liste rouge, etc.).

### 7.2. Menu principal

- Capture 2 : menu principal après connexion réussie.  
- Description : présenter les différentes options (liste des membres, liste des professeurs, recherche, liste rouge, utilisateurs connectés, déconnexion).

### 7.3. Liste des membres et détails d’un membre

- Capture 3 : vue de la liste des membres (TableView).  
- Capture 4 : fenêtre de détails / modification d’un membre.  
- Description : expliquer comment filtrer, sélectionner, modifier un membre, et comment ces actions déclenchent des appels RMI.

### 7.4. Gestion de la liste rouge

- Capture 5 : vue montrant l’ajout / retrait d’un membre dans la liste rouge.  
- Description : expliquer l’impact sur la possibilité de se connecter.

### 7.5. Utilisateurs connectés

- Capture 6 : écran listant les utilisateurs actuellement connectés.  
- Description : décrire les colonnes, la mise à jour de la liste, et le lien avec les services de connexion côté serveur.

### 7.6. Page "Service temporairement indisponible"

- Capture 7 : page « Service temporairement indisponible ».  
- Description : expliquer les éléments de l’écran (message, compteur, bouton « Réessayer », bouton « Quitter », messages de diagnostic) et le comportement lorsque le serveur revient.

---

## 8. Spécification du système réalisé

### 8.1. Vue fonctionnelle

- Le système permet de gérer un annuaire de personnes (membres, professeurs, etc.).
- L’utilisateur peut se connecter, parcourir des listes, rechercher des membres, consulter des détails et modifier certaines informations (selon les droits prévus dans le TP).
- La couche de présentation est entièrement en **JavaFX** et la couche métier / données est côté **serveur RMI**.

### 8.2. Vue logique (modules et packages)

- `com.example.demo` : point d’entrée client (`Launcher`, `HelloApplication`).
- `com.example.demo.server` : point d’entrée serveur (`ServerLauncher`) et configuration RMI.
- `com.example.demo.service` : services métier côté serveur et services clients (proxies RMI).
- `com.example.demo.controllers` (ou sous‑packages équivalents) : contrôleurs JavaFX pour chaque vue FXML.
- `Entite` / `CategorieEnum` : entités métiers et énumérations utilisées côté client et côté serveur.

### 8.3. Fichiers sources et contenu (exemples représentatifs)

> La liste ci‑dessous met en avant les fichiers principaux ; dans le rapport Word/PDF, vous pouvez ajouter une table plus exhaustive si désiré.

- `src/main/java/com/example/demo/Launcher.java`  
  - Contenu : point d’entrée du client, lance `HelloApplication`.  
  - Rôle : permet de démarrer le client dans les exécutables / scripts.

- `src/main/java/com/example/demo/HelloApplication.java`  
  - Contenu : classe JavaFX qui initialise la scène principale, charge `login.fxml` ou autre vue de départ.  
  - Rôle : bootstrap graphique du client.

- `src/main/java/com/example/demo/server/ServerLauncher.java`  
  - Contenu : main du serveur RMI, configuration du registre, publication de `RemoteAnnuaireImpl`, écriture de `port.txt`.  
  - Rôle : point d’entrée serveur, à lancer avant le client.

- `src/main/java/com/example/demo/server/RemoteAnnuaire.java`  
  - Contenu : interface RMI (méthodes distantes, exceptions `RemoteException`).  
  - Rôle : contrat de communication entre client et serveur.

- `src/main/java/com/example/demo/server/RemoteAnnuaireImpl.java`  
  - Contenu : implémentation de l’interface `RemoteAnnuaire`, délégation aux services (`PersonneService`, `ConnexionService`, etc.).  
  - Rôle : logique métier côté serveur, accessible à distance.

- `src/main/java/com/example/demo/service/AnnuaireServiceClient.java`  
  - Contenu : proxy RMI côté client pour les opérations d’annuaire.  
  - Rôle : encapsule les appels RMI, propage `RemoteException`.

- `src/main/java/com/example/demo/service/ConnexionServiceClient.java`  
  - Contenu : proxy RMI côté client pour la gestion des connexions / déconnexions.  
  - Rôle : utilisé par `LoginController` et par le monitoring serveur.

- Fichiers FXML principaux (`login.fxml`, `main-menu.fxml`, `liste-membres.fxml`, `ajouter-modifier-membre.fxml`, `service-indisponible.fxml`, etc.)  
  - Contenu : définitions des interfaces graphiques.  
  - Rôle : décrivent la structure de l’UI, liée aux contrôleurs JavaFX.

### 8.4. Génération et utilisation de l’exécutable

- **Génération** :
  - via Maven :
    - `mvn clean package` pour générer un JAR ;
  - via les scripts / guides fournis : utilisation possible de `jpackage` pour produire un `.exe`, `.app`, etc.
- **Utilisation** :
  - lancer d’abord le **serveur** (`ServerLauncher`) via script ou JAR dédié ;
  - lancer ensuite le **client** (`Launcher`) ;
  - suivre les scénarios décrits dans les sections « Lancement » et « Cas d’utilisation ».

---

## 9. Distribution des tâches entre les membres de l’équipe

> À remplir par l’équipe. Exemple de structure de tableau :

| Membre de l’équipe | Matricule | Tâches principales | Commentaires |
|--------------------|-----------|--------------------|-------------|
| Nom 1              | 1234567   | Migration RMI, serveur, DAO |  |
| Nom 2              | 7654321   | Interface JavaFX, FXML, contrôleurs |  |
| Nom 3              | 1122334   | Tests, scripts, documentation |  |

---

## 10. Lancement du serveur et du client (macOS / Linux / Windows)

### 10.1. Prérequis logiciels

- **Java 17** installé et configuré (variable `JAVA_HOME`, `java` dans le PATH).
- **Maven** (pour un lancement manuel ou la compilation).
- **MySQL** accessible (en local ou via un service comme Railway), avec la base de données attendue configurée.

### 10.2. Lancement via scripts sur macOS / Linux

Depuis la racine du projet (`Tp2_inf1010`) :

1. Démarrer le **serveur** (Terminal 1) :

```bash
cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
./start-server.sh
```

2. Attendre le message indiquant que le serveur est prêt (port indiqué, `port.txt` mis à jour).

3. Démarrer le **client** (Terminal 2) :

```bash
cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
./start-client.sh
```

4. L’interface JavaFX s’ouvre (via `Launcher` / `HelloApplication`).

### 10.3. Lancement via scripts sur Windows

Dans deux consoles distinctes :

1. Démarrer le **serveur** :

```cmd
cd C:\chemin\vers\Tp2_inf1010
start-server.bat
```

2. Démarrer le **client** :

```cmd
cd C:\chemin\vers\Tp2_inf1010
start-client.bat
```

Veiller à **lancer le serveur avant le client**.

### 10.4. Lancement manuel avec Maven ou depuis l’IDE

#### Depuis Maven (ligne de commande)

- Serveur :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.demo.server.ServerLauncher"
```

- Client :

```bash
mvn javafx:run
```

#### Depuis l’IDE (IntelliJ IDEA)

- Clic droit sur `ServerLauncher` → **Run 'ServerLauncher.main()'**.
- Puis clic droit sur `Launcher` ou `HelloApplication` → **Run 'Launcher.main()'** / **Run 'HelloApplication.main()'**.

### 10.5. Exécutables et empaquetage

Le projet contient également des scripts et guides pour produire des exécutables (JAR, .exe, .app, etc.) à l’aide de `jpackage` (voir `GUIDE-EXECUTABLES.md`, `GUIDE-SIMPLE-EXECUTABLES.md`).

L’idée générale :
- produire un **JAR exécutable** (main = `com.example.demo.Launcher`) ;
- empaqueter ce JAR dans un exécutable natif adapté à la plateforme.

### 10.6. Dépannage de base

En cas de problème de connexion RMI ou de port :
- vérifier que le serveur est bien lancé et que `port.txt` existe et contient un port valide ;
- vérifier qu’aucun autre processus n’utilise le port ;
- utiliser les scripts de nettoyage comme `restart-all.sh` qui tuent les anciens processus Java et relancent le projet ;
- consulter `GUIDE-DEPANNAGE-RMI.md` pour les erreurs de type IP non locale ou inaccessible.

---

## 11. Schémas de flux de fonctionnement

### 11.1. Schéma 1 : cycle normal de fonctionnement

```
+---------------------------+
|    Démarrage du serveur   |
|   (ServerLauncher.java)   |
+-------------+-------------+
              |
              v
+---------------------------+
|   Serveur RMI en écoute   |
|       Port écrit dans     |
|         port.txt          |
+-------------+-------------+
              |
              | (autre terminal)
              v
+---------------------------+
|   Démarrage du client     |
|      (Launcher.java)      |
+-------------+-------------+
              |
              | Lit port.txt, se connecte
              v
+---------------------------+
|    Page de connexion      |
|    (LoginController)      |
+-------------+-------------+
              |
              | Authentification RMI
              v
+---------------------------+
|      Menu principal       |
|  (ServerMonitor actif)    |
+-------------+-------------+
              |
              | Actions utilisateur
              v
+---------------------------+
| Appels RMI (liste, CRUD, |
|   recherche, liste rouge) |
+---------------------------+
```

### 11.2. Schéma 2 : gestion de l’indisponibilité

```
        [Menu principal]
        (ServerMonitor actif)
                |
                | Vérification périodique
                v
       +-------------------+
       | Serveur disponible|
       +-------------------+
                |
                | OK → Continuer
                |
      (si le serveur tombe)
                v
       +-------------------+
       | Serveur indispo.  |
       +-------------------+
                |
                v
        [Alerte affichée]
         "⚠ Connexion au
           serveur perdue"
                |
                v
   [Page "Service indisponible"]
                |
                | Boucle de vérif. auto (3s)
                | / Bouton "Réessayer"
                v
   +------------------+   +-------------------+
   | Serveur toujours |   | Serveur redevenu  |
   | KO               |   | OK                |
   +------------------+   +-------------------+
            |                      |
            |                      v
       Rester sur la        Message "Connexion
          page              rétablie" puis
                             redirection Login
```

---

## 12. Bilan du projet et conclusion

### 12.1. État actuel et objectifs atteints

- Architecture **client‑serveur RMI** opérationnelle.
- **Plus aucun accès direct** à la base de données depuis le client.
- **Surveillance continue** du serveur et gestion propre de la perte de connexion.
- Interface JavaFX **stabilisée** (corrections FXML, responsive sur les formulaires critiques).
- Documentation riche (analyse, corrections, guides de démarrage et de dépannage).

### 12.2. Problèmes rencontrés

- Problèmes de compatibilité entre les versions de JavaFX utilisées dans les fichiers FXML et le runtime réellement utilisé (erreurs du type `Unable to coerce CONSTRAINED...`).
- Crash de l’application au niveau de `LoginController` (NullPointerException sur `stage` non initialisé).
- Client qui pouvait fonctionner sans serveur (accès direct à la base via des services locaux).
- Difficultés de configuration RMI sur certaines machines (problèmes de hostname / IP non locale).

Pour chacun de ces problèmes, des solutions ont été apportées (corrections FXML, protections null, introduction des services RMI côté client, configuration `java.rmi.server.hostname=localhost`, etc.), comme détaillé dans les fichiers `CORRECTIONS-EFFECTUEES.md`, `TRAVAIL-TERMINE.md` et `RAPPORT-MIGRATION-RMI.md`.

### 12.3. Critiques et propositions d’améliorations au travail réalisé

- **Robustesse** : le système gère déjà la perte de connexion serveur, mais pourrait aller plus loin (timeouts configurables, meilleure gestion des cas extrêmes, logs plus détaillés).
- **Sécurité** : les mots de passe devraient être hachés (par exemple avec BCrypt) et éventuellement transmis via un canal chiffré.
- **UX / UI** : ajouter plus de feedback visuel (indicateurs de chargement, messages d’erreur localisés, aide contextuelle) et harmoniser le style graphique.
- **Qualité du code** : factoriser certains contrôleurs et services, introduire des classes de base communes (`BaseController`, gestion centralisée des erreurs RMI, etc.).

### 12.4. Critiques et propositions d’améliorations au travail demandé

- Clarifier dès le début du TP l’architecture cible (par exemple livrer un diagramme d’architecture de référence) pour mieux guider la migration.
- Fournir un jeu de données de test plus riche pour permettre de tester plus de scénarios (erreurs, cas limites, utilisateurs en liste rouge, etc.).
- Proposer un squelette de tests automatisés (par exemple quelques tests JUnit pour les services côté serveur) afin d’encourager une démarche de développement plus orientée tests.

---

## 13. Bibliographie

> Exemple de style **IEEE** (à adapter selon vos vraies sources) :

[1] Oracle, "Java Platform, Standard Edition Documentation," https://docs.oracle.com/javase/ (consulté en 2025).

[2] Oracle, "JavaFX Documentation," https://openjfx.io/ (consulté en 2025).

[3] Oracle, "Java Remote Method Invocation (RMI)," https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/ (consulté en 2025).

[4] MySQL, "MySQL 8.0 Reference Manual," https://dev.mysql.com/doc/ (consulté en 2025).

[5] Apache Maven Project, "Maven Project," https://maven.apache.org/ (consulté en 2025).

> Vous pouvez également ajouter les références à des tutoriels, livres ou articles que vous avez réellement utilisés, en respectant le même format (IEEE ou APA, selon la consigne choisie).

