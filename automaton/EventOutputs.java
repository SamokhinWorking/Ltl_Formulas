package automaton;

public class EventOutputs {
    public String[] Name;

    public EventOutputs(String[] name) {
        this.Name = new String[name.length];

        for (int i = 0; i < name.length; i++) {
            this.Name[i] = name[i];
        }
    }
    public void printEventOutputs() {
        for(int i = 0; i < this.Name.length; i++) {
            System.out.println("Name= " + this.Name[i]);
        }
    }
}
