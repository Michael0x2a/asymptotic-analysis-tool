package simplegrammar;

public interface AstNode {
    String nodeName();
    String toString();
    boolean isTerminal();

    <T> T accept(AstNodeVisitor<T> visitor);
}
