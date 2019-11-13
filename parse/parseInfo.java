package parse;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import helpClass.*;

import java.util.ArrayList;

import org.w3c.dom.*;

public class parseInfo {

    public static EventInputs parseEventInputs(Document document) throws DOMException, XPathExpressionException{
      //  System.out.println("Печать EventInputs");
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
        String []name=nameList.toArray(new String[nameList.size()]);
        String []comment=commentList.toArray(new String[commentList.size()]);


      //  System.out.println();
        EventInputs eventInputs =new EventInputs(name,comment);
       // eventInputs.printEventInputs();
        return eventInputs;
    }
    public static EventOutputs parseEventOutputs(Document document) throws DOMException, XPathExpressionException{
      //  System.out.println("Печать EventOutput");
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
        String []name=nameList.toArray(new String[nameList.size()]);
        String []comment=commentList.toArray(new String[commentList.size()]);


       // System.out.println();
        EventOutputs eventOutputs =new EventOutputs(name,comment);
       // eventOutputs.printEventOutputs();
        return eventOutputs;
    }
    public static String[] parseInputVars(Document document) throws DOMException, XPathExpressionException {
      //  System.out.println("Печать InputVars");
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
      //  System.out.println();

       // for(int i=0;i<name.length;i++)
       // {
       //     System.out.println("Name= "+name[i]);
      //  }
        return name;
    }
    public static String[] parseOutputVars(Document document) throws DOMException, XPathExpressionException {
       // System.out.println("Печать OutputVars");
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
       // System.out.println();

       // for(int i=0;i<name.length;i++)
       // {
       //     System.out.println("Name= "+name[i]);
       // }
        return name;
    }
    public static ECState parseECState(Document document) throws DOMException, XPathExpressionException {
        // System.out.println("Печать OutputVars");
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
        //System.out.println();

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
        for (int i = 0; i < name.length; i++)
        {
            name[i]=name[i].replace("INIT","INIT_S");
            algorithm[i]=algorithm[i].replace("INIT","INIT_S");
        }
        ECState ecState =new ECState(name,algorithm);

       // ecState.printECState();
        return ecState;

    }
    public static ECTransition parseECTransition(Document document) throws DOMException, XPathExpressionException {
        // System.out.println("Печать OutputVars");
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

        int metka=15;

        while (sourceList.size()>metka)
        {
            sourceList.remove(metka);
            destinationList.remove(metka);
            conditionList.remove(metka);
        }

        String[] source = sourceList.toArray(new String[sourceList.size()]);
        String[] destination=destinationList.toArray(new String[destinationList.size()]);
        String[] condition = conditionList.toArray(new String[conditionList.size()]);
        //replace INIT
        for (int i=0;i<source.length;i++){
            source[i]=source[i].replace("INIT","INIT_S");
            destination[i]=destination[i].replace("INIT","INIT_S");
            condition[i]=condition[i].replace("INIT","INIT_S");
        }
        for(int i=0; i<condition.length;i++)
        {
            condition[i]=condition[i].replaceAll("  "," ");
            condition[i]=condition[i].replaceAll("\\(","( ");
            condition[i]=condition[i].replaceAll("\\)"," )");
            //delite REQ&
            condition[i]=condition[i].replace("REQ&","");

        }


        //System.out.println();

        ECTransition ecTransition = new ECTransition(source,destination,condition);
        // ecTransition.printECTransition();
        return ecTransition;

    }
    public static Algorithm parseAlgorithm(Document document) throws DOMException, XPathExpressionException {
       // System.out.println("Печать Algorothms");
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
       // System.out.println();

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
        Algorithm algorithm =new Algorithm(name,text);
       // algorithm.printAlgorithm();

        return algorithm;

    }

}

