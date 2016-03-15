package math;

import java.util.List;

public interface MathExpression {
    String nodeName();
    String toEquation();

    default String removeExtraParens(String eq) {
        while (eq.startsWith("(") && eq.endsWith(")")) {
            eq = eq.substring(1, eq.length() - 1);
        }
        return eq;
    }

    default List<MathExpression> failOnEmpty(List<MathExpression> items) {
        if (items.size() == 0) {
            throw new IllegalArgumentException("Empty term");
        }
        return items;
    }

    <T> T accept(MathExpressionVisitor<T> visitor);
}
