package  automaton;

public class ECAlgorithm {
    public String[] name;
    public String[] text;

    public ECAlgorithm(String[] name, String[] text) {
        this.name = new String[name.length];
        this.text = new String[text.length];

        for (int i = 0; i < name.length; i++) {
            this.name[i] = name[i];
        }
        for (int i = 0; i < text.length; i++) {
            this.text[i] = text[i];
        }
    }
    public void printAlgorithm() {
        for (int i = 0; i<this.name.length; i++) {
            System.out.println("Name = " + this.name[i] + "  Text = " + this.text[i]);
        }

    }

}
