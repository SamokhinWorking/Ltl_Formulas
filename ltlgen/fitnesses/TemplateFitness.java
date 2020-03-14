package ltlgen.fitnesses;

import automat.Automat;
import verifier.Verifier;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateFitness extends SingleFitness{

    public static double chekTemplate(String row){
        double res=0.0;

        String reg ="G\\(\\(" +
                "([PC]*\\.*[A-z]*[0-9]*[A-z]*)" +
                " -> F" +
                "\\(([PC]*\\.*[A-z]*[0-9]*[A-z]*)\\)\\)\\)";

        // reg template G((pred -> F(pred))

        Pattern pattern = Pattern.compile(reg);
        Matcher match = pattern.matcher(row);
        int matchCounter = 0;
        String result="";

      //  System.out.println(row);
        while (match.find()) {
            matchCounter++;
            result=row.substring(match.start(),match.end());
        //    System.out.println(result);
        }
        if(matchCounter==1){
            if(result.equals(row)){
                return 1.0;
            }
        }

        return res;
    }



    @Override
    public double getFitness(String formula, int complexity, Automat automat,String []setOfCondition) {


        double res=chekTemplate(formula);
        if(res==0){
            return 0.0;
        }

        return res;
    }

}
