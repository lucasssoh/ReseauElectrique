package up.mi.paa.re.models;

import java.io.IOException;
import java.util.*;
import up.mi.paa.re.io.LoaderStrategy;
import up.mi.paa.re.io.SaverStrategy;

//====================================================
// Classe ReseauElectrique
//====================================================
// Cette classe gère l'ensemble des objets du réseau électrique
// : générateurs, maisons, connexions, vérifications et calculs

public class ReseauElectrique {

    private final Map<String, Maison> maisons = new HashMap<>();
    private final Map<String, Generateur> generateurs = new HashMap<>();
    private final Map<Maison, Generateur> connexions = new HashMap<>();

    public ReseauElectrique() {}

    // ============================================================
    // --- Réinitialisations ---
    // ============================================================
    public void reinitialiserReseau() {
        maisons.clear();
        generateurs.clear();
        reinitialiserConnexions();
    }
    
    public void reinitialiserConnexions() {
    	connexions.clear();
    }
    // ============================================================
    // --- Ajout / modification d’éléments ---
    // ============================================================
    public void ajouterGenerateur(String nom, double capaciteMax) {
        if (generateurs.containsKey(nom)) {
            generateurs.get(nom).setCapacite(capaciteMax);
            System.out.println("Capacité du générateur '" + nom + "' mise à jour.");
        } else {
            generateurs.put(nom, new Generateur(nom, capaciteMax));
            System.out.println("Générateur '" + nom + "' ajouté.");
        }
    }

    public void ajouterMaison(String nom, Conso conso) {
        if (maisons.containsKey(nom)) {
            maisons.get(nom).setConso(conso);
            System.out.println("Consommation de la maison '" + nom + "' mise à jour.");
        } else {
            maisons.put(nom, new Maison(nom, conso));
            System.out.println("Maison '" + nom + "' ajoutée.");
        }
    }

    public void connecterMaisonGenerateur(String nomMaison, String nomGenerateur) {
        Maison maison = maisons.get(nomMaison);
        Generateur generateur = generateurs.get(nomGenerateur);

        if (maison == null) {
            System.out.println("Connexion impossible : maison '" + nomMaison + "' introuvable.");
            return;
        }
        if (generateur == null) {
            System.out.println("Connexion impossible : générateur '" + nomGenerateur + "' introuvable.");
            return;
        }

        connexions.put(maison, generateur);
        System.out.println("Connexion créée : (" + maison.getNom() + " - " + generateur.getNom() + ").");
    }

    public void supprimerConnexionMaison(String nomMaison) {
        Maison maison = maisons.get(nomMaison);
        if (maison == null) {
            System.out.println("X Nom invalide : ce n'est pas une maison.");
            return;
        }
        if (connexions.remove(maison) != null) {
            System.out.println("! Connexion supprimée pour " + maison.getNom());
        } else {
            System.out.println("X Aucune connexion existante pour cette maison.");
        }
    }

    public void modifierConnexionMaison(String nomMaison, String nomNouveauGenerateur) {
        Maison m = maisons.get(nomMaison);
        Generateur nouveauG = generateurs.get(nomNouveauGenerateur);

        if (m == null || nouveauG == null) {
            System.out.println("X Maison ou générateur inconnu.");
            return;
        }

        Generateur ancienG = connexions.get(m);
        if (ancienG == null) {
            System.out.println("X La maison '" + nomMaison + "' n'est connectée à aucun générateur.");
            return;
        }

        connexions.put(m, nouveauG);
        System.out.println("🔄 Connexion mise à jour : " 
                + nomMaison + " était connectée à " + ancienG.getNom() 
                + ", maintenant connectée à " + nouveauG.getNom() + ".");
    }

    // ============================================================
    // --- Vérifications et calculs ---
    // ============================================================
    public void verifierCoherence() {
        List<String> erreurs = new ArrayList<>();
        for (Maison m : maisons.values()) {
            if (!connexions.containsKey(m)) {
                erreurs.add(m.getNom() + " : non connectée");
            }
        }

        if (erreurs.isEmpty()) {
            System.out.println("Toutes les maisons sont connectées correctement !");
        } else {
            System.out.println("Problèmes détectés :");
            erreurs.forEach(System.out::println);
        }
    }

