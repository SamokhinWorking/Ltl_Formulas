package automat;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import helpClass.*;
import parse.parseInfo;

import java.util.Random;

public class Automat {

    EventInputs eventInputs;
    EventOutputs eventOutputs;
    String[] inputVars;
    String[] outputVars;
    ECState ecState;
    ECTransition ecTransition;
    Algorithm algorithm;

    public Automat(String path)
    {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(path);

            EventInputs eventInputs=parseInfo.parseEventInputs(document);
            EventOutputs eventOutputs=parseInfo.parseEventOutputs(document);
            String[] inputVars=parseInfo.parseInputVars(document);
            String[] outputVars =parseInfo.parseOutputVars(document);
            ECState ecState =parseInfo.parseECState(document);
            ECTransition ecTransition =parseInfo.parseECTransition(document);
            Algorithm algorithm =parseInfo.parseAlgorithm(document);

            this.ecState=ecState;
            this.algorithm=algorithm;
            this.eventInputs=eventInputs;
            this.eventOutputs=eventOutputs;
            this.inputVars=inputVars;
            this.outputVars=outputVars;
            this.ecTransition=ecTransition;

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public EventInputs getEventInputs()
    {
        return this.eventInputs;
    }

    public EventOutputs getEventOutputs() {
        return eventOutputs;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
    public ECState getEcState()
    {
        return this.ecState;
    }

    public ECTransition getEcTransition() {
        return ecTransition;

    }

    public String[] getInputVars() {
        return inputVars;
    }

    public String[] getOutputVars() {
        return outputVars;
    }


    public static void main(String[] args) {

        Automat auto = new Automat("CentralController.xml");

        ECTransition ecTransition=auto.getEcTransition();

        //System.out.println(ecTransition.Condition.length);

    }

}

