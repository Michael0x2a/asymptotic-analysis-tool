package math;

public class Constant implements MathExpression {

	private int value;
	
	public Constant(int value) {
		this.value = value;
	}
	@Override
	public String nodeName() {
		return "constant";
	}
	@Override
	public String toEquation() {
		return String.valueOf(value);
	}
	
}
