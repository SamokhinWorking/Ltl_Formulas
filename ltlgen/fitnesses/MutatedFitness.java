package  ltlgen.fitnesses;

import ec.EvolutionState;
import ec.gp.GPTree;
import ec.util.MersenneTwisterFast;
import  automaton.ECAlgorithm;
import  automaton.ECStates;
import  automaton.ECTransition;
import  tree.BooleanExpressionTree;
import  automaton.Automaton;
import  verifier.Verifier;


public class MutatedFitness extends SingleFitness {

    private static final int SAMPLE_SIZE = 50;
    private static final Automaton auto = new Automaton("CentralController.xml");

    public double getResult(String formula, Automaton auto, EvolutionState evolutionState) {
        MersenneTwisterFast random = evolutionState.random[(int)Thread.currentThread().getId() - 1];
        Automaton mutant = new Automaton(Automaton.automaton);
        mutant.mutate(random);
        return new Verifier(mutant).verify(formula);
    }

    @Override
    public double getFitness(String formula, int complexity, EvolutionState state) {
        double result = 0.0;
        for(int i = 0; i < SAMPLE_SIZE; i++) {
            result += getResult(formula, auto, state);
        }
        return 1 - result / SAMPLE_SIZE;
    }
}
