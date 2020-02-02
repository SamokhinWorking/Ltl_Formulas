package main;

import java.io.*;
import java.util.ArrayList;


public class TestTakeCondition {


    public static String [] TakeConditions(String name){
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

        String []condition = lines.toArray(new String[lines.size()]);
       return condition;
    }

    public static void main(String[] args) {

        String [] condition=TakeConditions("src/conditions/result-humans.stat");

       for(int i=0;i<condition.length;i++)
       {
           System.out.println(condition[i]);
       }

    }

}
