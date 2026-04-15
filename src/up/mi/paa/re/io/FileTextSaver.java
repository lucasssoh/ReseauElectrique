package up.mi.paa.re.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import up.mi.paa.re.models.Conso;
import up.mi.paa.re.models.Generateur;
import up.mi.paa.re.models.Maison;
import up.mi.paa.re.models.ReseauElectrique;

public class FileTextSaver implements SaverStrategy {

    @Override
    public void sauvegarder(String fichier, ReseauElectrique re) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichier))) {

            saveGenerateurs(bw, re);
            saveMaisons(bw, re);
            saveConnexions(bw, re);
        }
    }


    // ----------------------------------------------------
    // Écriture des générateurs
    // ----------------------------------------------------
    private void saveGenerateurs(BufferedWriter bw, ReseauElectrique re) throws IOException {
        for (Generateur g : re.getGenerateurs().values()) {
        	bw.write(String.format(java.util.Locale.US, "generateur(%s,%.2f).", g.getNom(), g.getCapacite()));
            bw.newLine();
        }
    }


    // ----------------------------------------------------
    // Écriture des maisons
    // ----------------------------------------------------
    private void saveMaisons(BufferedWriter bw, ReseauElectrique re) throws IOException {
        for (Maison m : re.getMaisons().values()) {
            bw.write(String.format("maison(%s,%s).", m.getNom(), Conso.fromValue(m.getConso())));
            bw.newLine();
        }
    }


    // ----------------------------------------------------
    // Écriture des connexions
    // ----------------------------------------------------
    private void saveConnexions(BufferedWriter bw, ReseauElectrique re) throws IOException {
        re.getConnexions().forEach((maison, generateur) -> {
            try {
                bw.write(String.format(
                    "connexion(%s,%s).",
                    maison.getNom(),
                    generateur.getNom()
                ));
                bw.newLine();
            } catch (IOException e) {
                // Si une écriture échoue, on remonte l'erreur
                throw new RuntimeException(e);
            }
        });
    }
    
    
}
