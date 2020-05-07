package  ltlgen.formulas.predicate;


import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import  ltlgen.LTLData;
import  ltlgen.formulas.Verifiable;


public class dropped extends GPNode implements Verifiable {

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
        return "dropped";
    }
}