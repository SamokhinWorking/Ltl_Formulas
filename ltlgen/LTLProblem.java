package ltlgen;

import automat.Automat;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ltlgen.filters.Filter;
import ltlgen.fitnesses.SingleFitness;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LTLProblem extends GPProblem {
    public static int EVENT_NUMBER;
    public static int ACTION_NUMBER;
    private static Filter[] filters;
    private static SingleFitness[] fitnesses;
    private static final Map<String, EvaluationResult> results = new HashMap<>();

    @Override
    public void setup(final EvolutionState state, Parameter base) {
        super.setup(state, base);
        if (!(input instanceof LTLData)) {
            state.output.fatal("GPData class must subclass from " + LTLData.class, base.push(P_DATA));
        }

       // Parameter a = new Parameter("automaton");
        //EVENT_NUMBER = state.parameters.getInt(a.push("event-number"), null);
        //ACTION_NUMBER = state.parameters.getInt(a.push("action-number"), null);

        Parameter ft = base.push("filters");
        filters = new Filter[state.parameters.getInt(ft.push("number"), null)];
        for (int i = 0; i < filters.length; i++) {
            Class c = state.parameters.getClassForParameter(ft.push(Integer.toString(i)), null, Filter.class);
            try {
                filters[i] = (Filter) c.newInstance();
                filters[i].setup(state, ft.push(Integer.toString(i)));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fitnesses = new SingleFitness[state.parameters.getInt(new Parameter(new String[]{"multi", "fitness", "num-objectives"}), null)];
        Parameter f = base.push("fitness");

        for (int i = 0; i < fitnesses.length; i++) {
            Class c = state.parameters.getClassForParameter(f.push(Integer.toString(i)), null, SingleFitness.class);
            try {
                fitnesses[i] = (SingleFitness) c.newInstance();
                fitnesses[i].setup(state, f.push(Integer.toString(i)));
            } catch (InstantiationException | IllegalAccessException e) {
                state.output.fatal("Error while loading fitness function: " + e.getMessage());
            }
        }
    }

    private double[]  getFitness(String formula, int size, Automat automat, String[] setOfCondition) {
        if (results.containsKey(formula)) {
            return results.get(formula).result;
        }
       // int result=0; // = new double[fitnesses.length];
        double[] result = new double[fitnesses.length];

        boolean r = true;
        for (Filter filter : filters) {
            if (!filter.accepts(formula, size)) {
                r = false;
                break;
            }
        }
        if (r) {
            for (int i = 0; i < result.length; i++) {
                result[i] = fitnesses[i].getFitness(formula, size,automat,setOfCondition);
                if (result[i] == -1) {
                    for (int j = 0; j <= i; j++) {
                        result[j] = 0;
                    }
                    break;
                }
            }
        }
        results.put(formula, new EvaluationResult(result));
        return result;
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated) {
            LTLData input = (LTLData) this.input;
            GPIndividual individual = (GPIndividual) ind;
            individual.trees[0].child.eval(state, threadnum, input, stack, individual, this);

            //change my file add automat to function getFitness()
            Automat automat = new Automat("CentralController.xml");
            String [] setOfCondition=TakeConditions("src/conditions/result-humans.stat");

            /*
            KozaFitness f =(KozaFitness) ind.fitness;

            String formula = "G(" + input.result + ")";


            f.setStandardizedFitness(state, getFitness(formula, input.complexity));
            ind.evaluated = true;

            */

            MultiObjectiveFitness f = (MultiObjectiveFitness) ind.fitness;
            String formula = "G(" + input.result + ")";
            //f.set
            f.setObjectives(state, getFitness(formula, input.complexity,automat,setOfCondition));
            ind.evaluated = true;

        }
    }


    public static String [] TakeConditions(String name){
        ArrayList<String> lines = new ArrayList<String>();
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;
            String target1="Fitness:";
            String target2="Strength:";
            String target3="Distance:";
            while((line=reader.readLine())!= null){

                if(!line.contains(target1) &&  !line.contains(target2) && !line.contains(target3)) {
                    lines.add(line);
                }
            }
            reader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String [] allCondition = lines.toArray(new String[lines.size()]);



        return allCondition;
    }

    private static class EvaluationResult {
        private final double[] result;

        public EvaluationResult(double[] result) {
            this.result = result;
        }
    }

}