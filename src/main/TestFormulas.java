package main;
import ec.Evolve;
public class TestFormulas {
    public static void main(String[] args) {
        String pathToFiles = "src/main/";
        int numberOfJobs = 1;
        String[] runConfig = new String[] {
                Evolve.A_FILE, "src/params/ltlgen.params",
//				"-p", ("stat="+statisticType),
                "-p", ("stat.file=$"+pathToFiles+"out1.stat"),
                "-p", ("jobs="+numberOfJobs)
        };
        Evolve.main(runConfig);
    }
}