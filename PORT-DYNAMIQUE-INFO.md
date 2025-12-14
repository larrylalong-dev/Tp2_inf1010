# 🔧 Port Dynamique RMI - Documentation

## 📋 Résumé des modifications

Le système utilise maintenant un **port dynamique** pour le serveur RMI. Si le port par défaut (1099) est occupé, le serveur essaiera automatiquement les ports suivants jusqu'à trouver un port disponible.

## 🚀 Fonctionnalités

### Serveur (ServerLauncher.java)
- **Plage de ports** : 1099 à 1109 (10 ports possibles)
- **Détection automatique** : Essaie chaque port jusqu'à en trouver un disponible
- **Sauvegarde du port** : Le port utilisé est sauvegardé dans `port.txt`
- **Messages clairs** : Affiche le port utilisé au démarrage

### Client (ServerConnectionManager.java)
- **Lecture automatique** : Lit le port depuis `port.txt` au démarrage
- **Port par défaut** : Utilise 1099 si `port.txt` n'existe pas
- **Connexion transparente** : Se connecte automatiquement au bon port

## 📝 Utilisation

### 1. Démarrer le serveur
```bash
./start-server.sh
```

Le serveur affichera :
```
═══════════════════════════════════════════════════════
   🚀 DÉMARRAGE DU SERVEUR ANNUAIRE INF1010
═══════════════════════════════════════════════════════
[RMI] Configuration hostname: localhost
[RMI] ✅ Registre démarré sur le port 1099
[CONFIG] Port 1099 sauvegardé dans port.txt
[RMI] Service 'AnnuaireService' bindé dans le registre
═══════════════════════════════════════════════════════
   ✅ SERVEUR RMI PRÊT SUR LE PORT 1099
   📝 Port sauvegardé dans port.txt
   ⚠️  Laissez cette fenêtre ouverte
═══════════════════════════════════════════════════════
```

### 2. Si le port 1099 est occupé
Le serveur essaiera automatiquement les ports suivants :
```
[RMI] ⚠️  Port 1099 déjà occupé, essai du port suivant...
[RMI] ✅ Registre démarré sur le port 1100
```

### 3. Démarrer le client
```bash
./start-client.sh
```

Le client lira automatiquement le port depuis `port.txt` :
```
[CLIENT] Port lu depuis port.txt: 1100
```

## 🔍 Fichier port.txt

Le fichier `port.txt` contient simplement le numéro de port :
```
1100
```

Ce fichier est :
- ✅ Créé automatiquement par le serveur
- ✅ Lu automatiquement par le client
- ✅ Mis à jour à chaque démarrage du serveur

## ⚠️ Situations gérées

### Tous les ports sont occupés
Si tous les ports de 1099 à 1109 sont occupés :
```
❌ ERREUR: Impossible de trouver un port disponible entre 1099 et 1109
```
**Solution** : Libérez un des ports ou arrêtez un autre serveur RMI.

### Le fichier port.txt est absent ou invalide
Le client utilisera le port par défaut 1099 :
```
[CLIENT] Impossible de lire port.txt, utilisation du port par défaut 1099
```

### Le serveur n'est pas démarré
Le client affichera une alerte appropriée dans l'interface.

## 🛠️ Personnalisation

Pour modifier la plage de ports, éditez `ServerLauncher.java` :

```java
private static final int PORT_DEBUT = 1099;  // Premier port à essayer
private static final int PORT_MAX = 1109;    // Dernier port à essayer
```

## 📊 Avantages

✅ **Flexibilité** : Plus de conflit si le port est occupé
✅ **Automatique** : Aucune configuration manuelle nécessaire
✅ **Transparent** : Le client trouve automatiquement le serveur
✅ **Robuste** : Gère les erreurs gracieusement
✅ **Informatif** : Messages clairs sur le port utilisé

## 🔄 Workflow complet

1. **Démarrage serveur** → Trouve un port libre → Sauvegarde dans `port.txt`
2. **Démarrage client** → Lit `port.txt` → Se connecte au bon port
3. **Communication** → Tout fonctionne normalement

## 📞 Support

Si vous rencontrez des problèmes :
1. Vérifiez que `port.txt` existe et contient un numéro valide
2. Assurez-vous que le serveur est démarré avant le client
3. Vérifiez qu'au moins un port entre 1099 et 1109 est disponible
4. Consultez les messages dans la console du serveur et du client

