package tree.ast.terminal;

import ec.util.MersenneTwisterFast;
import tree.Lexer;
import tree.ast.BooleanExpression;
import tree.ast.Terminal;
import tree.ast.nonterminal.Not;

public class Variable extends Terminal {
    private String name;

    public Variable(String name) {
        super();
        this.name = name;
    }

    public boolean interpret() {
        return value;
    }

    @Override
    public BooleanExpression mutate(MersenneTwisterFast random) {
        if (random.nextBoolean()) {
            return new Variable(Lexer.terminals[random.nextInt(Lexer.terminals.length)]);
        } else {
            Not not = new Not();
            not.setChild(this);
            return not;
        }
    }

    @Override
    public BooleanExpression clone() {
        return new Variable(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
