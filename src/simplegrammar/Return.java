package simplegrammar;

public class Return implements AstNode {
    private AstNode value;

    public Return(AstNode value) {
        this.value = value;
    }

    public AstNode getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "Return";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitReturn(this);
    }
}
