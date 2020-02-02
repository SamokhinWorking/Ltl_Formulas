package main;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import helpClass.*;
import parse.parseInfo;

public class TestParse {

    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("CentralController.xml");

            EventInputs eventInputs=parseInfo.parseEventInputs(document);
            EventOutputs eventOutputs=parseInfo.parseEventOutputs(document);
            String[] inputVars=parseInfo.parseInputVars(document);
            String[] outputVars =parseInfo.parseOutputVars(document);
            ECState ecState =parseInfo.parseECState(document);
            ECTransition ecTransition =parseInfo.parseECTransition(document);
            Algorithm algorithm =parseInfo.parseAlgorithm(document);

            //delite REQ&
            /*
            for (int i=0;i<ecTransition.Condition.length;i++){
                ecTransition.Condition[i]=ecTransition.Condition[i].replace("REQ&","");
            }
            String some="c1Home AND NOT c1End AND (c2Home OR c2End)";
            System.out.println("length= "+ some.length());
            String foo ="c2Home";
            if(some.contains(foo)){
                System.out.println("foo= " +foo +"  some= "+some);
            }
            */
            System.out.println("InputVars");
            for(int i=0; i<inputVars.length;i++)
            {
                System.out.println(inputVars[i]);
            }
            System.out.println("OutputVars");
            for(int i=0; i<outputVars.length;i++)
            {
                System.out.println(outputVars[i]);
            }
           // ecState.printECState();
            ecTransition.printECTransition();
            algorithm.printAlgorithm();

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

}
