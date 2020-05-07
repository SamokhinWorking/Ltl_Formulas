package automaton;

public class EventInputs {
    public String[] name;
    public EventInputs(String[] name) {
        this.name = new String[name.length];
        for (int i = 0; i < name.length; i++) {
            this.name[i] = name[i];
        }
    }
    public void printEventInputs() {
        for (int i = 0; i < this.name.length; i++) {
            System.out.println("Name= " + this.name[i]);
        }
    }


}
