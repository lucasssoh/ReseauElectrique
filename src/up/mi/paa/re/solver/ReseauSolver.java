package up.mi.paa.re.solver;

import up.mi.paa.re.models.ReseauElectrique;

public interface ReseauSolver {
    ReseauElectrique optimiser(ReseauElectrique re, double lambda, int k);
}
