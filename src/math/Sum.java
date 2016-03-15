package math;

public class Sum implements MathExpression {
	private MathExpression start;
	private MathExpression end;
	private MathExpression body;
	private Variable index;
	
	public Sum(MathExpression start, MathExpression end, MathExpression body, Variable index) {
		this.start = start;
		this.end = end;
		this.body = body;
		this.index = index;
	}

	@Override
	public String nodeName() {
		return "sum";
	}

	@Override
	public String toEquation() {
		return "(sum from " + index.toEquation() + " " + start.toEquation() + " to " + end.toEquation() + " of " + body.toEquation() + ")";
	}
}
