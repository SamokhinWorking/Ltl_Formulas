package ltlgen.fitnesses;

import automat.Automat;
import verifier.Verifier;

import java.io.File;
public class CorrectnessFitness extends SingleFitness{

    @Override
    public double getFitness(String formula, int complexity, Automat automat,String []setOfCondition) {

        String row =new String();
        row="LTLSPEC "+formula;
       // File file = new File("C.smv");
        Verifier ver =new Verifier("C.smv");
        //ver.addNewRow(row);
    //    System.out.println(row);

        int test=0;
        test= ver.testLtlFormulas(row);
     //   ver.deleteLastRow();

        if((test == 1) ){
            return 1.0;
        }
        else{
            return -1;
        }

    }

}
