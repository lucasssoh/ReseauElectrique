package up.mi.paa.re.solver;

import up.mi.paa.re.models.*;
import java.util.*;

public class SolverILS implements ReseauSolver {

    private final Random random = new Random();

    @Override
    public ReseauElectrique optimiser(ReseauElectrique re, double lambda, int k) {
        // ─────────────────────────────────────────────
        // 1) Travailler sur une copie du réseau
        // ─────────────────────────────────────────────
        ReseauElectrique courant = re.cloner();
        ReseauElectrique meilleur = courant.cloner();
        double coutCourant = courant.cout(lambda);
        double meilleurCout = coutCourant;

        List<Generateur> generateurs = new ArrayList<>(courant.getGenerateurs().values());

        // ─────────────────────────────────────────────
        // 2) Boucle ILS
        // ─────────────────────────────────────────────
        for (int i = 0; i < k; i++) {

            // Perturbation guidée : surcharge ou dispersion
            Generateur gPerturb = choisirGenerateurSurcharge(courant);
            if (gPerturb == null) {
                gPerturb = choisirGenerateurParDispersion(courant);
            }

            if (gPerturb == null) continue; // aucun générateur à déplacer

            // Choisir une maison connectée à ce générateur
            Maison m = choisirMaisonConnectee(courant, gPerturb);
            if (m == null) continue;

            Generateur ancienG = courant.getConnexions().get(m);

            // Trouver un générateur sous-utilisé pour réduire dispersion/surcharge
            Generateur nouveauG = choisirGenerateurSousUtilise(courant, m, generateurs);
            if (nouveauG == null) continue;

            // Appliquer la perturbation
            courant.connecterMaisonGenerateur(m.getNom(), nouveauG.getNom());

            double nouveauCout = courant.cout(lambda);

            // Acceptation si meilleur ou égal
            if (nouveauCout <= coutCourant) {
                coutCourant = nouveauCout;
                if (nouveauCout < meilleurCout) {
                    meilleur = courant.cloner();
                    meilleurCout = nouveauCout;
                }
            } else {
                // Annulation si c'est pire
                courant.connecterMaisonGenerateur(m.getNom(), ancienG.getNom());
            }
        }

        System.out.println("ILS : coût final = " + meilleurCout);
        return meilleur;
    }

    // ─────────────────────────────────────────────
    // Fonctions utilitaires internes
    // ─────────────────────────────────────────────

    // Choisir générateur le plus surchargé (>1)
    private Generateur choisirGenerateurSurcharge(ReseauElectrique re) {
        double maxSurcharge = 0.0;
        Generateur gMax = null;
        for (Generateur g : re.getGenerateurs().values()) {
            double u = re.getTauxUtilisation(g);
            double surcharge = Math.max(0, u - 1.0);
            if (surcharge > maxSurcharge) {
                maxSurcharge = surcharge;
                gMax = g;
            }
        }
        return gMax;
    }

    // Choisir générateur qui déséquilibre le plus la charge (écart à la moyenne)
    private Generateur choisirGenerateurParDispersion(ReseauElectrique re) {
        Collection<Generateur> gens = re.getGenerateurs().values();
        if (gens.isEmpty()) return null;

        // Calcul de la moyenne des taux
        double sum = 0.0;
        for (Generateur g : gens) sum += re.getTauxUtilisation(g);
        double moyenne = sum / gens.size();

        // Choisir générateur avec le plus grand écart
        Generateur gMax = null;
        double maxEc = -1.0;
        for (Generateur g : gens) {
            double ecart = Math.abs(re.getTauxUtilisation(g) - moyenne);
            if (ecart > maxEc) {
                maxEc = ecart;
                gMax = g;
            }
        }
        return gMax;
    }

    // Choisir une maison connectée à un générateur donné
    private Maison choisirMaisonConnectee(ReseauElectrique re, Generateur g) {
        List<Maison> candidates = new ArrayList<>();
        for (Map.Entry<Maison, Generateur> e : re.getConnexions().entrySet()) {
            if (e.getValue().equals(g)) candidates.add(e.getKey());
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    // Choisir générateur sous-utilisé pour réduire dispersion/surcharge
    private Generateur choisirGenerateurSousUtilise(ReseauElectrique re, Maison m, List<Generateur> generateurs) {
        Generateur meilleur = null;
        double minTaux = Double.MAX_VALUE;
        for (Generateur g : generateurs) {
            if (g.equals(re.getConnexions().get(m))) continue;
            double u = re.getTauxUtilisation(g);
            if (u < minTaux) {
                minTaux = u;
                meilleur = g;
            }
        }
        return meilleur;
    }
}
