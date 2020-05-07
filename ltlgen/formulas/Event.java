package  ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import  automaton.Automaton;
import  ltlgen.LTLData;

import java.util.Random;

public class Event extends GPNode implements Verifiable {

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
        String[] outputVars = Automaton.automaton.getOutputVars();

        for (int i = 0; i < outputVars.length; i++) {
            outputVars[i] = "C." + outputVars[i];
        }


        //TODO: remove this
        Random random = new Random();
        int indx = random.nextInt(outputVars.length);
        return outputVars[indx];
    }
}
