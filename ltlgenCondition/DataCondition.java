package ltlgenCondition;

import ec.gp.GPData;

public class DataCondition extends GPData {
    public String result;
    public int complexity;

    public void copyTo(GPData gpd) {
        DataCondition ld = (DataCondition) gpd;
        ld.result = result;
        ld.complexity = complexity;
    }
}