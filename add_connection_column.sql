-- Script SQL pour ajouter la colonne est_connecte à la table personne
-- Ce script doit être exécuté dans votre base de données

-- Ajouter la colonne est_connecte avec une valeur par défaut de 0 (false)
ALTER TABLE personne
ADD COLUMN est_connecte BOOLEAN DEFAULT FALSE;

-- Mettre à jour tous les enregistrements existants pour qu'ils soient marqués comme déconnectés
UPDATE personne SET est_connecte = FALSE;

-- Optionnel: Créer un index sur la colonne est_connecte pour améliorer les performances des requêtes
CREATE INDEX idx_personne_est_connecte ON personne(est_connecte);

-- Afficher la structure de la table pour vérifier que la colonne a été ajoutée
DESCRIBE personne;
