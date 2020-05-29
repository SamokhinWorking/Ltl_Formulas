package verifier;
import automaton.Automaton;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Verifier {
    private String smvModel;

    public Verifier(Automaton automaton) {
        this.smvModel = automaton.toSMV();
    }

    public class VerificationResult {
        public int verified;
        public int counterExampleLength;

        public VerificationResult(int verified) {
            this.verified = verified;
            this.counterExampleLength = 0;
        }

        public VerificationResult(int verified, int counterExampleLength) {
            this.verified = verified;
            this.counterExampleLength = counterExampleLength;
        }

        public double getUnsatisfiedFormulaFunction() {
            return 1.0 - 1.0 / Math.pow(1 + 0.5 * counterExampleLength, 0.5);
        }
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

    public VerificationResult verify(String s) {
        int verificationResult = 0;
        int nStates = 0;
        try {
            File modelFile = File.createTempFile("smvModel", "");
//            File modelFile = new File("smvModel");
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


           if (lines[lines.length - 1].isEmpty()){
               verificationResult = 0;
           } else {
               String result = "";
               String target = "-- specification";

               for (int i = 0; i < lines.length; i++) {
                   if (lines[i].contains(target)) {
                       result = lines[i];
                       if (result.contains("false")) {
                           verificationResult = 0;
                       } else if (result.contains("true")) {
                           verificationResult = 1;
                       }
                   }
                   if (lines[i].contains("State: ")) {
                       nStates++;
                   }
               }
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (verificationResult == 1) {
            return new VerificationResult(verificationResult);
        }

        if (verificationResult == 0 && nStates == 0) {
            throw new RuntimeException();
        }

//        return new VerificationResult(0, 1 - 1.0 / Math.pow(1 + 0.1 * nStates, 0.1);
        return new VerificationResult(0, nStates);
    }
}
