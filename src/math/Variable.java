package math;

public class Variable implements MathExpression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String nodeName() {
        return "Variable";
    }
    @Override
    public String toEquation() {
        return name;
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }
    
    @Override
    public String toString() {
    	return name;
    }
}
