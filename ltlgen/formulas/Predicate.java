package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ltlgen.LTLData;
import ltlgen.LTLProblem;

public class Predicate extends ERC implements Verifiable {
    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(LTLProblem.predicates.length);
    }

    @Override
    public boolean nodeEquals(GPNode gpNode) {
        return gpNode instanceof Predicate && value == ((Predicate) gpNode).value;
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = toString();
        data.complexity = 1;
    }

    @Override
    public String toStringForHumans() {
        return toString();
    }

    @Override
    public String toStringForVerifier() {
        return toString();
    }

    @Override
    public String toString() {
        return LTLProblem.predicates[value];
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

}
