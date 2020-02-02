package main;

import automat.Automat;
import helpClass.Algorithm;
import helpClass.ECState;
import helpClass.ECTransition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.*;

public class TestNewGeneration {


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

    public static ECTransition genNewBounds(String [] name,String[]condition){




        int numberOfState =name.length;

        int numberOfCondition= numberOfState+numberOfState/2;


        String [ ] source = new String[numberOfCondition];
        String [] destination =new String[numberOfCondition];




        //gen start state and bounds
        source[0]=name[0];
        destination[0]=name[1];




        ArrayList <Integer>  arr =new ArrayList<Integer>();

        for (int i=1;i<numberOfState;i++){
            arr.add(i);
        }

        int val=numberOfState-1;


        Random randomNumber = new Random();

        int randomValue;
        int second;

        // all states have transitions

        for(int i=1; i<numberOfState;i++){
            source[i]=name[i];

            Collections.shuffle(arr);
            while (arr.get(0) ==i){
                Collections.shuffle(arr);
            }
            destination[i]=name[arr.get(0)];
            arr.remove(0);
        }

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

        int val=allCondition.length-(length-1);

        Random randomNumber = new Random();


        int randomValue=randomNumber.nextInt(val);

        String []condition=new String[length];
        condition[0]="1";
        for(int i=1;i<length;i++,randomValue++){
            condition[i]=allCondition[randomValue];
        }


        return condition;
    }

    public static void printStr(String[]str){
        System.out.println("Length of string == "+str.length);
        for (int i=0;i<str.length;i++){
            System.out.println(str[i]);
        }
    }

    public static Algorithm makeAlgorithm (String []name, String [] outputVars){
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
    public static void main(String []args){
        Automat automat = new Automat("CentralController.xml");
        ECState ecStateFirst =automat.getEcState();
        String [] OutputVars =automat.getOutputVars();

        int stateLength=ecStateFirst.Name.length;

        String[] nameState =genNameState(stateLength);

        int numberOfCondition=nameState.length+nameState.length/2;


        String [] condition=TakeConditions("src/conditions/result-humans.stat",numberOfCondition);



        printStr(nameState);



       ECTransition ecTransition=genNewBounds(nameState,condition);

        Algorithm algorithm= makeAlgorithm(nameState,OutputVars);
        algorithm.printAlgorithm();

        ecTransition.printECTransition();




    //    ecTransition.printECTransition();


    }

}
