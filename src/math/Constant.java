package math;

public class Constant implements MathExpression {
    private int value;

    public Constant(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "Constant";
    }
    @Override
    public String toEquation() {
        return String.valueOf(value);
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitConstant(this);
    }

}
