package math;

public class Variable implements MathExpression {

	private String name;
	
	public Variable(String name) {
		this.name = name;
	}
	@Override
	public String nodeName() {
		return "variable";
	}
	@Override
	public String toEquation() {
		return name;
	}
	
}
