package tree.ast;

import ec.util.MersenneTwisterFast;

/**
 * <expression>::=<term>{<or><term>}
 * <term>::=<factor>{<and><factor>}
 * <factor>::=<constant>|<not><factor>|(<expression>)
 * <constant>::= false|true
 * <or>::='|'
 * <and>::='&'
 * <not>::='!'
 */
public interface BooleanExpression {
	public boolean interpret();
	public BooleanExpression mutate(MersenneTwisterFast random);
	public BooleanExpression clone();
}
