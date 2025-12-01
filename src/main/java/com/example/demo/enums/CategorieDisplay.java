package com.example.demo.enums;

import CategorieEnum.Categorie;

public enum CategorieDisplay {
    PROFESSEUR("👨‍🏫 Professeur", Categorie.professeur),
    AUXILIAIRE("👨‍🎓 Auxiliaire d'enseignement", Categorie.auxiliaire),
    ETUDIANT("🎓 Étudiant", Categorie.etudiant),
    ADMINISTRATEUR("👔 Administrateur", Categorie.administrateur);

    private final String displayText;
    private final Categorie categorie;

    CategorieDisplay(String displayText, Categorie categorie) {
        this.displayText = displayText;
        this.categorie = categorie;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public static CategorieDisplay fromCategorie(Categorie categorie) {
        for (CategorieDisplay display : values()) {
            if (display.categorie == categorie) {
                return display;
            }
        }
        return PROFESSEUR; // Par défaut
    }

    public static CategorieDisplay fromDisplayText(String displayText) {
        for (CategorieDisplay display : values()) {
            if (display.displayText.equals(displayText)) {
                return display;
            }
        }
        return PROFESSEUR; // Par défaut
    }

    public static String[] getAllDisplayTexts() {
        CategorieDisplay[] values = values();
        String[] displayTexts = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayTexts[i] = values[i].displayText;
        }
        return displayTexts;
    }
}
