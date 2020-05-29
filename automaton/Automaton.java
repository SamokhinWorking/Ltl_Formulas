package automaton;


import ec.util.MersenneTwisterFast;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import tree.Lexer;
import tree.ast.BooleanExpression;
import tree.parser.RecursiveDescentParser;

import javax.crypto.spec.PSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntFunction;

public class Automaton {

    enum MutationType {
        TRANSITION_DESTINATION,
        TRANSITION_CONDITION,
//        ALGORITHM
    }

    private static String[] eventInputs;
    private static String[] eventOutputs;
    private static String[] inputVars;
    private static String[] outputVars;
    private String[] ecStateName;
    private String[] ecStateAlgorithmName;
    private String[] transitionSource;
    private String[] transitionDestination;
    private String[] transitionCondition;
    private String[] algorithmName;
    private String[] algorithmText;

    public static Automaton automaton = new Automaton("CentralController.xml");
    private static String plantPart = readPlantModel();

    public Automaton(String filename) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(filename);

            eventInputs = parseEventInputs(document);
            eventOutputs = parseEventOutputs(document);
            inputVars = parseInputVars(document);
            outputVars = parseOutputVars(document);

            ECStates ecState = parseECState(document);
            ecStateName = ecState.name;
            ecStateAlgorithmName = ecState.algorithm;

            ECTransition ecTransition = parseECTransition(document);
            transitionSource = ecTransition.source;
            transitionDestination = ecTransition.destination;
            transitionCondition = ecTransition.condition;

            ECAlgorithm algorithm = parseAlgorithm(document);
            algorithmName = algorithm.name;
            algorithmText = algorithm.text;

            for (int i = 0; i < transitionCondition.length; i++) {
                if (transitionCondition[i].contains("AND")) {
                    transitionCondition[i] = transitionCondition[i].replace("AND", "&");
                }
                // replace OR -> |
                if (transitionCondition[i].contains("OR")) {
                    transitionCondition[i] = transitionCondition[i].replace("OR", "|");
                }
                // replace NOT -> !
                if (transitionCondition[i].contains("NOT")) {
                    transitionCondition[i] = transitionCondition[i].replace("NOT", "!");
                }
                // replace IMPL -> "->"
                if (transitionCondition[i].contains("IMPL")) {
                    transitionCondition[i] = transitionCondition[i].replace("IMPL", "->");
                }
            }
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public Automaton(Automaton other) {
        this.eventInputs = Arrays.copyOf(other.eventInputs, other.eventInputs.length);
        this.eventOutputs = Arrays.copyOf(other.eventOutputs, other.eventOutputs.length);
        this.inputVars = Arrays.copyOf(other.inputVars, other.inputVars.length);
        this.outputVars = Arrays.copyOf(other.outputVars, other.outputVars.length);
        this.ecStateName = Arrays.copyOf(other.ecStateName, other.ecStateName.length);
        this.ecStateAlgorithmName = Arrays.copyOf(other.ecStateAlgorithmName, other.ecStateAlgorithmName.length);
        this.transitionSource = Arrays.copyOf(other.transitionSource, other.transitionSource.length);
        this.transitionDestination = Arrays.copyOf(other.transitionDestination, other.transitionDestination.length);
        this.transitionCondition = Arrays.copyOf(other.transitionCondition, other.transitionCondition.length);
        this.algorithmName = Arrays.copyOf(other.algorithmName, other.algorithmName.length);
        this.algorithmText = Arrays.copyOf(other.algorithmText, other.algorithmText.length);
    }

    public void mutate(MersenneTwisterFast random) {
        //select random transition
        int transition = 0;

        while (transition < 2) {
            transition = random.nextInt(transitionSource.length);
        }


        MutationType mutationType = MutationType.values()[random.nextInt(MutationType.values().length)];
        //either change the transition destination or condition
        switch (mutationType) {
            case TRANSITION_DESTINATION: {
                String newDestination = ecStateName[random.nextInt(ecStateName.length)];
                while (newDestination.equals(transitionDestination[transition])) {
                    newDestination = ecStateName[random.nextInt(ecStateName.length)];
                }
                transitionDestination[transition] = newDestination;
                break;
            }
            case TRANSITION_CONDITION: {
                BooleanExpression be = new RecursiveDescentParser(new Lexer(transitionCondition[transition])).build();
                be = be.mutate(random);
                transitionCondition[transition] = be.toString();
                break;
            }
//            case ALGORITHM: {
//                break;
//            }
        }
    }

