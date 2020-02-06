package ltlgen.fitnesses;

import automat.Automat;
import verifier.Verifier;

import java.io.File;

public class ComplexityFitness extends SingleFitness {
    @Override
    public double getFitness(String formula, int complexity, Automat automat,String []setOfCondition) {
        double result;
    //    if (complexity>2)
        //{
            result= 1 / (1.0 + complexity);
            return result < threshold ? -1 : result;
      //  }
     //   else
     //       return -1;
    }
}
