package up.mi.paa.re.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import up.mi.paa.re.models.Conso;
import up.mi.paa.re.models.ReseauElectrique;

public class FileTextLoader implements LoaderStrategy {

    private FileTextSection sectionActuelle;

    public FileTextLoader() {
    }

    @Override
    public void charger(String fichier, ReseauElectrique re) {

        // Reset du réseau avant de charger
        re.reinitialiserReseau();

        // Au début, on s’attend à lire des générateurs
        this.sectionActuelle = FileTextSection.GENERATEURS;

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                parseLigne(ligne.trim(), re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // Gère une ligne du fichier
    // ----------------------------------------------------
    private void parseLigne(String ligne, ReseauElectrique re) {

        // Ligne vide ou commentaire
        if (ligne.isEmpty() || ligne.startsWith("%"))
            return;

        if (ligne.startsWith("generateur")) {
            ensureSectionIs(FileTextSection.GENERATEURS);
            parseGenerateur(getInside(ligne), re);
            return;
        }

        if (ligne.startsWith("maison")) {

            // Si on était encore dans les générateurs, on passe à maisons
            if (sectionActuelle == FileTextSection.GENERATEURS) {
                sectionActuelle = FileTextSection.MAISONS;
            }

            ensureSectionIs(FileTextSection.MAISONS);
            parseMaison(getInside(ligne), re);
            return;
        }

        if (ligne.startsWith("connexion")) {

            // Passage en section connexions si nécessaire
            if (sectionActuelle != FileTextSection.CONNEXIONS) {
                sectionActuelle = FileTextSection.CONNEXIONS;
            }

            parseConnexion(getInside(ligne), re);
            return;
        }

        throw new IllegalArgumentException("Ligne non reconnue : " + ligne);
    }


    // ----------------------------------------------------
    // Parsing d'un générateur
    // Ex : generateur(nom, capacite)
    // ----------------------------------------------------
    private void parseGenerateur(String inside, ReseauElectrique re) {
        String[] parts = inside.split(",");
        String nom = parts[0].trim();
        int capacite = Integer.parseInt(parts[1].trim());

        re.ajouterGenerateur(nom, capacite);
    }


    // ----------------------------------------------------
    // Parsing d'une maison
    // Ex : maison(nom, Conso.MOYENNE)
    // ----------------------------------------------------
    private void parseMaison(String inside, ReseauElectrique re) {
        String[] parts = inside.split(",");
        String nom = parts[0].trim();
        Conso conso = Conso.valueOf(parts[1].trim());

        re.ajouterMaison(nom, conso);
    }


    // ----------------------------------------------------
    // Parsing d'une connexion
    // Ex : connexion(M1, G2)
    // ----------------------------------------------------
    private void parseConnexion(String inside, ReseauElectrique re) {
        String[] parts = inside.split(",");

        String a = parts[0].trim();
        String b = parts[1].trim();

        // Maison -> Générateur ou Générateur -> Maison
        if (re.getMaisons().containsKey(a) && re.getGenerateurs().containsKey(b)) {
            re.connecterMaisonGenerateur(a, b);
        } else if (re.getMaisons().containsKey(b) && re.getGenerateurs().containsKey(a)) {
            re.connecterMaisonGenerateur(b, a);
        } else {
            throw new IllegalStateException(
                "Connexion invalide (" + a + ", " + b + 
                ") : maison ou générateur inconnu."
            );
        }
    }


    // ----------------------------------------------------
    // Méthode utilitaire pour extraire le texte entre (...)
    // ----------------------------------------------------
    private String getInside(String ligne) {
        int i1 = ligne.indexOf('(');
        int i2 = ligne.lastIndexOf(')');
        return ligne.substring(i1 + 1, i2);
    }


    // ----------------------------------------------------
    // Permet de garantir que la section courante est celle attendue
    // ----------------------------------------------------
    private void ensureSectionIs(FileTextSection expected) {
        if (sectionActuelle != expected) {
            throw new IllegalStateException(
                "Erreur de section : attendu " + expected +
                ", trouvé " + sectionActuelle
            );
        }
    }

}

