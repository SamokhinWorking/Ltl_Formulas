package main;

import verifier.Verifier;

import java.io.*;

public class TestChangeFile {
    public static void main(String[] args) {
        System.out.println("Take my energy boy");
        String row1 = "LTLSPEC ((P.c2End & F(P.c1Home)) P.vcHome (F(P.vcEnd) pp2))";
        String row2= "LTLSPEC G(!(!P.c1Home & !P.c1End & !P.vcHome & !P.vcEnd))";
        File file = new File("C.smv");
        Verifier ver =new Verifier(file);
        //ver.addNewRow(row2);
       // ver.testLtlFormulas();
       // ver.deleteLastRow();


    }


}
