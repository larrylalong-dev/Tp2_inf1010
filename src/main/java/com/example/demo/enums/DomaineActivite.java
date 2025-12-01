package com.example.demo.enums;

public enum DomaineActivite {
    INFORMATIQUE("Informatique"),
    GENIE_LOGICIEL("Génie logiciel"),
    INTELLIGENCE_ARTIFICIELLE("Intelligence artificielle"),
    RESEAUX_SECURITE("Réseaux et sécurité"),
    BASE_DONNEES("Base de données"),
    SYSTEMES_DISTRIBUES("Systèmes distribués"),
    INTERFACE_UTILISATEUR("Interface utilisateur"),
    ALGORITHMES("Algorithmes"),
    MATHEMATIQUES_APPLIQUEES("Mathématiques appliquées"),
    RECHERCHE_OPERATIONNELLE("Recherche opérationnelle"),
    VISION_ORDINATEUR("Vision par ordinateur"),
    APPRENTISSAGE_AUTOMATIQUE("Apprentissage automatique");

    private final String displayText;

    DomaineActivite(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static DomaineActivite fromDisplayText(String displayText) {
        for (DomaineActivite domaine : values()) {
            if (domaine.displayText.equals(displayText)) {
                return domaine;
            }
        }
        return INFORMATIQUE; // Par défaut
    }

    public static String[] getAllDisplayTexts() {
        DomaineActivite[] values = values();
        String[] displayTexts = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayTexts[i] = values[i].displayText;
        }
        return displayTexts;
    }
}
