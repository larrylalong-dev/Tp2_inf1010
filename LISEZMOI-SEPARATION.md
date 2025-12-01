# 🚀 Séparation Client/Serveur - Annuaire INF1010

## ✅ IMPLÉMENTATION TERMINÉE

J'ai créé une **séparation complète entre le serveur et le client** dans votre projet, avec validation automatique de la connexion serveur avant chaque action.

---

## 📦 Ce qui a été créé

### 🆕 11 NOUVEAUX FICHIERS

#### Code Java (5 fichiers)
1. **`ServerConnectionManager.java`** - Gestionnaire de connexion client/serveur
2. **`ServerLauncher.java`** - Point d'entrée dédié pour le serveur
3. **`ServerValidator.java`** - Validation de connexion avant chaque action
4. **`ServiceIndisponibleController.java`** - Contrôleur de la page d'erreur
5. **`service-indisponible.fxml`** - Interface de la page d'erreur

#### Scripts de lancement (4 fichiers)
6. **`start-server.sh`** - Lancement serveur (macOS/Linux)
7. **`start-server.bat`** - Lancement serveur (Windows)
8. **`start-client.sh`** - Lancement client (macOS/Linux)
9. **`start-client.bat`** - Lancement client (Windows)

#### Documentation (2 fichiers)
10. **`GUIDE-CLIENT-SERVEUR.md`** - Guide complet d'utilisation
11. **`README-SEPARATION.md`** - Résumé technique détaillé

---

## 🔧 Fichiers modifiés

1. **`MainMenuController.java`** - Ajout validation serveur sur toutes les actions
2. **`GestionnaireClient.java`** - Ajout gestion du PING
3. **`module-info.java`** - Ajout du package `client`

---

## 🎯 FONCTIONNALITÉS CLÉS

### ✅ Validation Automatique
Avant **CHAQUE action** dans l'interface :
- ✔️ Vérification si le serveur est disponible (timeout 3s)
- ✔️ Si disponible → Action exécutée normalement
- ✔️ Si indisponible → Redirection automatique vers page d'erreur

### ✅ Page "Service Indisponible"
Affichée automatiquement quand le serveur n'est pas accessible :
- 🔄 **Vérification automatique** toutes les 5 secondes
- 🔄 **Bouton "Réessayer"** pour forcer une reconnexion
- 💡 **Conseils de dépannage** affichés
- 📊 **Informations de connexion** (host, port)
- ✅ **Reconnexion automatique** quand le serveur revient

### ✅ Robustesse
- **3 tentatives** de connexion avec retry
- **Timeout de 3 secondes** pour ne pas bloquer l'interface
- **Gestion des erreurs** à tous les niveaux
- **Support multi-clients** (plusieurs clients simultanés)

---

## 🚀 COMMENT UTILISER

### Étape 1 : Démarrer le SERVEUR (Terminal 1)

```bash
# Sur macOS/Linux
./start-server.sh

# Sur Windows
start-server.bat
```

**Sortie attendue :**
```
═══════════════════════════════════════════════════════
   🚀 DÉMARRAGE DU SERVEUR ANNUAIRE INF1010
═══════════════════════════════════════════════════════
[SERVER] Serveur en écoute sur le port 445
[SERVER] Port sauvegardé dans port.txt : 445
[SERVER] ouvert attend nouvelle connection cliente ...
```

⚠️ **NE FERMEZ PAS cette fenêtre** tant que vous utilisez l'application !

---

### Étape 2 : Démarrer le CLIENT (Terminal 2)

```bash
# Sur macOS/Linux
./start-client.sh

# Sur Windows
start-client.bat
```

**Comportement :**
- ✅ **Serveur accessible** → Application fonctionne normalement
- ❌ **Serveur inaccessible** → Page "Service Indisponible" s'affiche

---

## 🔍 VALIDATION DE CONNEXION

### Actions avec validation automatique

Toutes ces actions vérifient maintenant la disponibilité du serveur :

1. 📋 **Lister les membres**
2. 👨‍🏫 **Lister les professeurs par domaine**
3. 🔍 **Rechercher un membre**
4. ➕ **Ajouter un membre**
5. 🚫 **Gérer la liste rouge**
6. 👥 **Voir les utilisateurs connectés**

### Comment ça marche ?

```
Utilisateur clique sur un bouton
         ↓
  Validation serveur ?
         ↓
    ┌────┴────┐
    │         │
   OUI       NON
    │         │
    ↓         ↓
Exécuter   Rediriger vers
l'action   page d'erreur
```

---

## 🎨 PAGE "SERVICE INDISPONIBLE"

### Fonctionnalités

- ⚠️ **Message clair** : "Service Temporairement Indisponible"
- 📊 **Détails** : Host, Port, Statut
- 🔄 **Vérification automatique** : Toutes les 5 secondes
- 🔄 **Bouton "Réessayer"** : Force une vérification immédiate
- ✅ **Retour automatique** : Quand le serveur redevient disponible
- 💡 **Conseils** : Liste des actions à effectuer
- ❌ **Bouton "Quitter"** : Ferme l'application

### Exemple d'affichage

```
⚠️

Service Temporairement Indisponible

Le serveur n'est pas accessible pour le moment.
Serveur: localhost:445
Vérifiez que le serveur est démarré.

🔄 Vérification automatique en cours...

[🔄 Réessayer Maintenant]  [❌ Quitter l'Application]

💡 Conseils de dépannage:
• Vérifiez que le serveur est démarré (ServerLauncher)
• Vérifiez que le fichier port.txt existe
• Vérifiez qu'aucun pare-feu ne bloque la connexion
```

