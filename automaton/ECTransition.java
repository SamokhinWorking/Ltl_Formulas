package automaton;

public class ECTransition {
    public String[] source;
    public String[] destination;
    public String[] condition;

    public ECTransition(String[] source, String[] destination, String[] condition) {
        this.source = new String[source.length];
        this.destination = new String[destination.length];
        this.condition = new String[condition.length];

        for (int i = 0; i < source.length; i++) {
            this.source[i] = source[i];
        }
        for (int i = 0; i < destination.length; i++) {
            this.destination[i] = destination[i];
        }
        for (int i = 0; i < condition.length; i++) {
            this.condition[i] = condition[i];
        }
    }

    public void printECTransition(){
        for(int i = 0; i < this.source.length; i++) {
            System.out.println("Source =  " + this.source[i] + "  Destination = " + this.destination[i] +
                    "  Condition = " + this.condition[i]);
        }
    }
}
