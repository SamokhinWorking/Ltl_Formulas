package verifier;
import java.io.*;
import java.util.ArrayList;

public class Verifier {
    String path;
    File file;

    public Verifier (String path)
    {
        this.path=path;
        this.file=new File(path);
    }


    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public int testLtlFormulas(String s){
        int verifier=0;
        try {

            // print a message

            // create a process and execute notepad.exe
            String NewPath="verifier/tmp.smv";
            File fileNew = new File(NewPath);

            copyFileUsingStream(this.file,fileNew);

            FileWriter writer =new FileWriter(fileNew,true);
            writer.write(s);
            writer.flush();


            String [] trying={"/bin/bash",
                    "-c",
                    "NuSMV/bin/NuSMV "+NewPath,
            };
            Process process = Runtime.getRuntime().exec(trying);

            BufferedReader input =new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error =new BufferedReader(new InputStreamReader(process.getErrorStream()));




            String line;
            ArrayList<String> str =new ArrayList<String>();
            while((line=input.readLine())!=null){
            //     System.out.println(line);
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
           //    System.out.println(result);
               if (result.contains("false")) {
                   verifier =0;
                   //System.out.println("false confirm");
               } else if (result.contains("true")) {
                 //  System.out.println("true confirm");
                   verifier =1;
                  //System.out.println(result);
               }
           }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return verifier;
    }
}
