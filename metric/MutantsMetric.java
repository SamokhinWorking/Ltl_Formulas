package metric;

import automat.Automat;
import helpClass.Algorithm;
import helpClass.ECState;
import helpClass.ECTransition;
import smv_model.SmvModel;
import tree.BooleanExpressionTree;
import verifier.Verifier;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class MutantsMetric {
    public static String [] TakeConditions(String name){
        ArrayList<String> lines = new ArrayList<String>();
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;
            String target1="Fitness:";
            String target2="Rank:";
            String target3="Strength:";
            String target4="Distance:";
            String target5="Sparsity:";
            String target6="--------------------";
           // String target7 ="Best individuals:";
            while((line=reader.readLine())!= null){

                if(!line.contains(target1) &&  !line.contains(target2) &&
                        !line.contains(target3) && !line.contains(target4) &&
                        !line.contains(target5) &&!line.contains(target6) ) {
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



        return allCondition;
    }


    public static String TakeTime(String name){
        String time="";
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;
            String target1="Time taken:";

            while((line=reader.readLine())!= null){

                if(line.contains(target1)  ) {
                    time=line;
                    break;
                }
            }
            reader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return time;
    }

    public static String [] TakeSpecification(String name){
        ArrayList<String> lines = new ArrayList<String>();
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;
            String target1="Generation";
            String target2="Rank:";
            String target3="Strength:";
            String target4="Distance:";
            String target5="Sparsity:";
            String target6="--------------------";
            String target7="Fitness:";
            while((line=reader.readLine())!= null){

                if(!line.contains(target1) &&  !line.contains(target2) &&
                        !line.contains(target3) && !line.contains(target4) &&
                        !line.contains(target5) &&!line.contains(target6) && !line.contains(target7)) {
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
        for(int i=0;i<allCondition.length;i++){
            allCondition[i]=allCondition[i].replace("Best individual: ","");
        }


        return allCondition;
    }
    public static String [] TakeSpecification2 (String name){

        ArrayList<String> lines = new ArrayList<String>();
        try{

            BufferedReader reader =new BufferedReader(new FileReader(name));
            String line;

            while((line=reader.readLine())!= null){

                    lines.add(line);

            }
            reader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String [] allSpecification = lines.toArray(new String[lines.size()]);



        return allSpecification;
    }
    public static String [] MakeGoodSpecification(String []spec){



        Verifier ver = new Verifier("C.smv");
        int test=0;


        ArrayList<String> str =new ArrayList<String>();
        for(int i=0;i<spec.length;i++){

            String specification =new String();
            if(spec[i].contains("Best individual:")){
                spec[i]=spec[i].replace("Best individual:","");
            }

               // ver.addNewRow("\nLTLSPEC "+spec[i]);
                test = ver.testLtlFormulas("\nLTLSPEC "+spec[i]);
               // ver.deleteLastRow();

                if(test==1){
                    str.add(spec[i]);
                 //   System.out.println("good= "+spec[i]);
                }
                /*
                else{
                    System.out.println("bad= "+spec[i]);
                }

                 */

        }
       String[] result= str.toArray(new String[str.size()]);
        return result;
    }


    public static String ModifyString (String str){

        String result =str;

        if(str.contains("&")){
            result=result.replace("&","AND");
        }
        if(str.contains("!")){
            result=result.replace("!","NOT");
        }
        if(str.contains("|")){
            result=result.replace("|","OR");
        }
        if(str.contains("->")){
            result=result.replace("->","IMPL");
        }


       return result;
    }

    public static double verifyOldSpec(Automat automat,String []specification,String []condition){
        double result =0.0;
        ECState ecState = automat.getEcState();
        ECTransition ecTransition = automat.getEcTransition();
        Algorithm algorithm = automat.getAlgorithm();
        String[] inputVars=automat.getInputVars();
        String [] outputVars =automat.getOutputVars();



        //create smv file

        File file1 = new File("src/smv_model/Controller.smv");
        SmvModel smv = new SmvModel(file1, "src/smv_model/Controller.smv");

        smv.clearFile();
        smv.buildSmvModel(ecState, ecTransition, algorithm, condition,inputVars,outputVars);


        //Verifier LTL Formulas

       // File file = new File("src/smv_model/Controller.smv");

        //File file = new File("C.smv");

        Verifier ver = new Verifier("src/smv_model/Controller.smv");
        int test=0;

        for(int i=0;i<specification.length;i++){

         //   ver.addNewRow("\nLTLSPEC "+specification[i]);
            test = ver.testLtlFormulas("\nLTLSPEC "+specification[i]);

           // test = ver.testLtlFormulas("C.smv");
           // ver.deleteLastRow();
          //  ver.deleteLastRow();
            if (test ==0){
                result=1.0;
                break;
            }

        }


        return result;
    }

    public static void pritString(String[]a){
        for(int i=0;i<a.length;i++){
            System.out.println(a[i]);
        }
    }
    public static double MutMetrics(Automat automat, String []specification, String []mutCondition){
        double result=0.0;

        ECTransition ecTransition = automat.getEcTransition();

/*

        ArrayList<BooleanExpressionTree> trees =new ArrayList<BooleanExpressionTree>();
        for(int i=0;i<ecTransition.Condition.length;i++){

            BooleanExpressionTree tree = new BooleanExpressionTree();
            tree.constructTree(ecTransition.Condition[i]);
            trees.add(tree);


        }
        BooleanExpressionTree[] someTree = trees.toArray(new BooleanExpressionTree[trees.size()]);

 */

        String[] condition = new String[ecTransition.Condition.length];



        int val=condition.length-1;
        int valMutCond=mutCondition.length;

        Random randomNumber = new Random();

        int BoolTree,MutCond;


        BooleanExpressionTree InitTree =new BooleanExpressionTree();

        BooleanExpressionTree tree =new BooleanExpressionTree();

        String tmp= new String();

        for(int i=0;i<1000;i++) {

            BoolTree = randomNumber.nextInt(val)+1;
            MutCond = randomNumber.nextInt(valMutCond);

          //  System.out.println("booltree= "+BoolTree+"   mutcond= "+MutCond);


            //System.out.println("Init cond= "+ecTransition.Condition[BoolTree]);
            InitTree.constructTree(ecTransition.Condition[BoolTree]);


            //take mutCondition
            tmp=mutCondition[MutCond];

            //modify mutCondition
            tmp=ModifyString(tmp);

            tree.constructTree(tmp);

            //Change one tree from condition
           InitTree.changeTree(tree);



            for (int j = 0; j < ecTransition.Condition.length; j++) {
                if(j!=BoolTree) {
                    condition[j] = ecTransition.Condition[j];
                }
                else{
                    condition[j]=InitTree.makeString();
                  //  System.out.println("someTree="+condition[j]);
                }
            }
            //pritString(condition);

            result+=verifyOldSpec(automat,specification,condition);
         //   System.out.println("i="+i+ " result=="+result);
        }



        return result;
    }

    public static String [] CreateFormulas(String [] s){
        ArrayList <String> arr =new ArrayList<String>();

        //System.out.println("Length of s =="+s.length);
        int tmp=0;

        arr.add(s[0]);
        for(int i=1;i<s.length;i++) {
            tmp = 0;
            for (int j = 0; j < arr.size(); j++) {
                if (s[i].equals(arr.get(j))) {
                    tmp++;
                    break;
                }
            }
            if(tmp==0){
                arr.add(s[i]);
            }
            tmp=0;

        }
        String[] result= arr.toArray(new String[arr.size()]);
     //   System.out.println("length= "+result.length);
        return result;

    }
    public static void twoForOne(File f1, File f2){

        try {
            //Открываем 1-й файл для записи
            BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(f1, true)); // true - добавление в конец файла

            //Открываем 2-й файл для считывания
            BufferedInputStream bufRead = new BufferedInputStream(new FileInputStream(f2));

            int n;
            while ((n = bufRead.read()) != -1) {
                bufOut.write(n);
            }
            bufOut.flush();      // Принудительно выталкиваем данные с буфера
            bufOut.close();     // Закрываем соединения
            bufRead.close();
        }
        catch ( IOException ex) {
            ex.printStackTrace(System.out);
        }


    }

    //dodelat ety function
    public static void writeToFile(String []s, String path){

        File f2= new File(path);
        clearFile(path);
        try {
            //Открываем 1-й файл для записи
            FileWriter writer =new FileWriter(f2,true);
            for(int i=0; i<s.length;i++) {

                writer.write(s[i]+"\n");
            }

            writer.flush();      // Принудительно выталкиваем данные с буфера
            writer.close();     // Закрываем соединения

        }
        catch ( IOException ex) {
            ex.printStackTrace(System.out);
        }


    }

    public static void writeToFileDouble(double []s, String path){

        File f2= new File(path);
        clearFile(path);
        try {
            //Открываем 1-й файл для записи
            FileWriter writer =new FileWriter(f2,true);
            for(int i=0; i<s.length;i++) {

                writer.write(s[i]+"\n");
            }

            writer.flush();      // Принудительно выталкиваем данные с буфера
            writer.close();     // Закрываем соединения

        }
        catch ( IOException ex) {
            ex.printStackTrace(System.out);
        }


    }

    public static void createTestmutant (String n1, String n2, String n3) throws IOException {

        File source =new File("TestMutantMetrics.stat");
        File f1 =new File (n1);
        File f2 =new File (n2);
        File f3 =new File (n3);

        twoForOne(source,f1);

        twoForOne(source,f2);

        twoForOne(source,f3);



        ;

    }
    public static void clearFile(String path){
        try{
            FileWriter fw = new FileWriter(path);
            PrintWriter pw = new PrintWriter(fw);
            pw.write("");
            pw.flush();
            pw.close();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }



    public static double [] MakeStaregi(double []a){

        double []result=new double[14];

        for(int i=0; i<14;i++){
            result[i]=0.0;
        }
        int tmp=0;
        for(int i=0,j=0;i<a.length;i++){
            result[j]+=a[i];
            tmp++;
            if(tmp==10){
                tmp=0;
                j++;
            }

        }
        for(int i=0;i<14;i++){
            result[i]/=10.0;
        }

        return result;
    }

    public static void main (String []args) throws IOException {



       // String [] spec =TakeSpecification2("Old_Spec.txt");

        /*

        String [] times =new String[10];



        for(int i=0;i<10;i++) {

            clearFile("TestMutantMetrics.stat");

            createTestmutant("src/metric/files/job."+i+".log.stat", "src/metric/files/job."+i+".result.stat", "src/metric/files/job."+i+".result-humans.stat");


            String[] spec = TakeSpecification("TestMutantMetrics.stat");

            String[] goodSpecification = MakeGoodSpecification(spec);



        for(int i=0;i<spec.length;i++){
            System.out.println(spec[i]);
        }





        for(int i=0;i<goodSpecification.length;i++){
            System.out.println(goodSpecification[i]);
        }


            String[] formulas = CreateFormulas(goodSpecification);


            for (int i = 0; i < formulas.length; i++) {
                System.out.println(formulas[i]);
            }



            writeToFile(formulas, "src/metric/output/"+i+".txt");


            times[i]=TakeTime( "src/metric/files/job."+i+".result-humans.stat");


        }

        writeToFile(times,"src/metric/output/time.txt");


       */






        Automat automat =new Automat("CentralController.xml");
        String []mutCondition =TakeConditions("src/conditions/result-humans.stat");


/*

        for(int i=0;i<mutCondition.length;i++){
            System.out.println(mutCondition[i]);
        }

*/



        double result;
        double []cmut=new double[140];

        for(int i=0;i<140;i++) {

            String[] goodSpecification = TakeSpecification2("src/metric/files/"+i+".txt");

            result = MutMetrics(automat, goodSpecification, mutCondition);
            result=result/1000.0;
            System.out.println("j= " + i + "  result= " + result);
            cmut[i]=result;

        }

        writeToFileDouble(cmut,"src/metric/output/result.txt");

        double []Csum=MakeStaregi(cmut);

        writeToFileDouble(Csum,"src/metric/output/Cmut.txt");



    }
}
