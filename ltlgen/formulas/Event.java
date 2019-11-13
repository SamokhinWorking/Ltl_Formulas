package ltlgen.formulas;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ltlgen.LTLData;
import ltlgen.LTLProblem;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import parse.parseInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Random;

public class Event extends GPNode  implements Verifiable {

    @Override
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
        LTLData data = ((LTLData) (input));
        data.result = toString();
        data.complexity = 1;
    }

    @Override
    public String toStringForHumans() {
        return toString();
    }

    @Override
    public String toStringForVerifier() {
        return toString();
    }

    @Override
    public String toString() {
        String output="Event";
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("CentralController.xml");

            String[] outputVars= parseInfo.parseOutputVars(document);

            for(int i=0;i<outputVars.length;i++)
            {
                outputVars[i]="C."+outputVars[i];
            }

            Random random = new Random();
            int indx = random.nextInt(outputVars.length);
            output= outputVars[indx];

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
        return output;
    }
}