    public String[] getAlgorithmName() {
        return algorithmName;
    }

    public String[] getAlgorithmText() {
        return algorithmText;
    }

    public String[] getECStateName() {
        return ecStateName;
    }

    public String[] getEcStateAlgorithmName() {
        return ecStateAlgorithmName;
    }

    public String[] getECTransitionSource() {
        return transitionSource;
    }

    public String[] getECTransitionDestination() {
        return transitionDestination;
    }

    public String[] getECTransitionCondition() {
        return transitionCondition;
    }

    public String[] getInputVars() {
        return inputVars;
    }

    public String[] getOutputVars() {
        return outputVars;
    }

    private static String readPlantModel() {
        Scanner in = null;
        try {
            in = new Scanner(new File("smv_model/C.smv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        while (in.hasNext()) {
            sb.append(in.nextLine()).append("\n");
        }

        in.close();
        return sb.toString();
    }

    public String toSMV() {
        StringBuilder sb = new StringBuilder();
        String first = "--controller model\n";

        sb.append(first);

        // module control init vars
        sb.append("MODULE CONTROL(REQ,");
        for (int i = 0; i < inputVars.length - 2; i++) {
            sb.append(inputVars[i]).append(", ");
        }
        sb.append(inputVars[inputVars.length- 2 ]).append(")\nVAR\n");
        sb.append("\t_state : {");

        for (int i = 0; i < ecStateName.length - 1; i++){
            sb.append(ecStateName[i]).append(", ");
        }
        sb.append(ecStateName[ecStateName.length - 1]).append("};\n");
        sb.append("\tCNF : boolean;\n");
        for (int i = 0; i < outputVars.length; i++){
            sb.append("\t").append(outputVars[i]).append(" : boolean;\n");
        }

        sb.append("\tvs : boolean;\n").append("\tvac : boolean;\n\n");
        sb.append("ASSIGN\n\tinit(_state) := " + ecStateName[0] + ";\n\n" +
                "--variable part of controller starts here--\n\n" +
                "next(_state) := case\n");

        // ot pervonachalnogo ya vischitau pervii stroki statov, a tolko potom yje next

        sb.append("\t").append("_state = ").append(transitionSource[0]).append(" : ").append(transitionDestination[0]).append(";\n");

        for (int i = 1; i < transitionCondition.length; i++) {
            if (!transitionDestination[i].equals("INIT_S")) {
                if (!transitionSource[i].equals("INIT_S")) {
                    sb.append("\t_state = ").append(transitionSource[i]).append(" & REQ & ").append(transitionCondition[i]).append(" : ")
                            .append(transitionDestination[i]).append(";\n");
                } else {
                    sb.append("\t_state = ").append(transitionSource[i]).append(" : ")
                            .append(transitionDestination[i]).append(";\n");
                }
            } else {
                if (transitionSource[i].equals("START")) {
                    sb.append("\t_state = ").append(transitionSource[i]).append(" : ")
                            .append(transitionDestination[i]).append(";\n");
                }
            }
        }

        sb.append("TRUE : _state;\nesac;\n \n");

        //next(CNF) := case
        // just change "i" from "0" -> "2"

        sb.append("next(CNF) := case\n");
        String next_state = " next(_state) ";
        for (int i = 1; i < transitionCondition.length; i++) {
            if (!transitionDestination[i].equals("INIT_S")) {
                if (!transitionSource[i].equals("INIT_S") && !transitionSource[i].equals("START")) {
                    sb.append("\t_state = ").append(transitionSource[i]).append(" & REQ &").append(next_state).append("= ")
                            .append(transitionDestination[i]).append(" & ")
                            .append(transitionCondition[i]).append(": TRUE;\n");
                }
            }
        }

        sb.append("TRUE : FALSE;\nesac;\n \n");

        //outputVars description
        for (int i = 0; i < outputVars.length; i++) {
            sb.append(outputVarsDescription(algorithmName, algorithmText, ecStateName, ecStateAlgorithmName, outputVars[i]));
        }
        sb.append(plantPart);
        return sb.toString();
    }

    public String outputVarsDescription(String[] algorithmName, String[] algorithmText, String[] ecStateName,
                                        String[] ecStateAlgorithmName, String outputVar) {
        String rowLine = "init(" + outputVar + ") := FALSE;\n\nnext(" + outputVar + ") := case\n";

        String rowT = "\tFALSE | next(_state) = ";
        String rowF = "\tFALSE | next(_state) = ";

        //row for TRUE / False
        for (int i = 0; i < algorithmText.length; i++) {
            String rowTrue = outputVar + ":=TRUE";
            String rowFalse = outputVar + ":=FALSE";
            if (algorithmText[i].contains(rowTrue)) {
                for (int j = 0; j < ecStateName.length; j++) {
                    if (ecStateAlgorithmName[j].equals(algorithmName[i])) {
                        rowT += ecStateName[j] + " | next(_state) = ";
                        break;
                    }
                }
            } else if (algorithmText[i].contains(rowFalse)) {
                for (int j = 0; j < ecStateName.length; j++) {
                    if (ecStateAlgorithmName[j].equals(algorithmName[i])) {
                        rowF += ecStateName[j] + " | next(_state) = ";
                        break;
                    }
                }
            }
        }

        rowF = replaceLast(rowF,"| next(_state) ="," : FALSE;\n");
        rowT = replaceLast(rowT,"| next(_state) ="," : TRUE;\n");
        String closeRow = "\tTRUE :" + outputVar + ";\nesac;\n \n";
        rowLine += rowT + rowF + closeRow;
        return rowLine;
    }


    private static String[] parseEventInputs(Document document) throws DOMException, XPathExpressionException{
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/InterfaceList/EventInputs/Event");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> commentList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
                commentList.add(eElement.getAttribute("Comment"));
            }
        }
        return nameList.toArray(new String[nameList.size()]);
    }

