package main;


import automat.Automat;
import helpClass.Algorithm;
import helpClass.ECState;
import helpClass.ECTransition;
import smv_model.SmvModel;

import java.io.File;

public class TestSmvBuild {
    public static void main(String []args){

        Automat automat=new Automat("CentralController.xml");

        ECState ecState = automat.getEcState();
        ECTransition ecTransition = automat.getEcTransition();
        Algorithm algorithm = automat.getAlgorithm();
        String[] inputVars=automat.getInputVars();
        String [] outputVars =automat.getOutputVars();
        String [] condition =ecTransition.Condition;

        /*
        for(int i=0;i<condition.length;i++){
            System.out.println(condition[i]);
        }

         */

        for(int i=0;i<outputVars.length;i++){
            System.out.println(outputVars[i]);
        }


        //create smv file

        File file1 = new File("src/smv_model/Controller.smv");
        SmvModel smv = new SmvModel(file1, "src/smv_model/Controller.smv");

        smv.clearFile();
        smv.buildSmvModel(ecState, ecTransition, algorithm, condition,inputVars,outputVars);



    }
}
