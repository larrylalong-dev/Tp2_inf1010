#!/usr/bin/env python3
"""
Script pour migrer automatiquement tous les contrôleurs vers RMI
"""

import os
import re

# Répertoire des contrôleurs
CONTROLLERS_DIR = "/Users/larrylalong/IdeaProjects/Tp2_inf1010/src/main/java/com/example/demo"

# Liste des contrôleurs à migrer
CONTROLLERS = [
    "ListeMembresController.java",
    "AjouterModifierMembreController.java",
    "RechercheMembreController.java",
    "ListeRougeController.java",
    "ListeProfesseursController.java"
]

def migrate_controller(filepath):
    """Migre un contrôleur vers RMI"""
    print(f"\n Migration de {os.path.basename(filepath)}...")

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 1. Remplacer les imports
    content = content.replace(
        'import com.example.demo.service.PersonneService;',
        'import com.example.demo.service.AnnuaireServiceClient;'
    )
    content = content.replace(
        'import com.example.demo.service.ConnexionService;',
        'import com.example.demo.service.ConnexionServiceClient;'
    )

    # 2. Ajouter l'import CategorieUtil si nécessaire
    if 'CategorieUtil' not in content and 'categorieToString' in content:
        # Trouver la ligne avec les autres imports util
        if 'import com.example.demo.util' in content:
            content = content.replace(
                'import com.example.demo.util.SessionManager;',
                'import com.example.demo.util.SessionManager;\nimport com.example.demo.util.CategorieUtil;'
            )

    # 3. Remplacer les déclarations de services
    content = re.sub(
        r'private\s+PersonneService\s+personneService;',
        'private AnnuaireServiceClient annuaireService;',
        content
    )
    content = re.sub(
        r'private\s+ConnexionService\s+connexionService;',
        'private ConnexionServiceClient connexionService;',
        content
    )

    # 4. Remplacer les initialisations
    content = re.sub(
        r'personneService\s*=\s*new\s+PersonneService\(\);',
        'annuaireService = new AnnuaireServiceClient();',
        content
    )
    content = re.sub(
        r'connexionService\s*=\s*new\s+ConnexionService\(\);',
        'connexionService = new ConnexionServiceClient();',
        content
    )

    # 5. Remplacer tous les appels personneService par annuaireService
    content = re.sub(r'\bpersonneService\.', 'annuaireService.', content)

    # 6. Remplacer les méthodes utilitaires
    content = re.sub(
        r'annuaireService\.categorieToString\(',
        'CategorieUtil.categorieToString(',
        content
    )
    content = re.sub(
        r'annuaireService\.stringToCategorie\(',
        'CategorieUtil.stringToCategorie(',
        content
    )

    if content != original_content:
        # Sauvegarder une copie de backup
        backup_path = filepath + '.backup'
        with open(backup_path, 'w', encoding='utf-8') as f:
            f.write(original_content)
        print(f"    Backup créé: {backup_path}")

        # Écrire le fichier migré
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"    Migration terminée!")
        return True
    else:
        print(f"     Aucun changement nécessaire")
        return False

def main():
    print("╔═══════════════════════════════════════════════════════════════╗")
    print("║    MIGRATION AUTOMATIQUE DES CONTRÔLEURS VERS RMI          ║")
    print("╚═══════════════════════════════════════════════════════════════╝")

    migrated = 0
    for controller in CONTROLLERS:
        filepath = os.path.join(CONTROLLERS_DIR, controller)
        if os.path.exists(filepath):
            if migrate_controller(filepath):
                migrated += 1
        else:
            print(f"  Fichier non trouvé: {controller}")

    print(f"\n{'='*65}")
    print(f" Migration terminée: {migrated}/{len(CONTROLLERS)} fichiers migrés")
    print(f"{'='*65}")
    print("\n Prochaines étapes:")
    print("   1. Vérifiez les fichiers .backup si besoin")
    print("   2. Recompilez le projet (Build → Rebuild Project)")
    print("   3. Redémarrez serveur puis client")
    print("   4. Testez en arrêtant le serveur → Service Indisponible")

if __name__ == '__main__':
    main()

