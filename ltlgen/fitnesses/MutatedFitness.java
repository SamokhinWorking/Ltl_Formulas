package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import automaton.Automaton;
import verifier.Verifier;

import java.util.HashSet;
import java.util.Set;


public class MutatedFitness extends SingleFitness {

    private static final int SAMPLE_SIZE = 50;
    private static final Automaton auto = new Automaton("CentralController.xml");
    private static Automaton mutants[] = null;


    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        MersenneTwisterFast random = state.random[(int)Thread.currentThread().getId() - 1];
        mutants = new Automaton[SAMPLE_SIZE];

        Set<String> uniqueMutants = new HashSet<>();
        uniqueMutants.add(auto.toSMV());
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            mutants[i] = new Automaton(auto);
            while (uniqueMutants.contains(mutants[i].toSMV())) {
                mutants[i].mutate(random);
            }
            uniqueMutants.add(mutants[i].toSMV());
        }
    }

    public double getResult(String formula, int i) {
        return new Verifier(mutants[i]).verify(formula).verified;
    }

    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        double result = 0.0;
        for(int i = 0; i < SAMPLE_SIZE; i++) {
            result += getResult(formula, i);
        }
        double ans = 1.0 - result / SAMPLE_SIZE;

        System.out.println("Mutated: " + result + "/" + SAMPLE_SIZE);
        return ans;
    }
}
