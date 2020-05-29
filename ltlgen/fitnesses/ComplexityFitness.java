package ltlgen.fitnesses;

import ec.EvolutionState;
import automaton.Automaton;

public class ComplexityFitness extends SingleFitness {
    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        double result = 1.0 / (1.0 + complexity);
        return result;
    }
}
