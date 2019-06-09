package ltlgen.fitnesses;

import helpClass.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parse.parseInfo;
import smv_model.SmvModel;
import tree.BooleanExpressionTree;
import verifier.Verifier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MutatedFitness extends SingleFitness{

    public double getResult(String formula){
        double result=0.0;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("CentralController.xml");

            ECState ecState = parseInfo.parseECState(document);
            ECTransition ecTransition = parseInfo.parseECTransition(document);
            Algorithm algorithm = parseInfo.parseAlgorithm(document);

            ArrayList<BooleanExpressionTree> trees =new ArrayList<BooleanExpressionTree>();
            for(int i=0;i<ecTransition.Condition.length;i++){
                if(!ecTransition.Condition[i].equals("INIT") && !ecTransition.Source[i].equals("START")) {
                    BooleanExpressionTree tree = new BooleanExpressionTree();
                    tree.constructTree(ecTransition.Condition[i]);
                    trees.add(tree);
                }
                else if (ecTransition.Source[i].equals("START")){
                    BooleanExpressionTree tree = new BooleanExpressionTree();
                    tree.constructTree(ecTransition.Condition[i]);
                    trees.add(tree);
                }

            }
            BooleanExpressionTree[] someTree = trees.toArray(new BooleanExpressionTree[trees.size()]);
            String[] condition = new String[someTree.length];


            int val=condition.length-2;
            int stateVal =ecState.Name.length-2;


                Random randomNumber = new Random();

                int first =randomNumber.nextInt(val);
                int second= randomNumber.nextInt(val);
                int valForEcState = randomNumber.nextInt(stateVal);


                if (first == second)
                {
                    while (first==second)
                    {
                        second=randomNumber.nextInt(val);
                    }
                }

                if(ecTransition.Destination[first+2].equals(ecState.Name[valForEcState+2]))
                {
                    while (ecTransition.Destination[first+2].equals(ecState.Name[valForEcState+2]))
                    {
                        valForEcState = randomNumber.nextInt(stateVal);
                    }
                }


                someTree[first+2].changeTree(someTree[second+2]);
                ecTransition.Destination[first+2]=ecState.Name[valForEcState+2];

                //String controlS= someTree[first+2].makeString();
                // System.out.println(controlS);
                //System.out.println();

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

        }
        catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }


        return result;
    }
    @Override
    public double getFitness(String formula, int complexity) {

        double result=0.0;
        
        for(int i=0;i<50;i++)
        {
            result+=getResult(formula);
        }
        return result;
    }
}
