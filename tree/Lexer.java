package tree;

import automaton.Automaton;

import java.io.*;

public class Lexer {
    private StreamTokenizer input;

    private int symbol = NONE;
    public static final int EOL = -3;
    public static final int EOF = -2;
    public static final int INVALID = -1;

    public static final int NONE = 0;

    public static final int OR = 1;
    public static final int AND = 2;
    public static final int NOT = 3;

    public static final int TRUE = 4;
    public static final int FALSE = 5;

    public static final int LEFT = 6;
    public static final int RIGHT = 7;

    public static final String TRUE_LITERAL = "true";
    public static final String FALSE_LITERAL = "false";

    public static final String[] terminals = new String[]{"c1Home", "c1End", "c2Home", "c2End", "vcHome", "vcEnd",
            "vac", "pp1", "pp2", "pp3", "c1Extend", "c1Retract", "c2Extend", "c2Retract", "vacuum_off",
            "vacuum_on", "vcExtend"};

    public static final int[] symbols = new int[]{8, 9, 10, 11, 12, 13,
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};

    public Lexer(InputStream in) {
        Reader r = new BufferedReader(new InputStreamReader(in));
        input = new StreamTokenizer(r);

        input.resetSyntax();
        input.wordChars('a', 'z');
        input.wordChars('A', 'Z');
        input.wordChars('0', '9');
        input.whitespaceChars('\u0000', ' ');
        input.whitespaceChars('\n', '\t');

        input.ordinaryChar('(');
        input.ordinaryChar(')');
        input.ordinaryChar('&');
        input.ordinaryChar('|');
        input.ordinaryChar('!');
    }

    public Lexer(String in) {
//        Reader r = new BufferedReader(new InputStreamReader(in));
        input = new StreamTokenizer(new StringReader(in));

        input.resetSyntax();
        input.wordChars('a', 'z');
        input.wordChars('A', 'Z');
        input.wordChars('0', '9');
        input.whitespaceChars('\u0000', ' ');
        input.whitespaceChars('\n', '\t');

        input.ordinaryChar('(');
        input.ordinaryChar(')');
        input.ordinaryChar('&');
        input.ordinaryChar('|');
        input.ordinaryChar('!');
    }

    public int nextSymbol() {
        try {
            switch (input.nextToken()) {
                case StreamTokenizer.TT_EOL:
                    symbol = EOL;
                    break;
                case StreamTokenizer.TT_EOF:
                    symbol = EOF;
                    break;
                case StreamTokenizer.TT_WORD: {
                    if (input.sval.equalsIgnoreCase(TRUE_LITERAL))  {
                        symbol = TRUE;
                    } else if (input.sval.equalsIgnoreCase(FALSE_LITERAL)) {
                        symbol = FALSE;
                    } else {
                        for (int i = 0; i < symbols.length; i++) {
                            if (input.sval.equalsIgnoreCase(terminals[i])) {
                                symbol = symbols[i];
                            }
                        }
                    }
                    break;
                }
                case '(':
                    symbol = LEFT;
                    break;
                case ')':
                    symbol = RIGHT;
                    break;
                case '&':
                    symbol = AND;
                    break;
                case '|':
                    symbol = OR;
                    break;
                case '!':
                    symbol = NOT;
                    break;
                default:
                    symbol = INVALID;
            }
        } catch (IOException e) {
            symbol = EOF;
        }

        return symbol;
    }

    public String toString() {
        return input.toString();
    }

    public static void main(String[] args) {
        String expression = "c1Home & ((true | false) & !(true & false))";
        Lexer l = new Lexer(new ByteArrayInputStream(expression.getBytes()));
        int s;
        while ( (s = l.nextSymbol()) != Lexer.EOF) if(s != EOL) System.out.printf("%s -> %s\n", l, s);
    }
}