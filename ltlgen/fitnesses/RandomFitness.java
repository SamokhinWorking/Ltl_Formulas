package  ltlgen.fitnesses;


import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import  automaton.ECAlgorithm;
import  automaton.ECStates;
import  automaton.ECTransition;

import  automaton.Automaton;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class RandomFitness extends SingleFitness{

    private static final int SAMPLE_SIZE = 50;
    private static final Automaton auto = new Automaton("CentralController.xml");


    public static String[] genNameState(int length, EvolutionState evolutionState) {
        MersenneTwisterFast random = evolutionState.random[(int)Thread.currentThread().getId() - 1];
        int randomValue = random.nextInt(length / 2) + length / 2 + 2;
        String[] result = new String[randomValue];
        for (int i = 0; i < randomValue; i++) {
            result[i] = "state_" + i;
        }
        return  result;
    }

    public static ECTransition genNewBounds(String[] name, String[]condition, int numberOfCondition, EvolutionState evolutionState){
        int numberOfState = name.length;
        String[] source = new String[numberOfCondition];
        String[] destination = new String[numberOfCondition];

        //gen start state and bounds
        source[0] = name[0];
        destination[0] = name[1];

        ArrayList<Integer> arr =new ArrayList<Integer>();
        for (int i = 1; i < numberOfState; i++) {
            arr.add(i);
        }

        int val = numberOfState - 1;

        MersenneTwisterFast random = evolutionState.random[(int)Thread.currentThread().getId() - 1];
        int randomValue;
        int second;

        // all states have transitions
        for (int i = 1; i < numberOfState; i++){
            source[i] = name[i];
            Collections.shuffle(arr);
            while (arr.get(0) == i){
                Collections.shuffle(arr);
            }
            destination[i] = name[arr.get(0)];
        }

        //gen random source
        for (int i = numberOfState; i < numberOfCondition; i++){
            randomValue = random.nextInt(val) + 1;
            source[i] = name[randomValue];
            second = random.nextInt(val) + 1;
            while (second != randomValue) {
                randomValue = random.nextInt(val) + 1;
            }
            destination[i] = name[second];
        }

        ECTransition result = new ECTransition(source, destination, condition);
        return result;
    }

    public static String[] takeConditions(String[] setOfCondition, int length, EvolutionState evolutionState) {
        return setOfCondition;
//        int val = setOfCondition.length - (length + 3);
//
//        MersenneTwisterFast random = evolutionState.random[(int)Thread.currentThread().getId() - 1];
//        int randomValue = random.nextInt(val);
//
//        String[] condition = new String[length];
//        condition[0] = "1";
//        for (int i = 1; i < length; i++, randomValue++) {
//            condition[i] = setOfCondition[randomValue];
//        }
//
//        return condition;
    }

    public ECAlgorithm makeAlgorithm(String[] name, String[] outputVars, EvolutionState evolutionState){
        String[] text = new String[name.length];
        Arrays.fill(text, "");

        //init initial state
        for (int i = 0; i < outputVars.length; i++) {
            text[0] += outputVars[i] + ":=FALSE;\n";
        }
        MersenneTwisterFast random = evolutionState.random[(int)Thread.currentThread().getId() - 1];
        int randomValue;

        for (int i = 1; i < name.length; i++){
            for (int j = 0; j < outputVars.length; j++){
                randomValue = random.nextInt(3);

                if (randomValue == 0) {
                    text[i] += outputVars[j] + ":=FALSE;\n";
                } else if (randomValue == 1){
                    text[i] += outputVars[j] + ":=TRUE;\n";
                }
            }
            text[i] += "REQ := FALSE;";
        }
        ECAlgorithm algorithm = new ECAlgorithm(name, text);
        return algorithm;
    }

    public double getResult(String formula, Automaton auto, EvolutionState evolutionState) {
        double result = 0;

        String[] inputVars = Automaton.automaton.getInputVars();
        String[] outputVars = Automaton.automaton.getOutputVars();
        String[] nameState = genNameState(ecStateLength, evolutionState);

        //why this?
        int numberOfCondition = nameState.length;// + nameState.length / 2;
        String[] condition = Automaton.automaton.getECTransitionDestination();///takeConditions(setOfCondition, numberOfCondition, evolutionState);

        ECTransition ecTransition = null;//auto.getECTransition();//genNewBounds(nameState, condition, numberOfCondition, evolutionState);
        ECAlgorithm algorithm = makeAlgorithm(nameState, outputVars, evolutionState);
        ECStates ecState = new ECStates(nameState, nameState);

        //Verify LTL Formula
//        Verifier ver = new Verifier(new SmvModel(ecState, ecTransition, algorithm, condition, inputVars, outputVars));

        //TODO

        auto.setAlgorithm(algorithm);
        auto.setECState(nameState,nameState);


        return result;
    }


    @Override
    public double getFitness(String formula, int complexity, EvolutionState evolutionState) {
        double result = 0.0;
       
        
        for (int i = 0; i < SAMPLE_SIZE i++) {
            result += getResult(formula, auto, evolutionState);
        }
        return 1 - result / SAMPLE_SIZE;
    }


}
