package tree.ast.nonterminal;

import ec.util.MersenneTwisterFast;
import tree.ast.BooleanExpression;
import tree.ast.NonTerminal;;

public class Or extends NonTerminal {
	public boolean interpret() {
		return left.interpret() || right.interpret();
	}

	@Override
	public BooleanExpression mutate(MersenneTwisterFast random) {
		if (random.nextBoolean()) {
			And and = new And();
			and.setLeft(left);
			and.setRight(right);
			return and;
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
		Or result = new Or();
		result.setLeft(left.clone());
		result.setRight(right.clone());
		return result;
	}

	public String toString() {
		return String.format("(%s | %s)", left, right);
	}
}
