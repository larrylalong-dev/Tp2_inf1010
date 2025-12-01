═══════════════════════════════════════════════════════════════════
   📋 GUIDE COMPLET DE DÉMARRAGE - APPLICATION CLIENT-SERVEUR RMI
═══════════════════════════════════════════════════════════════════

L'application a été migrée vers une architecture client-serveur utilisant RMI
(Remote Method Invocation) comme protocole de communication.

═══════════════════════════════════════════════════════════════════
   🚀 DÉMARRAGE RAPIDE
═══════════════════════════════════════════════════════════════════

1️⃣  DÉMARRER LE SERVEUR (Terminal 1)
    
    ```bash
    cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
    ./start-server.sh
    ```
    
    ✅ Attendez de voir: "Serveur RMI prêt. Laissez cette fenêtre ouverte."

2️⃣  DÉMARRER LE CLIENT (Terminal 2 - séparé)
    
    ```bash
    cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
    ./start-client.sh
    ```
    
    ✅ L'interface graphique devrait s'ouvrir

═══════════════════════════════════════════════════════════════════
   🔧 ARCHITECTURE
═══════════════════════════════════════════════════════════════════

SERVEUR:
├─ ServerLauncher.java          → Point d'entrée du serveur
├─ RemoteAnnuaire.java          → Interface RMI (contrat)
├─ RemoteAnnuaireImpl.java      → Implémentation RMI
└─ Services (PersonneService, ConnexionService)

CLIENT:
├─ HelloApplication.java        → Point d'entrée JavaFX
├─ ServerConnectionManager.java → Gestion connexion RMI
├─ AnnuaireServiceClient.java   → Service client (proxy RMI)
├─ ConnexionServiceClient.java  → Service connexion client
└─ Controllers (LoginController, ListeMembresController, etc.)

COMMUNICATION:
    Client → RMI (localhost:1099) → Serveur → Base de données

═══════════════════════════════════════════════════════════════════
   🛡️ GESTION DES ERREURS
═══════════════════════════════════════════════════════════════════

VÉRIFICATION AUTOMATIQUE:
À chaque action client, le système vérifie la disponibilité du serveur.

SI LE SERVEUR EST INDISPONIBLE:
→ Affichage automatique de la page "Service Indisponible"
→ L'utilisateur peut réessayer après avoir démarré le serveur

FICHIERS CONCERNÉS:
- service-indisponible.fxml     → Vue d'erreur
- ServiceIndisponibleController.java → Logique de retry

═══════════════════════════════════════════════════════════════════
   📝 COMMANDES UTILES
═══════════════════════════════════════════════════════════════════

REDÉMARRER COMPLÈTEMENT:
```bash
./restart-all.sh
```

COMPILER MANUELLEMENT:
```bash
mvn clean compile
```

VOIR LES PROCESSUS JAVA:
```bash
jps
```

TUER UN PROCESSUS:
```bash
kill -9 <PID>
```

NETTOYER LES PROCESSUS RMI:
```bash
pkill -f "com.example.demo"
pkill -f "rmiregistry"
```

═══════════════════════════════════════════════════════════════════
   🔍 DÉPANNAGE
═══════════════════════════════════════════════════════════════════

PROBLÈME: "Exception creating connection to: 172.18.33.41"
SOLUTION: Voir GUIDE-DEPANNAGE-RMI.md

PROBLÈME: "Service Indisponible" au login
SOLUTION: 
1. Vérifiez que le serveur est démarré
2. Vérifiez le terminal serveur pour voir les erreurs
3. Redémarrez le serveur si nécessaire

PROBLÈME: Erreur de compilation
SOLUTION:
```bash
mvn clean compile
```

PROBLÈME: Port 1099 déjà utilisé
SOLUTION:
```bash
pkill -f "rmiregistry"
./start-server.sh
```

═══════════════════════════════════════════════════════════════════
   📂 FICHIERS IMPORTANTS
═══════════════════════════════════════════════════════════════════

SCRIPTS:
- start-server.sh               → Démarrer le serveur
- start-client.sh               → Démarrer le client
- restart-all.sh                → Nettoyer et redémarrer

DOCUMENTATION:
- GUIDE-CLIENT-SERVEUR.md       → Architecture détaillée
- GUIDE-DEPANNAGE-RMI.md        → Résolution des problèmes RMI
- README-SEPARATION.md          → Détails de séparation

CONFIGURATION:
- pom.xml                       → Configuration Maven
- module-info.java              → Configuration modules Java

═══════════════════════════════════════════════════════════════════
   ⚙️ CONFIGURATION RMI
═══════════════════════════════════════════════════════════════════

HOSTNAME: localhost
PORT RMI: 1099 (par défaut)
SERVICE NAME: AnnuaireService

MODIFICATION DE LA CONFIGURATION:
Si vous devez changer le hostname ou le port, modifiez:
1. ServerConnectionManager.java (ligne 17-18)
2. ServerLauncher.java (ligne 24, 30)

═══════════════════════════════════════════════════════════════════
   ✅ VÉRIFICATION DU SYSTÈME
═══════════════════════════════════════════════════════════════════

SERVEUR DÉMARRÉ CORRECTEMENT:
□ [RMI] Configuration hostname: localhost
□ [RMI] Registre démarré sur le port 1099
□ [RMI] Service 'AnnuaireService' bindé dans le registre
□ [SERVER] Serveur RMI prêt

CLIENT DÉMARRÉ CORRECTEMENT:
□ Connexion au serveur: localhost:1099
□ Fenêtre de login affichée
□ Pas de message "Service Indisponible"

TEST DE FONCTIONNEMENT:
1. Login avec un compte valide
2. Navigation dans le menu principal
3. Affichage de la liste des membres
4. Actions CRUD (ajout, modification, suppression)

═══════════════════════════════════════════════════════════════════

Pour plus d'informations, consultez:
- GUIDE-CLIENT-SERVEUR.md
- GUIDE-DEPANNAGE-RMI.md

═══════════════════════════════════════════════════════════════════

