package simplegrammar;

import java.util.List;

public interface Expression {
    String nodeName();
    String toString();
    boolean isTerminal();

    <T> T accept(ExpressionVisitor<T> visitor);
}
