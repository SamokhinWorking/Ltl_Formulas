package automaton;

public class ECStates {
    public String[] name;
    public String[] algorithm;

    public ECStates(String[] name, String[] algorithm) {
        this.name = new String[name.length];
        this.algorithm = new String[algorithm.length];

        for (int i = 0; i < name.length; i++) {
            this.name[i] = name[i];
        }
        for (int i = 0; i < algorithm.length; i++) {
            this.algorithm[i] = algorithm[i];
        }
    }
    public void printECState() {
        for(int i = 0; i < this.name.length; i++) {
            System.out.println("Name= " + this.name[i]);
        }
        System.out.println();
        for(int i = 0; i < this.algorithm.length; i++) {
            System.out.println("Algorithm= " + this.algorithm[i]);
        }
    }

}
