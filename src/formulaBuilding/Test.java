package formulaBuilding;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.GeneVectorIndividual;

public class Test extends Problem implements SimpleProblemForm{
    private String expected = "c1HomeANDNOTc1EndAND(c2HomeORc2End)";

    public void evaluate(final EvolutionState evolutionState,
                         final Individual individual,
                         final int subPopulation,
                         final int threadNum) {
        if (individual.evaluated)
            return;

        int fitnessValue = 0;

        GeneVectorIndividual stringVectorIndividual = (GeneVectorIndividual) individual;
        long length = stringVectorIndividual.size();
        for (int i = 0; i < length; i++) {
            StringVectorGene stringVectorGene
                    = (StringVectorGene) stringVectorIndividual.genome[i];
            String actual = stringVectorGene.getAllele() ;
            if (expected.contains(actual)) {
                if(actual.length()==1)
                fitnessValue += 1;
            }else{
                fitnessValue += 7;
            }
        }

        SimpleFitness fitness
                = (SimpleFitness) stringVectorIndividual.fitness;
        /*fitness.setFitness(evolutionState, fitnessValue,
                fitnessValue == stringVectorIndividual.genomeLength());
                */

        fitness.setFitness(evolutionState, fitnessValue,fitnessValue==58);

        stringVectorIndividual.evaluated = true;
    }

}
