package com.example.demo.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Entite.Personne;
import CategorieEnum.Categorie;
import com.example.demo.service.PersonneService;
import com.example.demo.service.ConnexionService;

/**
 * Implémentation RMI des opérations d'annuaire, déléguant vers les services existants.
 */
public class RemoteAnnuaireImpl extends UnicastRemoteObject implements RemoteAnnuaire {

    private final PersonneService personneService;
    private final ConnexionService connexionService;

    public RemoteAnnuaireImpl() throws RemoteException {
        super();
        this.personneService = new PersonneService();
        this.connexionService = new ConnexionService();
    }

    @Override
    public List<Personne> getAll() throws RemoteException {
        return personneService.getAllMembres();
    }

    @Override
    public Personne getMembreById(int id) throws RemoteException {
        return personneService.getMembreById(id);
    }

    @Override
    public boolean ajouterMembre(Personne personne) throws RemoteException {
        return personneService.ajouterMembre(personne);
    }

    @Override
    public boolean modifierMembre(Personne personne) throws RemoteException {
        return personneService.modifierMembre(personne);
    }

    @Override
    public boolean supprimerMembre(Personne personne) throws RemoteException {
        return personneService.supprimerMembre(personne);
    }

    @Override
    public Personne rechercherUnmembre(Personne personne) throws RemoteException {
        return personneService.rechercherUnmembre(personne);
    }

    @Override
    public List<Personne> getMembresParCategorie(Categorie categorie) throws RemoteException {
        return personneService.getMembresParCategorie(categorie);
    }

    @Override
    public List<Personne> getProfesseursParDomaine(int domaine) throws RemoteException {
        return personneService.getProfesseursParDomaine(domaine);
    }

    @Override
    public int gererListeRouge(int identifiant, String metSurLrouge) throws RemoteException {
        return personneService.gererListeRouge(identifiant, metSurLrouge);
    }

    @Override
    public boolean ajouterAListeRouge(int id) throws RemoteException {
        return personneService.ajouterAListeRouge(id);
    }

    @Override
    public boolean retirerDeListeRouge(int id) throws RemoteException {
        return personneService.retirerDeListeRouge(id);
    }

    @Override
    public boolean marquerUtilisateurConnecte(int idPersonne) throws RemoteException {
        return connexionService.marquerUtilisateurConnecte(idPersonne);
    }

    @Override
    public boolean marquerUtilisateurDeconnecte(int idPersonne) throws RemoteException {
        return connexionService.marquerUtilisateurDeconnecte(idPersonne);
    }

    @Override
    public boolean verifierEtatConnexion(int idPersonne) throws RemoteException {
        return connexionService.verifierEtatConnexion(idPersonne);
    }

    @Override
    public List<Personne> getUtilisateursConnectes() throws RemoteException {
        return connexionService.getUtilisateursConnectes();
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }
}
