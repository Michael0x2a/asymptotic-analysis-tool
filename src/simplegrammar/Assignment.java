package simplegrammar;

public class Assignment implements AstNode {
    private String name;
    private AstNode value;

    public Assignment(String name, AstNode value) {
        this.name = notNull(name);
        this.value = notNull(value);
    }

    public String getName() {
        return this.name;
    }

    public AstNode getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "Assignment";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
}
