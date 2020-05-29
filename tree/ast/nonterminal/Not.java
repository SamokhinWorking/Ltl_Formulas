package tree.ast.nonterminal;

import ec.util.MersenneTwisterFast;
import tree.ast.BooleanExpression;
import tree.ast.NonTerminal;

public class Not extends NonTerminal {
	public void setChild(BooleanExpression child) {
		setLeft(child);
	}

	public void setRight(BooleanExpression right) {
		throw new UnsupportedOperationException();
	}

	public boolean interpret() {
		return !left.interpret();
	}

	@Override
	public BooleanExpression mutate(MersenneTwisterFast random) {
		if (random.nextBoolean()) {
			return left;
		} else {
			left = left.mutate(random);
			return this;
		}
	}

	@Override
	public BooleanExpression clone() {
		Not result = new Not();
		result.setChild(left.clone());
		return result;
	}

	public String toString() {
		return String.format("!%s", left);
	}
}
