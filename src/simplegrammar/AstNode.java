package simplegrammar;

public interface AstNode {
    String nodeName();
    boolean isTerminal();

    <T> T accept(AstNodeVisitor<T> visitor);

    default <T> T notNull(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        return item;
    }
}
