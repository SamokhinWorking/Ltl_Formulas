package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;
import automaton.Automaton;
import verifier.Verifier;

public class ScenarioFitness extends SingleFitness {

    private Verifier verifier;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        verifier = new Verifier("Scenario.smv");
    }

    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        return 1 - verifier.verify(formula).getUnsatisfiedFormulaFunction();
    }
}
