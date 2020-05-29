package tree.ast.terminal;

import ec.util.MersenneTwisterFast;
import tree.ast.BooleanExpression;
import tree.ast.Terminal;

public class True extends Terminal {
	public True() {
		super(true);
	}

	public boolean interpret() {
		return value;
	}

	@Override
	public BooleanExpression clone() {
		return new True();
	}

	@Override
	public BooleanExpression mutate(MersenneTwisterFast random) {
		return new False();
	}
}
