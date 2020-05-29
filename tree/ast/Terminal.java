package tree.ast;

public abstract class Terminal implements BooleanExpression {
	protected boolean value;

	public Terminal() {

	}

	public Terminal(boolean value) {
		this.value = value;
	}

	public abstract BooleanExpression clone();

	public String toString() {
		return String.format("%s", value);
	}
}
