package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import automaton.Automaton;
import ltlgen.LTLData;

public class OutputVariable extends ERC implements Verifiable {

    private int value;

    @Override
    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(Automaton.automaton.getOutputVars().length);
    }

    @Override
    public boolean nodeEquals(GPNode gpNode) {
        return gpNode instanceof OutputVariable && value == ((OutputVariable) gpNode).value;
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
        return "C." + Automaton.automaton.getOutputVars()[value];
    }

    @Override
    public String encode() {
        return Code.encode(value);
    }

}