---

## 🧪 TESTS À EFFECTUER

### Test 1 : Démarrage normal ✅
```bash
Terminal 1: ./start-server.sh
Terminal 2: ./start-client.sh
→ L'application fonctionne normalement
```

### Test 2 : Serveur non démarré ⚠️
```bash
Terminal 1: (Ne rien lancer)
Terminal 2: ./start-client.sh
→ Page "Service Indisponible" s'affiche
```

### Test 3 : Perte de connexion 🔄
```bash
1. Démarrer serveur + client
2. Utiliser l'application
3. Arrêter le serveur (Ctrl+C)
4. Cliquer sur une action dans le client
→ Redirection automatique vers page d'erreur
```

### Test 4 : Reconnexion automatique ✅
```bash
1. Client affiche "Service Indisponible"
2. Redémarrer le serveur
3. Attendre 5 secondes
→ Retour automatique au menu principal
```

### Test 5 : Multi-clients 👥
```bash
Terminal 1: ./start-server.sh
Terminal 2: ./start-client.sh
Terminal 3: ./start-client.sh
→ Les deux clients fonctionnent simultanément
```

---

## 📂 ARCHITECTURE

```
Annuaire INF1010
│
├── SERVEUR (Indépendant)
│   ├── Server.java
│   ├── ServerLauncher.java ← Point d'entrée
│   ├── GestionnaireClient.java
│   └── Port dynamique (445+)
│
├── CLIENT (Indépendant)
│   ├── HelloApplication.java ← Point d'entrée
│   ├── ServerConnectionManager.java ← Gestion connexion
│   ├── ServerValidator.java ← Validation
│   ├── Controllers (avec validation)
│   └── Interface JavaFX
│
└── COMMUNICATION
    ├── Sockets TCP
    ├── PING/PONG
    └── Fichier port.txt
```

---

## 🔒 SÉCURITÉ & ROBUSTESSE

### Timeouts
- **Connexion** : 3 secondes maximum
- **Retry** : 3 tentatives avant abandon

### Validation
- ✅ Vérification avant chaque action
- ✅ Détection automatique de déconnexion
- ✅ Gestion des exceptions réseau

### Feedback utilisateur
- 🎯 Messages clairs et explicites
- 💡 Suggestions de résolution
- 🔄 Actions de récupération disponibles

---

## 📊 MÉTRIQUES

| Élément | Valeur |
|---------|--------|
| Nouveaux fichiers | 11 |
| Fichiers modifiés | 3 |
| Lignes de code ajoutées | ~800 |
| Packages ajoutés | 1 (client) |
| Scripts de lancement | 4 |
| Pages de documentation | 2 |

---

## 🎓 POINTS TECHNIQUES

### 1. Gestionnaire de connexion
```java
ServerConnectionManager.getInstance()
    .isServerAvailable() // Vérifie disponibilité
    .connect()           // Établit connexion
    .disconnect()        // Ferme connexion
    .pingServer()        // Teste connexion active
```

### 2. Validation avant action
```java
if (!ServerValidator.validateServerConnection(node)) {
    return; // Redirige vers page d'erreur automatiquement
}
// Sinon, exécute l'action normalement
```

### 3. Vérification automatique
```java
Timeline checkTimeline = new Timeline(
    new KeyFrame(Duration.seconds(5), event -> {
        checkServerAvailability(); // Vérifie toutes les 5s
    })
);
```

---

## 🆘 DÉPANNAGE

### ❌ "Maven non trouvé"
**Problème** : `zsh: command not found: mvn`
**Solution** : Utilisez votre IDE (IntelliJ, Eclipse) pour compiler et lancer

### ❌ "Port déjà utilisé"
**Problème** : `Port 445 occupé`
**Solution** : Normal ! Le serveur trouve automatiquement un port libre

### ❌ "Fichier port.txt manquant"
**Problème** : Client ne peut pas lire le port
**Solution** : Démarrez le serveur au moins une fois

### ❌ "Connexion refusée"
**Problème** : Client ne peut pas se connecter
**Solution** :
1. Vérifiez que le serveur est démarré
2. Vérifiez le fichier `port.txt`
3. Vérifiez le firewall

---

## 📖 DOCUMENTATION COMPLÈTE

- **`GUIDE-CLIENT-SERVEUR.md`** - Guide détaillé avec diagrammes
- **`README-SEPARATION.md`** - Documentation technique complète
- **Ce fichier** - Quick start en français

---

## ✨ AVANTAGES DE CETTE ARCHITECTURE

✅ **Séparation claire** : Serveur et client totalement indépendants
✅ **Robustesse** : Gestion automatique des erreurs
✅ **UX optimale** : Feedback immédiat à l'utilisateur
✅ **Pas de redémarrage** : Reconnexion automatique
✅ **Multi-clients** : Support natif
✅ **Maintenance facile** : Code modulaire
✅ **Déploiement simple** : Scripts dédiés

---

## 🎉 CONCLUSION

Votre application dispose maintenant d'une **architecture client/serveur complète** avec :

- ✅ **Lancement séparé** du serveur et du client
- ✅ **Validation automatique** de la connexion
- ✅ **Page d'erreur élégante** quand le serveur est indisponible
- ✅ **Reconnexion automatique** quand le serveur revient
- ✅ **Scripts de lancement** pour faciliter l'utilisation

---

## 🚀 PRÊT À TESTER !

```bash
# Terminal 1
./start-server.sh

# Terminal 2
./start-client.sh
```

**Bonne utilisation ! 🎊**

---

*Développé avec ❤️ pour INF1010*
*Version 2.0 - 30 Novembre 2025*

