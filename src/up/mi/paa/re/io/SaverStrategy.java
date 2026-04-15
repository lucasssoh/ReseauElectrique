package up.mi.paa.re.io;

import java.io.IOException;
import up.mi.paa.re.models.ReseauElectrique;

public interface SaverStrategy {
    void sauvegarder(String fichier, ReseauElectrique re) throws IOException;
}
