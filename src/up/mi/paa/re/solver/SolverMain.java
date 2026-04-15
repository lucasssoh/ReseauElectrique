package up.mi.paa.re.solver;

import up.mi.paa.re.models.ReseauElectrique;

public class SolverMain {

    private final HeuristiqueConstructive hBestFit = new HeuristiqueConstructive();
    private final SolverILS solverILS = new SolverILS();

    /**
     * Processus complet :
     *   - Tester solution brute (telle qu'elle)
     *   - Construire BestFit
     *   - Garder la meilleure des deux
     *   - Lancer l’ILS dessus
     */
    public ReseauElectrique optimiser(ReseauElectrique re, double lambda, int iterationsILS) {

        // 1) Coût du réseau initial
        double costInitial = re.cout(lambda);

        // 2) Solution BestFit
        ReseauElectrique solBF = hBestFit.construireReseau(re.cloner());
        double costBF = solBF.cout(lambda);

        // 3) Choisir meilleur point de départ
        ReseauElectrique start = (costBF < costInitial) ? solBF : re.cloner();

        // 4) Lancer ILS
        return solverILS.optimiser(start, lambda, iterationsILS);
    }
}
