package ltlgen.formulas.input;


import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import helpClass.*;
import ltlgen.LTLData;
import ltlgen.LTLProblem;


import ltlgen.formulas.Verifiable;


public class pp1 extends GPNode implements Verifiable {

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
        String input="pp1";
        return input;
    }

}