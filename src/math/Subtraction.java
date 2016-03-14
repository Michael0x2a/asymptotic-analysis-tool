package math;

import java.util.ArrayList;

public class Subtraction extends Addition {

	public Subtraction(MathExpression minuend, MathExpression subtrahend) {
		super(new ArrayList<>());
		terms.add(minuend);
		terms.add(new Negation(subtrahend));
	}

}
