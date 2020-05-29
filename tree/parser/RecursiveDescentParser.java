package tree.parser;

import tree.ast.*;
import tree.ast.nonterminal.And;
import tree.ast.nonterminal.Not;
import tree.ast.nonterminal.Or;
import tree.ast.terminal.False;
import tree.ast.terminal.True;
import tree.Lexer;
import tree.ast.terminal.Variable;

public class RecursiveDescentParser {
	private Lexer lexer;
	private int symbol;
	private BooleanExpression root;

	private final True t = new True();
	private final False f = new False();
	private final Variable[] variables = new Variable[Lexer.symbols.length];

	public RecursiveDescentParser(Lexer lexer) {
		this.lexer = lexer;
		for (int i = 0; i < Lexer.symbols.length; i++) {
			variables[i] = new Variable(Lexer.terminals[i]);
		}
	}

	public BooleanExpression build() {
		expression();
		return root;
	}

	private void expression() {
		term();
		while (symbol == Lexer.OR) {
			Or or = new Or();
			or.setLeft(root);
			term();
			or.setRight(root);
			root = or;
		}
	}

	private void term() {
		factor();
		while (symbol == Lexer.AND) {
			And and = new And();
			and.setLeft(root);
			factor();
			and.setRight(root);
			root = and;
		}
	}

	private void factor() {
		symbol = lexer.nextSymbol();
		if (symbol >= Lexer.symbols[0] && symbol <= Lexer.symbols[Lexer.symbols.length - 1]) {
			for (int i = 0; i < Lexer.symbols.length; i++) {
				if (symbol == Lexer.symbols[i]) {
					root = variables[i];
					symbol = lexer.nextSymbol();
					break;
				}
			}
		} else if (symbol == Lexer.TRUE) {
			root = t;
			symbol = lexer.nextSymbol();
		} else if (symbol == Lexer.FALSE) {
			root = f;
			symbol = lexer.nextSymbol();
		} else if (symbol == Lexer.NOT) {
			Not not = new Not();
			factor();
			not.setChild(root);
			root = not;
		} else if (symbol == Lexer.LEFT) {
			expression();
			symbol = lexer.nextSymbol(); // we don't care about ')'
		} else {
			throw new RuntimeException("Expression Malformed: " + symbol);
		}
	}
}
