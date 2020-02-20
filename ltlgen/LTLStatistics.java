package ltlgen;

import ec.EvolutionState;
import ec.Subpopulation;
import ec.Individual;
import ec.Statistics;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ltlgen.formulas.Verifiable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LTLStatistics extends Statistics {


    private int logFile, resultFile, humansFile;
    private int bestNumber;
    private long startTime;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        File log = state.parameters.getFile(base.push("log"), null);
        File result = state.parameters.getFile(base.push("result"), null);
        File humans = state.parameters.getFile(base.push("result-humans"), null);
        bestNumber = state.parameters.getInt(base.push("best-number"), null);
        try {
            logFile = state.output.addLog(log, true);
            resultFile = state.output.addLog(result, true);
            humansFile = state.output.addLog(humans, true);
        } catch (IOException e) {
            state.output.fatal("Error while setting up stats file: " + e.getMessage());
        }
        startTime = System.currentTimeMillis();
    }

    private String individualToString(boolean forHumans, GPIndividual individual, Set<String> sh) {
        String s;
        if (forHumans) {
            s = "G(" + individual.trees[0].child.toStringForHumans() + ")\n";
        } else {
            s = "G(" + ((Verifiable) individual.trees[0].child).toStringForVerifier() + ")\n";
        }
        if (sh.contains(s)) {
            return "";
        }
        sh.add(s);
        return s + individual.fitness.fitnessToStringForHumans();
    }

    @Override
    public void postEvaluationStatistics(final EvolutionState state) {
        super.postEvaluationStatistics(state);
        state.output.println("Generation " + state.generation, logFile);

        Individual[] individuals = state.population.subpops.get(0).individuals.toArray(new Individual[0]);

        int idx=0;
        Individual best = individuals[0];

        /*
        state.output.println("First: ==>"+idx+" "  + individualToString(false, (GPIndividual) best, new HashSet<>()), logFile);
        state.output.println("Second: ==>"+idx+" "  + individualToString(false, (GPIndividual) best, new HashSet<>()), logFile);
        state.output.println("Third: ==>"+idx+" "  + individualToString(false, (GPIndividual) best, new HashSet<>()), logFile);
        state.output.println("Fourth: ==>"+idx+" "  + individualToString(false, (GPIndividual) best, new HashSet<>()), logFile);

        state.output.println("All individuals", logFile);


        for(int i=0;i<individuals.length;i++){
            state.output.println(i+"---> "+individualToString(false, (GPIndividual) individuals[i], new HashSet<>()), logFile);
        }

         */



        for (int i = 1; i < individuals.length; i++) {
            if (individuals[i].fitness.betterThan(best.fitness)) {
                //state.output.println(" my index = "+i,logFile);
                best = individuals[i];
               // idx=i;
            }
        }


        state.output.println("Best individual: "  + individualToString(false, (GPIndividual) best, new HashSet<>()), logFile);


        state.output.println("--------------------", logFile);
    }

    @Override
    public void finalStatistics(EvolutionState state, int result) {
        printFinal(state, result, false, resultFile);
        printFinal(state, result, true, humansFile);
    }

    private void printFinal(EvolutionState state, int result, boolean forHumans, int fileId) {
        if (forHumans) {
            state.output.println("Ideal individual " + (result == EvolutionState.R_SUCCESS ? "" : "not ") + "found.\n", fileId);
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            long minutes = seconds / 60, hours = minutes / 60, days = hours / 24;
            String d = days + " days, " + (hours % 24) + " hours, " + (minutes % 60) + " minutes, " + (seconds % 60) + " seconds.";
            state.output.println("Time taken: " + d + "\n", fileId);
        }
        state.output.println("Front:", fileId);
        ArrayList<Individual> front = new ArrayList<>();
        Set<String> shown = new HashSet<>();
        MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops.get(0).individuals, front, null);
        for (Individual individual : front) {
            String s = individualToString(forHumans, (GPIndividual) individual, shown);
            if (!s.equals("")) {
                state.output.println(s, fileId);
                state.output.println("--------------------", fileId);
                shown.add(s);
            }
        }
        state.output.println("\nBest individuals:", fileId);
        List<Individual> individuals = new ArrayList<>();
        // change  state.population.subpops[0].individuals to  state.population.subpops.get(0).individuals.toArray(new Individual[0])


        Collections.addAll(individuals, state.population.subpops.get(0).individuals.toArray(new Individual[0]));
        Collections.sort(individuals, new IndividualsComparator());
        shown.clear();
        int count = 0;
        for (Individual individual : individuals) {
            if (count >= bestNumber) {
                break;
            }
            String s = individualToString(forHumans, (GPIndividual) individual, shown);
            if (!s.equals("")) {
                state.output.println(s, fileId);
                state.output.println("--------------------", fileId);
                shown.add(s);
                count++;
            }
        }
    }

    private static class IndividualsComparator implements Comparator<Individual> {

        @Override
        public int compare(Individual i1, Individual i2) {
            if (i1.fitness.betterThan(i2.fitness)) {
                return -1;
            } else if (i2.fitness.betterThan(i1.fitness)) {
                return 1;
            }
            return 0;
        }
    }


}
