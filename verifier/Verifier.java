package verifier;
import java.io.*;
import java.util.ArrayList;

public class Verifier {
    File file;

    public Verifier (File file){
        this.file=file;
    }
    public void addNewRow(String row){
        try (FileWriter writer =new FileWriter(this.file,true))
        {
            //write a row in a file
            writer.write(row);
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
       // System.out.println("We add new row");
    }
    public void deleteLastRow(){

        //delete last row at file

        try {
            RandomAccessFile f = new RandomAccessFile(this.file, "rw");
            long length = f.length() - 1;
            byte b;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            } while (b != 10 && length>0);
            if(length==0) {
                f.setLength(length);
            }
            else{
                f.setLength(length+1);
            }
            f.close();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
       // System.out.println("We delite last row");
    }
    public int testLtlFormulas(String path){
        int verifier=0;
        try {

            // print a message

            // create a process and execute notepad.exe

            String [] trying={"/bin/bash",
                    "-c",
                    "/home/vlasam/Downloads/NuSMV-2.6.0-linux64/NuSMV-2.6.0-Linux/bin/NuSMV "+path,
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

           if(row[row.length-1].equals("")){
               verifier=0;
           }
           else {

               String result = new String();
               String target = "-- specification";

               for (int i = 0; i < row.length; i++) {
                   if (row[i].contains(target)) {
                       result = row[i];
                       break;
                   }

               }
              // System.out.println(result);
               if (result.contains("false")) {
                   verifier =0;
                  // System.out.println("false confirm");
               } else if (result.contains("true")) {
                   //System.out.println("true confirm");
                   verifier =1;
                  // System.out.println(result);
               }
           }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return verifier;
    }
}
