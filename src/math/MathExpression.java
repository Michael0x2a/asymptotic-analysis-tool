package math;

public interface MathExpression {
    String nodeName();
    String toEquation();

    <T> T accept(MathExpressionVisitor<T> visitor);
}
