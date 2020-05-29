package tree.ast.terminal;

import ec.util.MersenneTwisterFast;
import tree.ast.BooleanExpression;
import tree.ast.Terminal;

public class False extends Terminal {
	public False() {
		super(false);
	}

	public boolean interpret() {
		return value;
	}

	@Override
	public BooleanExpression clone() {
		return new False();
	}

	@Override
	public BooleanExpression mutate(MersenneTwisterFast random) {
		return new True();
	}
}
