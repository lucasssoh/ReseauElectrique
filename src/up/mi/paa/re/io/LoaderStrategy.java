package up.mi.paa.re.io;

import java.io.IOException;
import up.mi.paa.re.models.ReseauElectrique;

public interface LoaderStrategy {
    void charger(String fichier, ReseauElectrique re) throws IOException;
}
