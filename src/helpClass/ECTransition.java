package helpClass;

public class ECTransition {
    public String [] Source;
    public String [] Destination;
    public String [] Condition;
    public ECTransition(String []source, String []destination, String []condition ) {

        this.Source = new String[source.length];
        this.Destination = new String[destination.length];
        this.Condition =new String[condition.length];

        for (int i = 0; i < source.length; i++) {
            this.Source[i] = source[i];
        }
        for(int i=0;i<destination.length;i++){
            this.Destination[i]=destination[i];
        }
        for(int i=0;i<condition.length;i++){
            this.Condition[i]=condition[i];
        }

    }
    public void printECTransition(){

        for(int i=0;i<this.Source.length;i++)
        {
            System.out.println("Source =  "+this.Source[i]+ "  Destinition = "+this.Destination[i]+
                    "  Condition = "+this.Condition[i]);
        }

    }

}
