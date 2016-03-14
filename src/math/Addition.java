package math;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Addition implements MathExpression {

	protected List<MathExpression> terms;

	public Addition(MathExpression ... terms) {
		this(Arrays.asList(terms));
	}

	public Addition(List<MathExpression> terms) {
		this.terms = terms;
	}

	@Override
	public String nodeName() {
		return "+";
	}

	@Override
	public String toEquation() {
		StringJoiner output = new StringJoiner("+", "(", ")");
		for (MathExpression e : terms) {
			output.add(e.toEquation());
		}
		return output.toString();
	}

}
