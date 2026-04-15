package up.mi.paa.re.ui;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.InputMismatchException;
import java.util.Scanner;
import up.mi.paa.re.io.FileTextSaver;
import up.mi.paa.re.models.Conso;
import up.mi.paa.re.models.ReseauElectrique;
import up.mi.paa.re.solver.SolverMain;

//====================================================
// Classe InterfaceTextuellePartie1
//====================================================
// Gère uniquement l'UI : saisie utilisateur et affichage si aucun fichier n'a ete mis
// Toute la logique métier est déléguée à ReseauElectrique

public class InterfaceTextuellePartie1 {


    private final ReseauElectrique re;
    private final Scanner sc = new Scanner(System.in);

    public InterfaceTextuellePartie1(ReseauElectrique re) {
        this.re = re;
    }

    public void CLIPrompt() {
        init();
        explore();
    }

    // ============================================================
    // --- Méthodes utilitaires pour la saisie ---
    // ============================================================
    private int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                int valeur = sc.nextInt();
                sc.nextLine();
                return valeur;
            } catch (InputMismatchException e) {
                System.out.println(" Entrée invalide : veuillez entrer un nombre entier.");
                sc.nextLine();
            }
        }
    }

    private String lireLigne(String message) {
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println(" Entrée vide. Veuillez réessayer.");
        }
    }

    // ============================================================
    // --- Menu création du réseau ---
    // ============================================================
    private static void pInit() {
        System.out.println("\nVeuillez sélectionner une opération :");
        System.out.println(" (1) Ajouter un générateur");
        System.out.println(" (2) Ajouter une maison");
        System.out.println(" (3) Connecter une maison à un générateur");
        System.out.println(" (4) Supprimer une connexion existante");
        System.out.println(" (5) Fin");
    }

    public void init() {
        while (true) {
            pInit();
            int choix = lireEntier("Votre choix : ");

            switch (choix) {

                case 1 -> { // Ajouter générateur
                    String nom = lireLigne("Nom du générateur : ");
                    int capacite = lireEntier("Capacité max : ");
                    re.ajouterGenerateur(nom, capacite);
                }

                case 2 -> { // Ajouter maison
                    String nom = lireLigne("Nom de la maison : ");
                    String consoStr = lireLigne("Consommation (BASSE/NORMAL/FORTE) : ");
                    try {
                        Conso conso = Conso.valueOf(consoStr.toUpperCase());
                        re.ajouterMaison(nom, conso);
                    } catch (IllegalArgumentException e) {
                        System.out.println("X: Consommation inconnue. Valeurs possibles : BASSE, NORMAL, FORTE.");
                    }
                }

                case 3 -> { // Connecter maison -> générateur
                    String maison = lireLigne("Nom de la maison : ");
                    String generateur = lireLigne("Nom du générateur : ");
                    re.connecterMaisonGenerateur(maison, generateur);
                }

                case 4 -> { // Supprimer connexion
                    String maison = lireLigne("Maison pour supprimer la connexion : ");
                    re.supprimerConnexionMaison(maison);
                }

                case 5 -> { // Fin du menu init
                    re.verifierCoherence();
                    System.out.println("Terminé.");
                    return;
                }

                default -> System.out.println("X: Choix invalide !");
            }
        }
    }

    // ============================================================
    // --- Menu exploitation du réseau ---
    // ============================================================
    private void pExplore() {
        System.out.println("\nVeuillez sélectionner une opération :");
        System.out.println(" (1) Calculer et afficher le coût du réseau");
        System.out.println(" (2) Modifier une connexion");
        System.out.println(" (3) Afficher le réseau");
        System.out.println(" (4) Optimiser le réseau");
        System.out.println(" (5) Sauvegarde le fichier");
        System.out.println(" (6) Fin");
    }

    public void explore() {
        while (true) {
            pExplore();
            int choix = lireEntier("Votre choix : ");

            switch (choix) {

                case 1 -> System.out.print(re.StringCout(10));

                case 2 -> { // Modifier connexion
                    String maison = lireLigne("Maison à modifier : ");
                    String nouveauGen = lireLigne("Nouveau générateur : ");
                    re.modifierConnexionMaison(maison, nouveauGen);
                }

                case 3 -> re.afficher();
                
                case 4 -> { // Optimisation
                    String rep = lireLigne("Appliquer l'optimisation ? (O/N) : ");
                    if (rep.equalsIgnoreCase("O")) {
                        System.out.println("Optimisation en cours...");
                        SolverMain solver = new SolverMain();
                        ReseauElectrique optimised = solver.optimiser(re, 10, 200);

                        // Copier le résultat dans le réseau actuel
                        re.copierDepuis(optimised);

                        // Demander le fichier de sortie
                        String nomFichierSortie = demanderNomFichierSortie();

                        // Sauvegarde
                        try {
                            new FileTextSaver().sauvegarder(nomFichierSortie, re);
                            System.out.println("Réseau optimisé et sauvegardé dans " + nomFichierSortie);
                        } catch (IOException e) {
                            System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
                        }
                    } else {
                        System.out.println("Optimisation annulée.");
                    }
                }

                case 5 -> { // Sauvegarde dans un nouveau fichier
                    // Demander le fichier de sortie
                    String nomFichierSortie = demanderNomFichierSortie();

                    // Sauvegarde
                    try {
                        new FileTextSaver().sauvegarder(nomFichierSortie, re);
                        System.out.println("Réseau optimisé et sauvegardé dans " + nomFichierSortie);
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
                    }
                }

                case 6 -> { // Quitter 
                    System.out.println("Terminé.");
                    finish();
                }

                default -> System.out.println("X: Choix invalide !");
            }
        }
    }

    public void finish() {
        sc.close();
        System.exit(0);
    }
    
    private String demanderNomFichierSortie() {
        while (true) {
            String nomFichier = lireLigne("Nom du fichier de sortie : ");
            try {
                verifierFichierExiste(nomFichier);
                return nomFichier;
            } catch (FileAlreadyExistsException e) {
                System.out.println(e.getMessage() + " Veuillez choisir un autre nom.");
            }
        }
    }

    private void verifierFichierExiste(String nomFichier) throws FileAlreadyExistsException {
        java.io.File f = new java.io.File(nomFichier);
        if (f.exists()) {
            throw new FileAlreadyExistsException("Le fichier '" + nomFichier + "' existe déjà.");
        }
    }

}
