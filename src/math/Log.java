package math;

public class Log implements MathExpression {

	private MathExpression expression;
	
	public Log(MathExpression e) {
		expression = e;
	}
	
	@Override
	public String nodeName() {
		// TODO Auto-generated method stub
		return "log";
	}

	@Override
	public String toEquation() {
		return "log(" + expression + ")";
	}

}
