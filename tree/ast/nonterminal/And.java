package tree.ast.nonterminal;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import tree.ast.BooleanExpression;
import tree.ast.NonTerminal;

public class And extends NonTerminal {
	public boolean interpret() {
		return left.interpret() && right.interpret();
	}

	@Override
	public BooleanExpression mutate(MersenneTwisterFast random) {
		if (random.nextBoolean()) {
			Or or = new Or();
			or.setLeft(left);
			or.setRight(right);
			return or;
		} else {

			if (random.nextBoolean()) {
				left = left.mutate(random);
			} else {
				right = right.mutate(random);
			}
			return this;
		}
	}

	@Override
	public BooleanExpression clone() {
		And result = new And();
		result.setLeft(left.clone());
		result.setRight(right.clone());
		return result;
	}

	public String toString() {
		return String.format("(%s & %s)", left, right);
	}
}
