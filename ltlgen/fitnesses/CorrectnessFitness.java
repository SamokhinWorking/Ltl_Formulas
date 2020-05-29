package ltlgen.fitnesses;

import ec.EvolutionState;
import automaton.Automaton;
import verifier.Verifier;

public class CorrectnessFitness extends SingleFitness {

    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        Verifier ver = new Verifier(Automaton.automaton);
        Verifier.VerificationResult vr = ver.verify(formula);
		/*
        if (vr.verified == 1) {
            System.out.println("Verified: " + formula);
        }
		*/
        return 1.0 - vr.getUnsatisfiedFormulaFunction();
    }
}
