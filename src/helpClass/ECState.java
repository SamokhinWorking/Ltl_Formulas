package helpClass;

public class ECState {
    public String [] Name;
    public String [] Algorithm;
    public ECState(String []name, String []algorithm) {

        this.Name = new String[name.length];
        this.Algorithm = new String[algorithm.length];

        for (int i = 0; i < name.length; i++) {
            this.Name[i] = name[i];
        }
        for(int i=0;i<algorithm.length;i++){
            this.Algorithm[i]=algorithm[i];
        }
    }
    public void printECState(){

        for(int i=0;i<this.Name.length;i++)
        {
            System.out.println("Name= "+this.Name[i]);
        }
        System.out.println();
        for(int i=0;i<this.Algorithm.length;i++)
        {
            System.out.println("Algorithm= "+this.Algorithm[i]);
        }
    }

}
