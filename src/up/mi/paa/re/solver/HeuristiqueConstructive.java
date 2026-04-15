package up.mi.paa.re.solver;

import java.util.*;
import up.mi.paa.re.models.Generateur;
import up.mi.paa.re.models.Maison;
import up.mi.paa.re.models.ReseauElectrique;

//====================================================
// HeuristiqueConstructive Best Fit
//====================================================
// Algorithme constructif pour connecter les maisons aux générateurs
// en priorisant les maisons à forte consommation et en minimisant la surcharge
// et la dispersion de charge.
public class HeuristiqueConstructive {

    // ---------------------------------------------------
    // Constructeur vide
    // ---------------------------------------------------
    public HeuristiqueConstructive() {}

    // ---------------------------------------------------
    // Méthode principale : connecter les maisons
    // ---------------------------------------------------
    public ReseauElectrique construireReseau(ReseauElectrique re) {
        re.reinitialiserConnexions();

        List<Maison> maisonsTriees = trierMaisonsParConso(re);
        List<Generateur> generateurs = new ArrayList<>(re.getGenerateurs().values());

        // Map pour suivre les charges sans recalculer à chaque fois
        Map<String, Double> chargesActuelles = initialiserCharges(generateurs);

        // Boucle principale : placer chaque maison
        for (Maison m : maisonsTriees) {
            Generateur meilleurG = trouverGenerateurMeilleurPlace(m, generateurs, chargesActuelles);
            if (meilleurG != null) {
                re.connecterMaisonGenerateur(m.getNom(), meilleurG.getNom());
                chargesActuelles.put(meilleurG.getNom(), chargesActuelles.get(meilleurG.getNom()) + m.getConso());
            }
        }
        
        return re;
    }

    // ---------------------------------------------------
    // Tri décroissant des maisons par consommation
    // ---------------------------------------------------
    private List<Maison> trierMaisonsParConso(ReseauElectrique re) {
        List<Maison> maisons = new ArrayList<>(re.getMaisons().values());
        maisons.sort((m1, m2) -> Double.compare(m2.getConso(), m1.getConso()));
        return maisons;
    }

    // ---------------------------------------------------
    // Initialisation des charges des générateurs à 0
    // ---------------------------------------------------
    private Map<String, Double> initialiserCharges(List<Generateur> generateurs) {
        Map<String, Double> charges = new HashMap<>();
        for (Generateur g : generateurs) {
            charges.put(g.getNom(), 0.0);
        }
        return charges;
    }

    // ---------------------------------------------------
    // Trouver le générateur le plus adapté pour cette maison
    // ---------------------------------------------------
    private Generateur trouverGenerateurMeilleurPlace(Maison m, List<Generateur> generateurs, Map<String, Double> charges) {
        Generateur meilleur = null;
        double plusPetiteDiff = Double.MAX_VALUE;

        for (Generateur g : generateurs) {
            double espaceRestant = g.getCapacite() - charges.get(g.getNom());
            double diff = espaceRestant - m.getConso();

            // On cherche le générateur qui peut accueillir la maison avec le moins d’espace perdu
            if (diff >= 0 && diff < plusPetiteDiff) {
                plusPetiteDiff = diff;
                meilleur = g;
            }
        }

        // Si aucune place “propre”, choisir celui avec le plus d’espace disponible (peut surcharger)
        if (meilleur == null) {
            meilleur = trouverGenerateurAvecPlusDespace(generateurs, charges);
        }

        return meilleur;
    }

    // ---------------------------------------------------
    // Méthode utilitaire pour trouver le générateur avec le plus d’espace
    // ---------------------------------------------------
    private Generateur trouverGenerateurAvecPlusDespace(List<Generateur> generateurs, Map<String, Double> charges) {
        Generateur best = null;
        double maxSpace = -Double.MAX_VALUE;
        for (Generateur g : generateurs) {
            double espace = g.getCapacite() - charges.get(g.getNom());
            if (espace > maxSpace) {
                maxSpace = espace;
                best = g;
            }
        }
        return best;
    }
}
