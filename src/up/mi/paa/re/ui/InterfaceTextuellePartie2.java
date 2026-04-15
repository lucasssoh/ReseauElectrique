package up.mi.paa.re.ui;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.InputMismatchException;
import java.util.Scanner;
import up.mi.paa.re.io.FileTextSaver;
import up.mi.paa.re.models.ReseauElectrique;
import up.mi.paa.re.solver.SolverMain;

//====================================================
// Classe InterfaceTextuellePartie2
//====================================================
// Gère uniquement l'UI : saisie utilisateur et affichage si fichier a ete saisie
// Toute la logique métier est déléguée à ReseauElectrique

public class InterfaceTextuellePartie2 {

    private final String nom;
    private final ReseauElectrique re;
    private final Scanner sc = new Scanner(System.in);

    public InterfaceTextuellePartie2(String nom, ReseauElectrique re) {
        this.nom = nom;
        this.re = re;
    }

    public void CLIPrompt() {
        init();
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
        System.out.println(" (1) Resolution automatique");
        System.out.println(" (2) Sauvegarder la nouvelle solution");
        System.out.println(" (3) Fin");
    }

    public void init() {
        while (true) {
            pInit();
            int choix = lireEntier("Votre choix : ");

            switch (choix) {

                case 1 -> { // Optimise le reseau
                    System.out.println("Optimisation en cours...");
                    SolverMain solver = new SolverMain();
                    ReseauElectrique optimised = solver.optimiser(re, 10, 200);
                        // Copier le résultat dans le réseau actuel
                    re.copierDepuis(optimised);
                    System.out.println("Optimisation terminée. Cout : " + re.cout(choix));
                }

                case 2 -> { // Sauvegarde le reseau
                    String nomFichierSortie = demanderNomFichierSortie();
                    try {
                        new FileTextSaver().sauvegarder(nomFichierSortie, re);
                        System.out.println("Réseau sauvegardé dans " + nomFichierSortie);
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
                    }
                }

                case 3 -> { // Quitter
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

	public String getNom() {
		return this.nom;
	}

}
