-- Script pour corriger la base de données - Ajouter la colonne est_connecte
-- Exécutez ce script dans votre base de données MySQL

-- Ajouter la colonne est_connecte avec une valeur par défaut de FALSE
ALTER TABLE personne
ADD COLUMN est_connecte BOOLEAN DEFAULT FALSE;

-- Mettre à jour tous les enregistrements existants pour qu'ils soient marqués comme déconnectés
UPDATE personne SET est_connecte = FALSE;

-- Créer un index pour améliorer les performances
CREATE INDEX idx_personne_est_connecte ON personne(est_connecte);

-- Vérifier que la colonne a été ajoutée
DESCRIBE personne;

-- Afficher un message de confirmation
SELECT 'Colonne est_connecte ajoutée avec succès!' as message;