    public Map<Generateur, Double> calcCharge() {
        Map<Generateur, Double> charges = new HashMap<>();
        for (Map.Entry<Maison, Generateur> e : connexions.entrySet()) {
            double conso = e.getKey().getConso();
            charges.merge(e.getValue(), conso, Double::sum);
        }
        for (Generateur g : generateurs.values()) {
            charges.putIfAbsent(g, 0.0);
        }
        return charges;
    }

    public double getTauxUtilisation(Generateur g) {
        if (g.getCapacite() == 0) return 0.0; // Sécurité division par zéro
        
        // On calcule la charge totale connectée à ce générateur
        double chargeTotale = 0.0;
        for (Map.Entry<Maison, Generateur> entry : connexions.entrySet()) {
            if (entry.getValue().equals(g)) {
                chargeTotale += entry.getKey().getConso();
            }
        }
        return chargeTotale / g.getCapacite();
    }

    public double calcDisp() {
        if (generateurs.isEmpty()) return 0.0;

        // Calculer tous les taux d'utilisation
        List<Double> taux = new ArrayList<>();
        for (Generateur g : generateurs.values()) {
            taux.add(getTauxUtilisation(g));
        }

        // Calculer la moyenne (u barre)
        double moyenne = taux.stream().mapToDouble(val -> val).average().orElse(0.0);

        // 3. Sommer les écarts absolus
        return taux.stream().mapToDouble(u -> Math.abs(u - moyenne)).sum();
    }


    public double calcSurcharge() {
        double totalSurcharge = 0.0;

        for (Generateur g : generateurs.values()) {
            double u_g = getTauxUtilisation(g);
            
            if (u_g > 1.0) {
                totalSurcharge += (u_g - 1.0);
            }
        }
        return totalSurcharge;
    }


    public double cout(double lambda) {
        double disp = calcDisp();
        double surcharge = calcSurcharge();
        return disp + lambda * surcharge;
    }

    public String StringCout(double lambda) {
        double disp = calcDisp();
        double surcharge = calcSurcharge();
        double total = disp + lambda * surcharge;

        return String.format(
            "Disp(S) = %.2f | Surcharge(S) = %.2f | Coût total = %.2f%n",
            disp, surcharge, total
        );
    }


    public void afficher() {
        System.out.println("\n=== Réseau actuel ===");
        for (Generateur g : generateurs.values()) {
            System.out.println(g);
            for (Map.Entry<Maison, Generateur> e : connexions.entrySet()) {
                if (e.getValue().equals(g)) {
                    System.out.println("  └── " + e.getKey());
                }
            }
        }
        System.out.println("======================\n");
    }

    
    public ReseauElectrique cloner() {
        ReseauElectrique copy = new ReseauElectrique();

        for (Generateur g : generateurs.values()) {
            copy.ajouterGenerateur(g.getNom(), g.getCapacite());
        }

        for (Maison m : maisons.values()) {
            copy.ajouterMaison(m.getNom(), m.getConsoRaw());
        }

        for (Map.Entry<Maison, Generateur> e : connexions.entrySet()) {
            copy.connecterMaisonGenerateur(e.getKey().getNom(), e.getValue().getNom());
        }

        return copy;
    }
    
    public void copierDepuis(ReseauElectrique autre) {
        this.reinitialiserReseau();
        // Copier générateurs
        for (Generateur g : autre.getGenerateurs().values()) {
            this.ajouterGenerateur(g.getNom(), g.getCapacite());
        }
        // Copier maisons
        for (Maison m : autre.getMaisons().values()) {
            this.ajouterMaison(m.getNom(), m.getConsoRaw());
        }
        // Copier connexions
        for (Map.Entry<Maison, Generateur> e : autre.getConnexions().entrySet()) {
            this.connecterMaisonGenerateur(e.getKey().getNom(), e.getValue().getNom());
        }
    }


    
    // ============================================================
    // --- Accesseurs ---
    // ============================================================
    public Map<String, Maison> getMaisons() { return maisons; }
    public Map<String, Generateur> getGenerateurs() { return generateurs; }
    public Map<Maison, Generateur> getConnexions() { return connexions; }
    
    // ============================================================
    // --- IO du reseau ---
    // ============================================================
    public void chargerReseau(String fichier, LoaderStrategy loader) throws IOException {
        loader.charger(fichier, this);
    }

    public void sauvegarderReseau(String fichier, SaverStrategy saver) throws IOException {
        saver.sauvegarder(fichier, this);
    }
}
