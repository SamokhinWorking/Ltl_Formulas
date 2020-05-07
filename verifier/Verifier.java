package  verifier;
import  automaton.Automaton;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Verifier {
    private String smvModel;

    public Verifier(Automaton automaton) {
        this.smvModel = automaton.toSMV();
    }

    public Verifier(String filename) {
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        while (in.hasNext()) {
            sb.append(in.nextLine()).append("\n");
        }
        smvModel = sb.toString();
        in.close();
    }

    public int verify(String s) {
        int verificationResult = 0;
        try {
            File modelFile = File.createTempFile("smvModel", "");
            PrintWriter writer = new PrintWriter(modelFile);
            writer.write(smvModel + "\nLTLSPEC " + s);
            writer.close();
            Process p = Runtime.getRuntime().exec("resources/NuSMV " + modelFile.getPath());

            Scanner sc = new Scanner(new BufferedInputStream(p.getInputStream()));

            ArrayList<String> str = new ArrayList<String>();
            while (sc.hasNext()){
                String line = sc.nextLine();
                str.add(line);
            }
            sc.close();

            String[] lines = str.toArray(new String[str.size()]);

            // print another message
           if (lines[lines.length - 1].isEmpty()){
               verificationResult = 0;
           } else {
               String result = "";
               String target = "-- specification";

               for (int i = 0; i < lines.length; i++) {
                   if (lines[i].contains(target)) {
                       result = lines[i];
                       break;
                   }
               }
               if (result.contains("false")) {
                   verificationResult = 0;
               } else if (result.contains("true")) {
                   verificationResult = 1;
               }
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return verificationResult;
    }
}
