package formulaBuilding;
import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.Gene;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;


import parse.parseInfo;

public class StringVectorGene  extends Gene {
    public final static String P_ALPHABET = "alphabet";

    private static String []     inputVars;
    private static String []  helpVars={"NOT","AND","OR","(",")"};
    private String              allele;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("CentralController.xml");
            inputVars=parseInfo.parseInputVars(document);

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }



    /*
     * (non-Javadoc)
     * @see ec.vector.VectorGene#reset(ec.EvolutionState, int)
     */
    @Override
    public void reset(EvolutionState state, int thread) {

        int idx_mas = state.random[thread].nextInt(2);
        if(idx_mas==1){
            int idx_data = state.random[thread].nextInt(helpVars.length);
            allele=helpVars[idx_data];
        }
        else{
            int idx_data =state.random[thread].nextInt(inputVars.length);
            allele=inputVars[idx_data];
        }

    }

    public String getAllele() {
        return allele;
    }

    /*
     * (non-Javadoc)
     * @see ec.vector.VectorGene#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!this.getClass().isInstance(other)) {
            return false;
        }

        StringVectorGene that = (StringVectorGene) other;

        return allele == that.allele;
    }

    /*
     * @see ec.vector.VectorGene#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allele == null) ? 0 : allele.hashCode());
       // result =result *prime+((inputVars==null))


        return result;
    }

    @Override
    public Object clone() {
        StringVectorGene VectorGene = (StringVectorGene) (super.clone());

        return VectorGene;
    }

    @Override
    public String toString() {
        return allele;
    }
}

