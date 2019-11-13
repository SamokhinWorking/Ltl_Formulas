package helpClass;

public class Algorithm {
    public String [] Name;
    public String [] Text;
    public Algorithm(String []name, String []text) {

        this.Name = new String[name.length];
        this.Text = new String[text.length];

        for (int i = 0; i < name.length; i++) {
            this.Name[i] = name[i];
        }
        for(int i=0;i<text.length;i++){
            this.Text[i]=text[i];
        }
    }
    public void printAlgorithm(){

        for(int i=0;i<this.Name.length;i++)
        {
            System.out.println("Name = "+this.Name[i] + "  Text = "+this.Text[i]);
        }

    }

}
