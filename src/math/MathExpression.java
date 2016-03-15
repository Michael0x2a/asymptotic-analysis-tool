package math;

public interface MathExpression {
    String nodeName();
    String toEquation();

    default String removeExtraParens(String eq) {
        while (eq.startsWith("(") && eq.endsWith(")")) {
            eq = eq.substring(1, eq.length() - 1);
        }
        return eq;
    }

    <T> T accept(MathExpressionVisitor<T> visitor);
}
