package ltlgen;

import automaton.Automaton;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
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
import java.util.List;
import java.util.Map;

public class LTLProblem extends GPProblem {
    private static Filter[] filters;
    private static SingleFitness[] fitnesses;
    private static final Map<String, EvaluationResult> results = new HashMap<>();
    public static List<String> terminals;
    public static String[] predicates = new String[]{"lifted", "dropped", "C.vp1", "C.vp2", "C.vp3"};
    public static Parameter base;

    @Override
    public void setup(final EvolutionState state, Parameter base) {
        super.setup(state, base);

        if (!(input instanceof LTLData)) {
            state.output.fatal("GPData class must subclass from " + LTLData.class, base.push(P_DATA));
        }

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

        terminals = new ArrayList<>();
        for (String s : Automaton.automaton.getInputVars()) {
            terminals.add(s);
        }

        for (String s : Automaton.automaton.getOutputVars()) {
            terminals.add(s);
        }
        terminals.add("lifted");
        terminals.add("dropped");
        terminals.add("vp1");
        terminals.add("vp2");
        terminals.add("vp3");

        LTLProblem.base = this.defaultBase();
    }

    private double[] getFitness(Individual individual, String formula, int size, EvolutionState state) {
        if (results.containsKey(formula)) {
            return results.get(formula).result;
        }

        double[] result = new double[fitnesses.length];

        for (Filter filter : filters) {
            if (!filter.accepts(formula, size)) {
                results.put(formula, new EvaluationResult(result));
                return result;
            }
        }

        for (int i = 0; i < result.length; i++) {
            result[i] = fitnesses[i].getFitness(formula, size, state);
            if (result[i] == -1) {
                for (int j = 0; j <= i; j++) {
                    result[j] = 0;
                }
                break;
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
            MultiObjectiveFitness f = (MultiObjectiveFitness) ind.fitness;
            String formula = "G(" + input.result + ")";
            f.setObjectives(state, getFitness(individual, formula, input.complexity, state));
            ind.evaluated = true;
        }
    }

    private static class EvaluationResult {
        private final double[] result;
        public EvaluationResult(double[] result) {
            this.result = result;
        }
    }

}