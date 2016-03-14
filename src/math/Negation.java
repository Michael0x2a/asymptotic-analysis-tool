package math;

import java.util.ArrayList;

public class Negation extends Multiplication {

	public Negation(MathExpression e) {
		super(new ArrayList<>());
		terms.add(new Constant(-1));
		terms.add(e);
	}

}
