package ltlgen.fitnesses;

import helpClass.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parse.parseInfo;
import smv_model.SmvModel;
import tree.BooleanExpressionTree;
import verifier.Verifier;
import automat.Automat;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class MutatedFitness extends SingleFitness{

    public double getResult(String formula,Automat auto){
        double result=0.0;



            ECState ecState = auto.getEcState();
            ECTransition ecTransition = auto.getEcTransition();
            Algorithm algorithm = auto.getAlgorithm();
            String[] inputVars=auto.getInputVars();



            ArrayList<BooleanExpressionTree> trees =new ArrayList<BooleanExpressionTree>();
            for(int i=0;i<ecTransition.Condition.length;i++){

                    BooleanExpressionTree tree = new BooleanExpressionTree();
                    tree.constructTree(ecTransition.Condition[i]);
                    trees.add(tree);


            }
            BooleanExpressionTree[] someTree = trees.toArray(new BooleanExpressionTree[trees.size()]);
            String[] condition = new String[someTree.length];


            int val=condition.length-1;
            int stateVal =ecState.Name.length-1;
            int inVal=inputVars.length-1;



                Random randomNumber = new Random();

                int conditionRandom =randomNumber.nextInt(val)+1;
                int inValRandom = randomNumber.nextInt(inVal)+1;
                int valForEcState = randomNumber.nextInt(stateVal)+1;


                // check that destination != new random destination

                if(ecTransition.Destination[conditionRandom].equals(ecState.Name[valForEcState]))
                {
                    while (ecTransition.Destination[conditionRandom].equals(ecState.Name[valForEcState]))
                    {
                        valForEcState = randomNumber.nextInt(valForEcState);
                    }
                }

                // change destination + condition
                someTree[conditionRandom].changeTreeOneValue(inputVars[inValRandom]); // меняю дерево
                ecTransition.Destination[conditionRandom]=ecState.Name[valForEcState]; // меняю путь



                for (int i = 0; i < someTree.length; i++) {
                    condition[i] = someTree[i].makeString();
                }


                //create smv file

                File file1 = new File("src/smv_model/Controller.smv");
                SmvModel smv = new SmvModel(file1, "src/smv_model/Controller.smv");

                smv.clearFile();
                smv.buildSmvModel(ecState, ecTransition, algorithm, condition);


                //Verifier LTL Formulas

                String row = new String();
                row = "\nLTLSPEC " + formula;
                File file = new File("src/smv_model/Controller.smv");
                Verifier ver = new Verifier(file);
                ver.addNewRow(row);

                int test;
                test = ver.testLtlFormulas("src/smv_model/Controller.smv");
                ver.deleteLastRow();

                if(test==1)
                {
                    result+=0.0175;
                }



        return result;
    }
    @Override
    public double getFitness(String formula, int complexity) {

        double result=0.0;
        Automat auto = new Automat("CentralController.xml");
        for(int i=0;i<50;i++)
        {
            result+=getResult(formula,auto);
        }
        return result;
    }
}
