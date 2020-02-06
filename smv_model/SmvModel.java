package smv_model;
import ec.util.Output;
import helpClass.*;
import parse.parseInfo;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import tree.BooleanExpressionTree;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;

public class SmvModel {
    File file;
    String path;
    public SmvModel (File file,String path){
        this.file=file;
        this.path=path;
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

    }
    public String outputVarsDiscription(Algorithm algorithm,ECState ecState, String outputVar) {
        //init(c1Extend) := FALSE;
        String rowLine="init("+outputVar+") := FALSE;\n\nnext("+outputVar+") := case\n";
        //smv.addNewRow(rowLine);


        String rowT="\tFALSE | next(_state) = ";
        String rowF="\tFALSE | next(_state) = ";

        //row for TRUE / False
        for (int i=0; i<algorithm.Text.length;i++)
        {
            String rowTrue=outputVar+":=TRUE";
            String rowFalse=outputVar+":=FALSE";
            if(algorithm.Text[i].contains(rowTrue)){

                for(int j=0;j<ecState.Name.length;j++){
                    if (ecState.Algorithm[j].equals(algorithm.Name[i])){
                        rowT+=ecState.Name[j]+" | next(_state) = ";
                        break;
                    }
                }

            }
            else if(algorithm.Text[i].contains(rowFalse)){
                for(int j=0;j<ecState.Name.length;j++){
                    if (ecState.Algorithm[j].equals(algorithm.Name[i])){
                        rowF+=ecState.Name[j]+" | next(_state) = ";
                        break;
                    }
                }
            }

        }
        rowF=replaceLast(rowF,"| next(_state) ="," : FALSE;\n");
        rowT=replaceLast(rowT,"| next(_state) ="," : TRUE;\n");
        //smv.addNewRow(rowT);
       // smv.addNewRow(rowF);
        String closeRow="\tTRUE :"+outputVar+";\nesac;\n \n";
        rowLine+=rowT+rowF+closeRow;

        //smv.addNewRow(closeRow);
        return rowLine;

    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public void buildSmvModel( ECState ecState, ECTransition ecTransition,
                               Algorithm algorithm,String condition [],String[] inputVars, String [] outputVars){

        try {

           // File file2 = new File("src/smv_model/first.smv");

            //Открываем 1-й файл для записи
            BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(this.file, true)); // true - добавление в конец файла

            //Открываем 2-й файл для считывания
           // BufferedInputStream bufRead = new BufferedInputStream(new FileInputStream(file2));

            /*
            int n;
            while ((n = bufRead.read()) != -1) {
                bufOut.write(n);
            }
            bufOut.flush();      // Принудительно выталкиваем данные с буфера
            bufOut.close();     // Закрываем соединения
            bufRead.close();  //
          */


            String first;



            SmvModel smv = new SmvModel(this.file,this.path);

            first="--controller model\n";// +
                 //   "MODULE CONTROL(REQ, c1Home, c1End, c2Home, c2End, vcHome, vcEnd, pp1, pp2, pp3) \n" +
                 //   "VAR\n";

            smv.addNewRow(first);

            // module control init vars
            first="MODULE CONTROL(REQ,";
            for(int i=0;i<inputVars.length-2;i++){
                first+=inputVars[i]+", ";
            }
            first+=inputVars[inputVars.length-2]+")\nVAR\n";

            first+="\t_state : {";

            for(int i=0;i<ecState.Name.length-1;i++){
                first+=ecState.Name[i]+", ";
            }
            first+=ecState.Name[ecState.Name.length-1]+"};\n";


            first+="\tCNF : boolean;\n";
            for(int i=0;i< outputVars.length;i++){
                first+="\t"+outputVars[i]+" : boolean;\n";
            }
            first+="\tvs : boolean;\n"+
                    "\tvac : boolean;\n\n";
            smv.addNewRow(first);

            first="ASSIGN\n\tinit(_state) := "+ecState.Name[0]+";\n\n"+
            "--variable part of controller starts here--\n\n"+
            "next(_state) := case\n";
            smv.addNewRow(first);


            String row = "_state = ";




            // replace AND -> &
            for (int i=0;i<condition.length;i++){
                if(condition[i].contains("AND")) {
                    condition[i] = condition[i].replace("AND", "&");
                }
                // replace OR -> |
                if(condition[i].contains("OR"))
                {
                    condition[i] = condition[i].replace("OR", "|");
                }
                // replace NOT -> !
                if(condition[i].contains("NOT")) {
                    condition[i] = condition[i].replace("NOT", "!");
                }

            }



            // ot pervonachalnogo ya vischitau pervii stroki statov, a tolko potom yje next






                String rowToAdd="\n";
                rowToAdd = "\t" + row + ecTransition.Source[0]  + " : "
                        + ecTransition.Destination[0] + ";\n";
                smv.addNewRow(rowToAdd);



            for(int i=1; i<condition.length;i++)
            {
                rowToAdd="\n";
                if(!ecTransition.Destination[i].equals("INIT_S")){

                    if(!ecTransition.Source[i].equals("INIT_S")) {
                        rowToAdd = "\t" + row + ecTransition.Source[i] + " & REQ & " + condition[i] + " : "
                                + ecTransition.Destination[i] + ";\n";
                        smv.addNewRow(rowToAdd);
                    }
                    else{
                        rowToAdd = "\t" + row + ecTransition.Source[i]  + " : "
                                + ecTransition.Destination[i] + ";\n";
                        smv.addNewRow(rowToAdd);
                    }
                }
                else {

                    if (ecTransition.Source[i].equals("START")) {
                        rowToAdd="\t"+row+ecTransition.Source[i]+" : "
                                +ecTransition.Destination[i]+";\n";
                        smv.addNewRow(rowToAdd);
                    }
                }
            }


            String closeRow="TRUE : _state;\nesac;\n \n";
            smv.addNewRow(closeRow);

            //next(CNF) := case

            // just change "i" from "0" -> "2"

            String rowLine="next(CNF) := case\n";
            smv.addNewRow(rowLine);
            String next_state=" next(_state) ";
            for(int i=1; i<condition.length;i++)
            {
                rowToAdd="\n";
                if(!ecTransition.Destination[i].equals("INIT_S")){

                    if(!ecTransition.Source[i].equals("INIT_S") && !ecTransition.Source[i].equals("START")) {
                        rowToAdd = "\t" + row + ecTransition.Source[i] + " & REQ &" + next_state + "= "
                                + ecTransition.Destination[i] + " & "
                                + condition[i] + ": TRUE;\n";
                        smv.addNewRow(rowToAdd);
                    }
                }

            }
            closeRow="TRUE : FALSE;\nesac;\n \n";
            smv.addNewRow(closeRow);


            //outputVars description

            for(int i=0;i<outputVars.length;i++){
                rowToAdd=outputVarsDiscription(algorithm,ecState,outputVars[i]);
                smv.addNewRow(rowToAdd);
            }

            File file3 = new File("smv_model/C.smv");

            //Открываем 1-й файл для записи
            BufferedOutputStream bufOut2 = new BufferedOutputStream(new FileOutputStream(this.file, true)); // true - добавление в конец файла

            //Открываем 2-й файл для считывания
            BufferedInputStream bufRead2 = new BufferedInputStream(new FileInputStream(file3));
            int nk;
            while ((nk = bufRead2.read()) != -1) {
                bufOut2.write(nk);
            }
            bufOut2.flush();      // Принудительно выталкиваем данные с буфера
            bufOut2.close();     // Закрываем соединения
            bufRead2.close();  //

        }




        catch ( IOException ex) {
            ex.printStackTrace(System.out);
        }


    }
    public void clearFile(){
        try{
            FileWriter fw = new FileWriter("smv_model/Controller.smv");
            PrintWriter pw = new PrintWriter(fw);
            pw.write("");
            pw.flush();
            pw.close();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    public static void main(String args[]) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("CentralController.xml");

            EventInputs eventInputs = parseInfo.parseEventInputs(document);
            EventOutputs eventOutputs = parseInfo.parseEventOutputs(document);
            String[] inputVars = parseInfo.parseInputVars(document);
            String[] outputVars = parseInfo.parseOutputVars(document);
            ECState ecState = parseInfo.parseECState(document);
            ECTransition ecTransition = parseInfo.parseECTransition(document);
            Algorithm algorithm = parseInfo.parseAlgorithm(document);

            ArrayList <BooleanExpressionTree> trees =new ArrayList<BooleanExpressionTree>();
            for(int i=0;i<ecTransition.Condition.length;i++){
                if(!ecTransition.Condition[i].equals("INIT") && !ecTransition.Source[i].equals("START")) {
                    BooleanExpressionTree tree = new BooleanExpressionTree();
                    tree.constructTree(ecTransition.Condition[i]);
                    trees.add(tree);
                }
                else if (ecTransition.Source[i].equals("START")){
                    BooleanExpressionTree tree = new BooleanExpressionTree();
                    tree.constructTree(ecTransition.Condition[i]);
                    trees.add(tree);
                }
            }
            BooleanExpressionTree [] someTree =trees.toArray(new BooleanExpressionTree[trees.size()]);
            String [] condition= new String[someTree.length];
            //someTree[3].changeTree(someTree[11]);

            for(int i=0;i<someTree.length;i++)
            {
                condition[i]=someTree[i].makeString();
            }


            File file = new File("smv_model/Controller.smv");
            SmvModel smv = new SmvModel(file, "smv_model/Controller.smv");

            smv.clearFile();
            smv.buildSmvModel(ecState,ecTransition,algorithm,condition,inputVars,outputVars);

           // System.out.println("infix expression is");
        }
        catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }

    }
}
