package formulaBuilding;

import ec.Evolve;

public class MainTest {
    public static void main(String[] args) {
        String pathToFiles = "src/formulaBuilding/";
        int numberOfJobs = 1;
//		String statisticType = "ec.gp.koza.KozaShortStatistics";
        String[] runConfig = new String[] {
                Evolve.A_FILE, "src/formulaBuilding/test.params",
//				"-p", ("stat="+statisticType),
                "-p", ("stat.file=$"+pathToFiles+"out1.stat"),
                "-p", ("jobs="+numberOfJobs)
        };
        Evolve.main(runConfig);
    }
}