    private static String[] parseEventOutputs(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/InterfaceList/EventOutputs/Event");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> commentList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
                commentList.add(eElement.getAttribute("Comment"));
            }
        }
        return nameList.toArray(new String[nameList.size()]);
    }

    private static String[] parseInputVars(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/InterfaceList/InputVars/VarDeclaration");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
            }
        }
        String[] name = nameList.toArray(new String[nameList.size()]);
        return name;
    }

    private static String[] parseOutputVars(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/InterfaceList/OutputVars/VarDeclaration");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        ArrayList<String> nameList = new ArrayList<String>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
            }
        }
        String[] name = nameList.toArray(new String[nameList.size()]);
        return name;
    }

    private static ECStates parseECState(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/BasicFB/ECC/ECState");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
            }
        }
        String[] name = nameList.toArray(new String[nameList.size()]);

        XPathExpression expr2 = xpath.compile("FBType/BasicFB/ECC/ECState/ECAction");
        NodeList nodeAlgorithms = (NodeList) expr2.evaluate(document, XPathConstants.NODESET);
        ArrayList<String> algorithmsList = new ArrayList<String>();
        algorithmsList.add(" ");
        for (int i = 0; i < nodeAlgorithms.getLength(); i++) {
            Node n = nodeAlgorithms.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                algorithmsList.add(eElement.getAttribute("Algorithm"));
            }
        }
        String[] algorithm = algorithmsList.toArray(new String[algorithmsList.size()]);
        for (int i = 0; i < name.length; i++) {
            name[i] = name[i].replace("INIT","INIT_S");
            algorithm[i] = algorithm[i].replace("INIT","INIT_S");
        }
        ECStates ecState = new ECStates(name, algorithm);
        return ecState;
    }

    private static ECTransition parseECTransition(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/BasicFB/ECC/ECTransition");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> sourceList = new ArrayList<String>();
        ArrayList<String> destinationList = new ArrayList<String>();
        ArrayList<String> conditionList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                sourceList.add(eElement.getAttribute("Source"));
                destinationList.add(eElement.getAttribute("Destination"));
                conditionList.add(eElement.getAttribute("Condition"));
            }
        }
        int metka = 15;
        while (sourceList.size() > metka) {
            sourceList.remove(metka);
            destinationList.remove(metka);
            conditionList.remove(metka);
        }

        String[] source = sourceList.toArray(new String[sourceList.size()]);
        String[] destination=destinationList.toArray(new String[destinationList.size()]);
        String[] condition = conditionList.toArray(new String[conditionList.size()]);

        //replace INIT
        for (int i = 0; i < source.length; i++) {
            source[i] = source[i].replace("INIT","INIT_S");
            destination[i] = destination[i].replace("INIT","INIT_S");
            condition[i] = condition[i].replace("INIT","INIT_S");
        }

        for(int i = 0; i < condition.length; i++) {
            condition[i] = condition[i].replaceAll("  "," ");
            condition[i] = condition[i].replaceAll("\\(","( ");
            condition[i] = condition[i].replaceAll("\\)"," )");
            //delete REQ&
            condition[i] = condition[i].replace("REQ&","");
        }

        ECTransition ecTransition = new ECTransition(source, destination, condition);
        return ecTransition;

    }

    private static ECAlgorithm parseAlgorithm(Document document) throws DOMException, XPathExpressionException {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile("FBType/BasicFB/Algorithm");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                nameList.add(eElement.getAttribute("Name"));
            }
        }
        String[] name = nameList.toArray(new String[nameList.size()]);

        XPathExpression expr2 = xpath.compile("FBType/BasicFB/Algorithm/ST");
        NodeList nodeText = (NodeList) expr2.evaluate(document, XPathConstants.NODESET);
        ArrayList<String> textList = new ArrayList<String>();

        for (int i = 0; i < nodeText.getLength(); i++) {
            Node n = nodeText.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                textList.add(eElement.getAttribute("Text"));
            }
        }
        String[] text = textList.toArray(new String[textList.size()]);
        ECAlgorithm algorithm =new ECAlgorithm(name,text);
        return algorithm;
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

    public static Automaton generateRandomAutomaton(MersenneTwisterFast random) {
        Automaton a = new Automaton(automaton);
        int minStateDegree = 1;
        int maxStateDegree = 3;
        int minGuardConditionSize = 1;
        int maxGuardConditionSize = 10;
        int nStates = automaton.algorithmName.length;

        List<Integer> src = new ArrayList<>();
        List<Integer> dst = new ArrayList<>();
        List<String> guard = new ArrayList<>();

        for (int source = 0; source < nStates; source++) {
            int degree = random.nextInt(maxStateDegree - minStateDegree) + minStateDegree;
            for (int i = 0; i < degree; i++) {
                int destination = random.nextInt(nStates);
                //we need to have at least one non-loop transition
                if (i == 0) {
                    while (destination == source) {
                        destination = random.nextInt(nStates);
                    }
                }
                src.add(source);
                dst.add(destination);
            }
        }

        String terminals = Arrays.toString(automaton.getInputVars()).replaceAll(",", "");
        terminals = terminals.substring(1, terminals.length() - 1);

        StringBuilder sb = new StringBuilder("randltl").append(" -n ").append(src.size() * 3).append(" -p ").append(terminals)
                .append(" --ltl-priorities=X=0,xor=0,U=0,R=0,F=0,G=0,W=0,M=0,implies=0,equiv=0")
                .append(" --tree-size=").append(minGuardConditionSize).append("..").append(maxGuardConditionSize).append(" --seed ").append(System.currentTimeMillis());

        try {
            Process p = Runtime.getRuntime().exec(sb.toString());
            Scanner sc = new Scanner(new BufferedInputStream(p.getInputStream()));
            while (sc.hasNextLine()) {
                if (guard.size() == src.size()) {
                    break;
                }
                String line = sc.nextLine();
                if (!line.equalsIgnoreCase("0") && !line.equalsIgnoreCase("1")) {
                    guard.add(line);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < src.size(); i++) {
//            System.out.println(src.get(i) + " -> " + dst.get(i) + " [" + guard.get(i) + "]");
//        }

        a.ecStateName = new String[nStates];
        for (int i = 0; i < nStates; i++) {
            a.ecStateName[i] = i + "";
        }

        a.algorithmName = new String[nStates];
        for (int i = 0; i < nStates; i++) {
            a.algorithmName[i] = "alg_" + i;
        }

        a.transitionCondition = guard.toArray(new String[0]);
        a.transitionSource = Arrays.stream(src.toArray(new Integer[0])).map(x -> x + "").toArray(String[]::new);
        a.transitionDestination = Arrays.stream(dst.toArray(new Integer[0])).map(x -> x + "").toArray(String[]::new);

        for (int state = 0; state < nStates; state++) {
            StringBuilder alg = new StringBuilder();

            for (int i = 0; i < outputVars.length; i++) {
                if (random.nextDouble() < 2.0 / outputVars.length) {
                    alg.append(outputVars[i]).append(":=").append(random.nextBoolean() ? "TRUE" : "FALSE").append(";\n");
                }
            }

            a.algorithmText[state] = alg.toString();
        }

//        System.out.println(Arrays.toString(a.algorithmText));

        return a;
    }


    public static void main(String[] args) {
        Automaton auto = new Automaton("CentralController.xml");
        MersenneTwisterFast random = new MersenneTwisterFast();
    }
}

