package  ltlgen.fitnesses;

import ec.EvolutionState;
import  automaton.Automaton;
import  verifier.Verifier;

public class ScenarioFitness extends SingleFitness{

    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        Verifier verifier = new Verifier("Scenario.smv");
        int test = verifier.verify(formula);
        if (test == 1) {
            return -1.0;
        } else {
            return 1.0;
        }
    }
}
