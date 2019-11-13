package helpClass;

public class EventInputs {

  public String [] Name;
  public String [] Comment;
   public EventInputs(String []name, String []comment) {

        this.Name = new String[name.length];
        this.Comment = new String[comment.length];

        for (int i = 0; i < name.length; i++) {
            this.Name[i] = name[i];
        }
        for(int i=0;i<comment.length;i++){
            this.Comment[i]=comment[i];
        }
    }
    public void printEventInputs(){

       for(int i=0;i<this.Name.length;i++)
       {
           System.out.println("Name= "+this.Name[i]);
       }

       for(int i=0;i<this.Comment.length;i++)
        {
            System.out.println("Comment= "+this.Comment[i]);
        }
    }


}
