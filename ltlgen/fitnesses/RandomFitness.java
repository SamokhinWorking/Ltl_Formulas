package ltlgen.fitnesses;

import ec.EvolutionState;
import ec.util.Parameter;



import helpClass.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parse.parseInfo;

import smv_model.SmvModel;
import tree.BooleanExpressionTree;
import verifier.Verifier;
import automat.Automat;


import ltlgenCondition.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class RandomFitness extends SingleFitness{


    public static String [] genNameState (int length){

        String row="state_";

        Random randomNumber = new Random();
        int randomValue;
        randomValue=randomNumber.nextInt(length/2)+length/2+2;

        String [] result =new String[randomValue];
        for(int i=0; i<randomValue;i++)
        {
            result[i]=row+(i);
        }

        return  result;
    }



    public static ECTransition genNewBounds(String [] name,String[]condition,int numberOfCondition){


       // System.out.println("first");

        int numberOfState =name.length;



        String [ ] source = new String[numberOfCondition];
        String [] destination =new String[numberOfCondition];




        //gen start state and bounds
        source[0]=name[0];
        destination[0]=name[1];

       // System.out.println("one by one");


        ArrayList <Integer>  arr =new ArrayList<Integer>();

        for (int i=1;i<numberOfState;i++){
            arr.add(i);
        }
       // System.out.println("gen number ofState");

        int val=numberOfState-1;


        Random randomNumber = new Random();

        int randomValue;
        int second;

        // all states have transitions
       // System.out.println("number of state= "+numberOfState);
        for(int i=1; i<numberOfState;i++){
            source[i]=name[i];

           // System.out.println("i= "+i +"    arr= "+arr.get(0));

            Collections.shuffle(arr);
         //   System.out.println("i= "+i +"    arr= "+arr.get(0));

            while (arr.get(0) ==i){
                Collections.shuffle(arr);
            }
            destination[i]=name[arr.get(0)];
           // arr.remove(0);
        }
       // System.out.println("all states have transitions");

        //gen random source.

        for(int i=numberOfState;i<numberOfCondition;i++){

            randomValue=randomNumber.nextInt(val)+1;
            source[i]=name[randomValue];
            second=randomNumber.nextInt(val)+1;
            while(second!=randomValue)
            {
                randomValue=randomNumber.nextInt(val)+1;
            }

            destination[i]=name[second];
        }

        ECTransition result=new ECTransition(source,destination,condition);


        return result;
    }
/*
    public static String [] TakeConditions(String name,int length){
        ArrayList <String> lines = new ArrayList<String>();
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;
            String target1="Fitness:";
            String target2="Strength:";
            String target3="Distance:";
            while((line=reader.readLine())!= null){

                if(!line.contains(target1) &&  !line.contains(target2) && !line.contains(target3)) {
                    lines.add(line);
                }
            }
            reader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String [] allCondition = lines.toArray(new String[lines.size()]);

        int val=allCondition.length-(length+1);

        Random randomNumber = new Random();


        int randomValue=randomNumber.nextInt(val);

        String []condition=new String[length];
        condition[0]="1";
        for(int i=1;i<length;i++,randomValue++){
            condition[i]=allCondition[randomValue];
        }


        return condition;
    }

 */

    public static String [] TakeConditions(String []setOfCondition,int length){

        int val=setOfCondition.length-(length+3);

        Random randomNumber = new Random();


        int randomValue=randomNumber.nextInt(val);

        String []condition=new String[length];
        condition[0]="1";
        for(int i=1;i<length;i++,randomValue++){
            condition[i]=setOfCondition[randomValue];
        }


        return condition;
    }

    public  Algorithm makeAlgorithm (String []name, String [] outputVars){
        String []text =new String[name.length];
        for(int i=0;i<text.length;i++){
            text[i]="";
        }
        //init initial state
        for(int i=0; i<outputVars.length;i++){
            text[0]+=outputVars[i]+":=FALSE;\n";
        }
        Random randomNumber = new Random();
        int randomValue;


        for(int i=1; i<name.length;i++){
            for(int j=0;j<outputVars.length;j++){
                randomValue=randomNumber.nextInt(3);

                if(randomValue==0){
                    text[i]+=outputVars[j]+":=FALSE;\n";
                }
                else if(randomValue==1){
                    text[i]+=outputVars[j]+":=TRUE;\n";
                }
            }
            text[i]+="REQ:=FALSE;";
        }
        Algorithm algorithm=new Algorithm(name,text);
        return algorithm;
    }

    void printCondition(String [] a){
        for(int i=0; i<a.length;i++)
        {
            System.out.print(a[i]+" ");
        }
        System.out.println("-----------------");
    }




    public static void printStr(String[]str){
        System.out.println("Length of string == "+str.length);
        for (int i=0;i<str.length;i++){
            System.out.println(str[i]);
        }
    }

    public double getResult(String formula,Automat auto, int ecStateLength,String []setOfCondition){

        //System.out.println("First");


        double result=0.0;



        String [] outputVars =auto.getOutputVars();
        String [] inputVars=auto.getInputVars();

       // System.out.println("Get out and in");



        String[] nameState =genNameState(ecStateLength);

      //  System.out.println("GenName");

        int numberOfCondition=nameState.length+nameState.length/2;


        String [] condition=TakeConditions(setOfCondition,numberOfCondition);

       // System.out.println("TakeCondition");

       // printStr(nameState);


        ECTransition ecTransition =genNewBounds(nameState,condition,numberOfCondition);

      //  System.out.println("Gen ecTransition");

        /*
        ecTransition.printECTransition();
        System.out.println("-------------------------------------------------");
         */

        Algorithm algorithm= makeAlgorithm(nameState,outputVars);


      //  System.out.println("makeAlgorithm");

        ECState ecState =new ECState(nameState,nameState);


        //create smv file

        File file1 = new File("src/smv_model/Controller.smv");
        SmvModel smv = new SmvModel(file1, "src/smv_model/Controller.smv");


        smv.clearFile();
        smv.buildSmvModel(ecState, ecTransition, algorithm, condition,inputVars,outputVars);


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

       // System.out.println("I verify my formula and answer == "+test);

        //System.out.println("I was there");

        return result;
    }


    @Override
    public double getFitness(String formula, int complexity,Automat automat,String []setOfCondition) {

        double result=0.0;
        Automat auto = new Automat("CentralController.xml");
        int ecStateLength=auto.getEcState().Name.length;

        for(int i=0;i<50;i++)
        {
            result+=getResult(formula,automat,ecStateLength,setOfCondition);
        }
       // System.out.println("Result of RandomFitness == "+result);
        return 1.0-result;

    }


}
