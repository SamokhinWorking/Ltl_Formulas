package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class TestSmthNew {
    public static void main(String[] args) {
        try {

            // print a message
            System.out.println("Executing cmd");

            // create a process and execute notepad.exe

            String [] trying={"/bin/bash",
                    "-c",
                    "/home/biscuit/Downloads/NuSMV-2.6.0-linux64/NuSMV-2.6.0-Linux/bin/NuSMV C.smv",
            };
            Process process = Runtime.getRuntime().exec(trying);

            BufferedReader input =new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error =new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            ArrayList<String> str =new ArrayList<String>();
            while((line=input.readLine())!=null){
              //  System.out.println(line);
                str.add(line);
            }
            input.close();
            String [] row =str.toArray(new String[str.size()]);
            while((line=error.readLine())!=null){
                System.out.println(line);
            }

                // print another message
            System.out.println("cmd should now open.");
          //  System.out.println(row[row.length-1]);

            String result=new String();
            String target ="-- specification";
            for(int i=0; i<row.length;i++){
                if(row[i].contains(target)){
                    result=row[i];
                    break;
                }
            }
            System.out.println(result);
            if(result.contains("false")){
                System.out.println("false confirm");
            }
            else if(result.contains("true")){
                System.out.println("true confirm");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
