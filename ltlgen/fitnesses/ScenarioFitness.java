package ltlgen.fitnesses;

import automat.Automat;
import verifier.Verifier;

import java.io.File;
public class ScenarioFitness extends SingleFitness{

    @Override
    public double getFitness(String formula, int complexity, Automat automat,String []setOfCondition) {

        String row =new String();
        row="LTLSPEC "+formula;

        Verifier ver =new Verifier("Scenario.smv");


        int test=0;
        test= ver.testLtlFormulas(row);


        if((test == 1) ){
            return -1.0;
        }
        else {
            return 1.0;
        }

    }

}
