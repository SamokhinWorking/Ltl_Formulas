package ltlgen.fitnesses;

import verifier.Verifier;

import java.io.File;
public class CorrectnessFitness extends SingleFitness{

    @Override
    public double getFitness(String formula, int complexity) {

        String row =new String();
        row="LTLSPEC "+formula;
        File file = new File("C.smv");
        Verifier ver =new Verifier(file);
        ver.addNewRow(row);

        int test;
        test= ver.testLtlFormulas("C.smv");
        ver.deleteLastRow();

        if((test == 1) ){
            return threshold;
        }
        else{
            return -1;
        }

    }

}
