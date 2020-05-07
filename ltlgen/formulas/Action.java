package  ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import  automaton.Automaton;
import  ltlgen.LTLData;


import java.util.Random;

public class Action extends GPNode implements Verifiable {

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
        String[] inputVars = Automaton.automaton.getInputVars();
        for (int i = 0; i < inputVars.length; i++) {
            if (inputVars[i].equals("vac")) {
                inputVars[i] = "C." + inputVars[i];
            } else if (!inputVars[i].equals("pp1") && !inputVars[i].equals("pp2") && !inputVars[i].equals("pp3")) {
                inputVars[i] = "P." + inputVars[i];
            }
        }

        Random random = new Random();
        int indx = random.nextInt(inputVars.length);
        return inputVars[indx];
    }

}
