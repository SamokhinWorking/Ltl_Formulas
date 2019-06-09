package smv_model;
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
                               Algorithm algorithm,String condition []){

        try {

            File file2 = new File("src/smv_model/first.smv");

            //Открываем 1-й файл для записи
            BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(this.file, true)); // true - добавление в конец файла

            //Открываем 2-й файл для считывания
            BufferedInputStream bufRead = new BufferedInputStream(new FileInputStream(file2));
            int n;
            while ((n = bufRead.read()) != -1) {
                bufOut.write(n);
            }
            bufOut.flush();      // Принудительно выталкиваем данные с буфера
            bufOut.close();     // Закрываем соединения
            bufRead.close();  //

            SmvModel smv = new SmvModel(this.file,this.path);
            String row = "_state = ";

            /*
            //replace INIT
            for (int i=0;i<ecTransition.Source.length;i++){
                ecTransition.Source[i]=ecTransition.Source[i].replace("INIT","INIT_S");
                ecTransition.Destination[i]=ecTransition.Destination[i].replace("INIT","INIT_S");
            }
            */
            // add next(_state) := case



            // replace AND -> &
            for (int i=0;i<condition.length;i++){
                condition[i]=condition[i].replace("AND","&");
            }
            // replace OR -> |
            for (int i=0;i<condition.length;i++){
                condition[i]=condition[i].replace("OR","|");
            }
            // replace NOT -> !
            for (int i=0;i<condition.length;i++){
                condition[i]=condition[i].replace("NOT","!");
            }


            for(int i=0; i<condition.length;i++)
            {
                String rowToAdd="\n";
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



            String rowLine="next(CNF) := case\n";
            smv.addNewRow(rowLine);
            String next_state=" next(_state) ";
            for(int i=0; i<condition.length;i++)
            {
                String rowToAdd="\n";
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




            //init(c1Extend) := FALSE;
            rowLine="init(c1Extend) := FALSE;\n\nnext(c1Extend) := case\n";
            smv.addNewRow(rowLine);


            String rowT="\tFALSE | next(_state) = ";
            String rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="c1Extend:=TRUE";
                String rowFalse="c1Extend:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : c1Extend;\nesac;\n \n";
            smv.addNewRow(closeRow);

            //c2Extend

            rowLine="init(c2Extend) := FALSE;\n\nnext(c2Extend) := case\n";
            smv.addNewRow(rowLine);



            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="c2Extend:=TRUE";
                String rowFalse="c2Extend:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : c2Extend;\nesac;\n \n";
            smv.addNewRow(closeRow);

            //c1Retract

            rowLine="init(c1Retract) := FALSE;\n\nnext(c1Retract) := case\n";
            smv.addNewRow(rowLine);

            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="c1Retract:=TRUE";
                String rowFalse="c1Retract:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : c1Retract;\nesac;\n \n";
            smv.addNewRow(closeRow);


            //c2Retract

            rowLine="init(c2Retract) := FALSE;\n\nnext(c2Retract) := case\n";
            smv.addNewRow(rowLine);

            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="c2Retract:=TRUE";
                String rowFalse="c2Retract:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : c2Retract;\nesac;\n \n";
            smv.addNewRow(closeRow);

            //vcExtend

            rowLine="init(vcExtend) := FALSE;\n\nnext(vcExtend) := case\n";
            smv.addNewRow(rowLine);



            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="vcExtend:=TRUE";
                String rowFalse="vcExtend:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : vcExtend;\nesac;\n \n";
            smv.addNewRow(closeRow);



            //vacuum_on


            rowLine="init(vacuum_on) := FALSE;\n\nnext(vacuum_on) := case\n";
            smv.addNewRow(rowLine);



            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="vacuum_on:=TRUE";
                String rowFalse="vacuum_on:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : vacuum_on;\nesac;\n \n";
            smv.addNewRow(closeRow);


            //vacuum_off

            rowLine="init(vacuum_off) := FALSE;\n\nnext(vacuum_off) := case\n";
            smv.addNewRow(rowLine);



            rowT="\tFALSE | next(_state) = ";
            rowF="\tFALSE | next(_state) = ";

            //row for TRUE / False
            for (int i=1; i<algorithm.Text.length;i++)
            {
                String rowTrue="vacuum_off:=TRUE";
                String rowFalse="vacuum_off:=FALSE";
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
            smv.addNewRow(rowT);
            smv.addNewRow(rowF);
            closeRow="\tTRUE : vacuum_off;\nesac;\n \n";
            smv.addNewRow(closeRow);


            File file3 = new File("src/smv_model/C.smv");

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
            FileWriter fw = new FileWriter("src/smv_model/Controller.smv");
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
            someTree[3].changeTree(someTree[11]);

            for(int i=0;i<someTree.length;i++)
            {
                condition[i]=someTree[i].makeString();
            }


            File file = new File("src/smv_model/Controller.smv");
            SmvModel smv = new SmvModel(file, "src/smv_model/Controller.smv");

            smv.clearFile();
            smv.buildSmvModel(ecState,ecTransition,algorithm,condition);

           // System.out.println("infix expression is");
        }
        catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }

    }
}
