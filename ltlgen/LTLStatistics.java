package ltlgen;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.app.push.Print;
import ec.gp.GPIndividual;
import ec.gp.GPInitializer;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import automaton.Automaton;
import ltlgen.formulas.Verifiable;
import verifier.Verifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class LTLStatistics extends Statistics {


    private int logFile, resultFile, humansFile;
    private int bestNumber;
    private long startTime;
    private Parameter base;
    private static final int SAMPLE_SIZE = 1000;
    private Automaton[] mutants;
    private static final Automaton auto = new Automaton("CentralController.xml");

    private static final String[] referenceSpecification = new String[]{
            "G(!(C.c1Extend & C.c1Retract))",
            "G(!(C.c2Extend & C.c2Retract))",
            "G(!(C.vacuum_on & C.vacuum_off))",
            "G(!P.vcHome & !P.vcEnd -> P.c1Home | P.c1End)",
            "G(!P.c1Home & !P.c1End -> P.vcHome | P.vcEnd)",
            "G(P.c1Home & P.c2Home & P.vcHome & !pp1 & !lifted -> X(!C.c1Extend & !C.c2Extend & !C.vcExtend))",
            "G(pp1 -> F(C.vp1))",
            "G(!(!P.c1Home & !P.c1End & !P.vcHome & !P.vcEnd))",
            "G(lifted -> F(dropped))"
    };

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
        this.base = base;

        MersenneTwisterFast random = state.random[(int)Thread.currentThread().getId() - 1];
        mutants = new Automaton[SAMPLE_SIZE];

        Set<String> uniqueMutants = new HashSet<>();
        uniqueMutants.add(auto.toSMV());
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            mutants[i] = new Automaton(auto);
            while (uniqueMutants.contains(mutants[i].toSMV())) {
                mutants[i].mutate(random);
            }
            uniqueMutants.add(mutants[i].toSMV());
        }
    }

    private String individualToString(boolean forHumans, GPIndividual individual) {
        String s;
        if (forHumans) {
            s = "G(" + individual.trees[0].child.toStringForHumans() + ")\n";
        } else {
            s = "G(" + ((Verifiable) individual.trees[0].child).toStringForVerifier() + ")\n";
        }
        return s + individual.fitness.fitnessToStringForHumans();
    }

    @Override
    public void postEvaluationStatistics(final EvolutionState state) {
        super.postEvaluationStatistics(state);
        state.output.println("Generation " + state.generation, logFile);

        Individual[] individuals = state.population.subpops.get(0).individuals.toArray(new Individual[0]);

        Individual best = individuals[0];

        for (int i = 1; i < individuals.length; i++) {
            if (individuals[i].fitness.betterThan(best.fitness)) {
                best = individuals[i];
            }
        }

        state.output.println("Best individual: "  + individualToString(false, (GPIndividual) best), logFile);
        state.output.println("--------------------", logFile);

        ArrayList<Individual> front = new ArrayList<>();
        MultiObjectiveFitness.partitionIntoParetoFront(state.population.subpops.get(0).individuals, front, null);
        printUniqueIndividuals(state, front.toArray(new Individual[0]), "front");
        printUniqueIndividuals(state, individuals, "all");

        //calculate the mutants metric
//        calculateMutantsMetric(state);
    }

    private void printUniqueIndividuals(EvolutionState state, Individual[] individuals, String filePrefix) {
        Set<String> set = new HashSet<>();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new File(filePrefix + "-" + state.generation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int i = 0;
        for (Individual individual : individuals) {
            GPIndividual gpIndividual = (GPIndividual)individual;
            if (!set.contains(gpIndividual.trees[0].child.toStringForHumans())) {
                out.println(i++ + ": " + individualToString(true, gpIndividual));
                set.add(gpIndividual.trees[0].child.toStringForHumans());
            }
        }

        out.close();
    }


    @Override
    public void finalStatistics(EvolutionState state, int result) {
        printFinal(state, result, false, resultFile);
        printFinal(state, result, true, humansFile);

        //calculate the mutants metric
        calculateMutantsMetric(state);
    }

    private void calculateMutantsMetric(EvolutionState state) {
        double referenceSpecificationUnsatisfiedRatio = 0;
        for (Automaton a : mutants) {
            Verifier verifier = new Verifier(a);
            for (String ltl : referenceSpecification) {
                if (verifier.verify(ltl).verified == 0) {
                    referenceSpecificationUnsatisfiedRatio++;
                    break;
                }
            }
        }

        referenceSpecificationUnsatisfiedRatio = referenceSpecificationUnsatisfiedRatio / SAMPLE_SIZE;
        System.out.println("Reference ratio = " + referenceSpecificationUnsatisfiedRatio);

        List<Individual> generatedSpecification = new ArrayList<>();
        for (Individual i : state.population.subpops.get(0).individuals) {
            if (((MultiObjectiveFitness)i.fitness).getObjective(1) > 0.99999) {
                generatedSpecification.add(i);
            }
        }

        System.out.println("generated " + generatedSpecification.size() + " satisfiable formulas");

        double generatedSpecificationUnsatisfiedRatio = 0;
        for (Automaton a : mutants) {
            Verifier verifier = new Verifier(a);
            for (Individual individual : generatedSpecification) {
                String ltl = "G(" + ((GPIndividual)individual).trees[0].child.toStringForHumans() + ")";
                if (verifier.verify(ltl).verified == 0) {
                    generatedSpecificationUnsatisfiedRatio++;
                    break;
                }
            }
        }

        generatedSpecificationUnsatisfiedRatio = generatedSpecificationUnsatisfiedRatio / SAMPLE_SIZE;

        double mutantsMetric = generatedSpecificationUnsatisfiedRatio / referenceSpecificationUnsatisfiedRatio;
        System.out.println("Mutants metric = " + mutantsMetric);
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
            String s = individualToString(forHumans, (GPIndividual) individual);
            if (!s.equals("") && !shown.contains(s)) {
                state.output.println(s, fileId);
                state.output.println("--------------------", fileId);
                shown.add(s);
            }
        }
        state.output.println("\nBest individuals:", fileId);
        List<Individual> individuals = new ArrayList<>();
        Collections.addAll(individuals, state.population.subpops.get(0).individuals.toArray(new Individual[0]));
        Collections.sort(individuals, new IndividualsComparator());
        shown.clear();
        int count = 0;
        for (Individual individual : individuals) {
            if (count >= bestNumber) {
                break;
            }
            String s = individualToString(forHumans, (GPIndividual) individual);
            if (!s.equals("") && !shown.contains(s)) {
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
