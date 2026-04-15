package up.mi.paa.re;

import java.io.IOException;
import up.mi.paa.re.io.FileTextLoader;
import up.mi.paa.re.models.ReseauElectrique;
import up.mi.paa.re.ui.InterfaceTextuellePartie1;
import up.mi.paa.re.ui.InterfaceTextuellePartie2;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length > 1) {
            System.out.println("Usage : java up.mi.paa.re.Main <fichier>");
            System.exit(1);
        }

        

        // Interface
        if (args.length == 0){
            ReseauElectrique re = new ReseauElectrique();

            InterfaceTextuellePartie1 it = new InterfaceTextuellePartie1(re);
            it.CLIPrompt();
        }
        else{
            String fichier = args[0];

            // Charger le réseau
            ReseauElectrique re = new ReseauElectrique();
            re.chargerReseau(fichier, new FileTextLoader());

            InterfaceTextuellePartie2 it = new InterfaceTextuellePartie2(fichier, re);
            it.CLIPrompt();
        }
        
    }
}
