═══════════════════════════════════════════════════════════════════
   🚨 GUIDE DE DÉPANNAGE - PROBLÈME DE CONNEXION RMI
═══════════════════════════════════════════════════════════════════

PROBLÈME:
Vous voyez l'erreur "Exception creating connection to: 172.18.33.41"
ou "Host is down"

CAUSE:
RMI essaie d'utiliser l'adresse IP de votre machine au lieu de localhost.

SOLUTION:

1️⃣  ARRÊTEZ TOUS LES PROCESSUS EN COURS
   
   Exécutez le script de nettoyage:
   ```
   ./restart-all.sh
   ```

   OU manuellement:
   ```
   pkill -f "com.example.demo"
   pkill -f "rmiregistry"
   ```

2️⃣  DÉMARREZ LE SERVEUR

   Dans un PREMIER terminal:
   ```
   cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
   ./start-server.sh
   ```

   Attendez de voir: "Serveur RMI prêt. Laissez cette fenêtre ouverte."

3️⃣  DÉMARREZ LE CLIENT

   Dans un SECOND terminal (séparé):
   ```
   cd /Users/larrylalong/IdeaProjects/Tp2_inf1010
   ./start-client.sh
   ```

═══════════════════════════════════════════════════════════════════

VÉRIFICATIONS:

✅ Le serveur doit afficher:
   [RMI] Configuration hostname: localhost
   [RMI] Registre démarré sur le port 1099
   [RMI] Service 'AnnuaireService' bindé dans le registre
   [SERVER] Serveur RMI prêt

✅ Le client doit afficher:
   Connexion au serveur: localhost:1099

❌ Si vous voyez toujours l'erreur 172.18.33.41:
   - Assurez-vous d'avoir bien arrêté TOUS les processus Java
   - Redémarrez votre terminal
   - Réessayez les étapes 1, 2 et 3

═══════════════════════════════════════════════════════════════════

ALTERNATIVE - SI LE PROBLÈME PERSISTE:

Ajoutez cette ligne au début de votre fichier .zshrc ou .bashrc:
```
export JAVA_OPTS="-Djava.rmi.server.hostname=localhost"
```

Puis rechargez:
```
source ~/.zshrc
```

═══════════════════════════════════════════════════════════════════

