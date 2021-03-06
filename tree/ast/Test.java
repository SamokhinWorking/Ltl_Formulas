package tree.ast;

import tree.ast.nonterminal.And;
import tree.ast.nonterminal.Not;
import tree.ast.nonterminal.Or;
import tree.ast.terminal.False;
import tree.ast.terminal.True;

public class Test {
	public static void main(String[] args) {
		True t = new True();
		False f = new False();

		Or or = new Or();
		or.setLeft(t);
		or.setRight(f);

		Not not = new Not();
		not.setChild(f);
		And and = new And();
		and.setLeft(or);
		and.setRight(not);

		BooleanExpression root = and;

		System.out.println(root);
		System.out.println(root.interpret());
	}
}
